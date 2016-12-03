# -*- coding: utf-8 -*-
from datetime import datetime
from app import db
from app.models import Lesson, LessonBidding, LessonFavorite, User, CheckPhoneNumber
from app.error import InvalidTokenError, InvalidArgumentError, ElementAlreadyExists, NoSuchElementError, \
    InsufficientPermissionError, BadRequestError
from app.constant import LessonStatus, ErrorCause, UserType, TransactionType, CoinValueOfTransactionType, Gender
from app.utils import current_millis, StringUtils, ListUtils
from app.dto.common import Paging
from app.service.user_service import is_checked_phone_number
from app.service.notification_service import NotificationRequest, add_notification
from app.service.coin_service import add_coins, subtract_coins
from sqlalchemy.sql import or_


class GetLessonOptions:
    def __init__(self, **kwargs):
        self.gender = kwargs.get('gender')
        self.subjects = StringUtils.to_list(kwargs.get('subjects'))
        self.more_than_price = StringUtils.to_int(kwargs.get('more_than_price'))
        self.less_than_price = StringUtils.to_int(kwargs.get('less_than_price'))
        self.student_status = kwargs.get('student_status')
        self.grade = StringUtils.to_int(kwargs.get('grade'))
        self.max_zip_code = kwargs.get('max_zip_code')
        self.min_zip_code = kwargs.get('min_zip_code')
        self.statuses = kwargs.get('statuses')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetLessonBiddingOptions:
    def __init__(self, **kwargs):
        self.lesson_id = kwargs.get('lesson_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetLessonFavoriteOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


def get_lesson(lesson_id):
    lesson = Lesson.query.get(lesson_id)
    return lesson


def delete_lesson(user_id, lesson_id):
    lesson = Lesson.query.get(lesson_id)
    if lesson is None:
        raise NoSuchElementError(extra_message='Lesson does not exist.')
    owner = lesson.owner
    if owner is None:
        raise NoSuchElementError(extra_message='User does not exist.')
    if owner.id != user_id:
        raise InsufficientPermissionError()
    db.session.delete(lesson)
    db.session.commit()


def cancel_lesson(user_id, lesson_id):
    lesson = Lesson.query.get(lesson_id)
    if lesson is None:
        raise NoSuchElementError(extra_message='Lesson does not exist.')
    if lesson.owner.id != user_id:
        raise InsufficientPermissionError()
    lesson.status = LessonStatus.CANCELED
    db.session.commit()

    biddings = LessonBidding.query.filter(LessonBidding.lesson_id == lesson_id).all()
    for bidding in biddings:
        add_notification(NotificationRequest()
                         .to(bidding.user.id)
                         .on_lesson_canceled(lesson.owner.id, lesson.id))
        add_coins(bidding.user_id, CoinValueOfTransactionType.BIDDING, TransactionType.REFUND)
    return lesson


def get_lessons(options):
    query = Lesson.query
    if options.gender is not None:
        if options.gender != Gender.NONE:
            query = query.filter(Lesson.gender == options.gender)
    if not ListUtils.is_null_or_empty(options.subjects):
        for subject_id in options.subjects:
            query = query.filter(Lesson.available_subjects.any(id=subject_id))
    if options.more_than_price is not None:
        query = query.filter(options.more_than_price <= Lesson.preferred_price)
    if options.less_than_price is not None:
        query = query.filter(Lesson.preferred_price <= options.less_than_price)
    if options.student_status is not None:
        query = query.filter(Lesson.student_status == options.student_status)
    if options.grade is not None:
        query = query.filter(Lesson.grade == options.grade)
    if options.min_zip_code is not None:
        query = query.filter(options.min_zip_code <= Lesson.zip_code)
    if options.max_zip_code is not None:
        query = query.filter(Lesson.zip_code <= options.max_zip_code)
    if options.statuses is not None:
        conditions = []
        for status in options.statuses:
            conditions.append(Lesson.status == status)
        query = query.filter(or_(*conditions))

    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    lessons = query.order_by(Lesson.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return lessons, Paging(total_count=total_count, next_token=offset + per_page)


def create_lesson(user_id):
    if user_id is None:
        raise InvalidTokenError()
    user = User.query.get(user_id)
    if user.type != UserType.STUDENT:
        raise InvalidArgumentError(extra_message='It can be available only by student.')
    lesson = Lesson.query \
        .filter((Lesson.status == LessonStatus.BIDDING) | (Lesson.status == LessonStatus.DEALING)) \
        .filter(Lesson.owner_id == user_id).first()
    if lesson is not None:
        raise ElementAlreadyExists(extra_message='Student already has lesson.')
    student = user.student
    lesson = Lesson()
    lesson.owner_id = user.id
    lesson.status = LessonStatus.BIDDING
    current_time = current_millis()
    lesson.created_at = current_time
    lesson.created_time = datetime.utcnow()

    lesson.email = user.email
    lesson.name = user.name
    lesson.gender = user.gender
    lesson.phone_number = user.phone_number
    lesson.birth_year = user.birth_year
    lesson.zip_code = user.zip_code
    lesson.address = user.address
    lesson.sido = user.sido
    lesson.sigungu = user.sigungu
    lesson.bname = user.bname
    lesson.latitude = user.latitude
    lesson.longitude = user.longitude
    lesson.student_status = student.student_status
    lesson.grade = student.grade
    lesson.department = student.department
    lesson.level = student.level
    lesson.class_available_count = student.class_available_count
    lesson.class_time = student.class_time
    lesson.class_type = student.class_type
    lesson.preferred_gender = student.preferred_gender
    lesson.preferred_price = student.preferred_price
    lesson.description = student.description
    lesson.available_subjects = []
    for subject in student.available_subjects:
        lesson.available_subjects.append(subject)
    lesson.available_days_of_week = []
    for day_of_week in student.available_days_of_week:
        lesson.available_days_of_week.append(day_of_week)
    db.session.add(lesson)
    db.session.commit()
    add_notification(NotificationRequest()
                     .to(user_id)
                     .on_start_bidding(lesson.id))
    return lesson


def get_lesson_biddings_count(lesson_id):
    return LessonBidding.query.filter(LessonBidding.lesson_id == lesson_id).count()


def get_lesson_biddings(options):
    query = LessonBidding.query
    lesson_id = options.lesson_id
    query = query.filter(LessonBidding.lesson_id == lesson_id)
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    lesson_biddings = query.order_by(LessonBidding.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return lesson_biddings, Paging(total_count=total_count, next_token=offset + per_page)


def get_lesson_bidding(lesson_id, user_id):
    user = User.query.get(user_id)
    if user.type != UserType.TEACHER:
        return None
    lesson_bidding = LessonBidding.query \
        .filter(LessonBidding.lesson_id == lesson_id) \
        .filter(LessonBidding.user_id == user_id).first()
    return lesson_bidding


def add_lesson_bidding(lesson_id, user_id, bidding_request):
    lesson = Lesson.query.get(lesson_id)
    if lesson is None:
        raise NoSuchElementError(extra_message='Lesson is not existed.')
    if lesson.status != LessonStatus.BIDDING:
        raise BadRequestError(cause=ErrorCause.EXPIRED_LESSON_BIDDING, message='Lesson is expired.')
    user = User.query.get(user_id)
    if user.type != UserType.TEACHER:
        raise InvalidArgumentError(extra_message='It can be available only by teacher.')
    bidding = LessonBidding.query \
        .filter(LessonBidding.lesson_id == lesson_id) \
        .filter(LessonBidding.user_id == user.id).first()
    if bidding is not None:
        raise ElementAlreadyExists(extra_message='Already bidding.')
    bidding = LessonBidding()
    bidding.lesson_id = lesson.id
    bidding.user_id = user.id
    if bidding_request is None:
        raise InvalidArgumentError(extra_message='Bidding price must not be null.')
    price = StringUtils.to_int(bidding_request.price)
    if price is None:
        raise InvalidArgumentError(extra_message='Price is wrong.')
    bidding.price = price
    bidding.created_at = current_millis()
    db.session.add(bidding)
    db.session.commit()

    # 선생님은 입찰할 때마다 coin을 차감시킨다.
    subtract_coins(user_id, CoinValueOfTransactionType.BIDDING, TransactionType.BIDDING)
    add_notification(NotificationRequest()
                     .to(lesson.owner.id)
                     .on_bidding_lesson(user_id, lesson.id))
    return bidding


def remove_lesson_bidding(lesson_id, user_id):
    lesson_bidding = get_lesson_bidding(lesson_id, user_id)
    if lesson_bidding is not None:
        db.session.delete(lesson_bidding)
        db.session.commit()


def select_teacher(lesson_id, owner_id, user_id):
    lesson = Lesson.query.get(lesson_id)
    if lesson.status == LessonStatus.BIDDING \
            or lesson.status == LessonStatus.FINISHED:
        raise BadRequestError(cause=ErrorCause.EXPIRED_LESSON_DEALING, extra_message='Lesson status is not dealing.')
    owner = lesson.owner
    if owner.id != owner_id:
        raise InsufficientPermissionError()
    selected_user = User.query.get(user_id)
    if selected_user is None:
        raise NoSuchElementError(extra_message='The selected User does not exist.')
    lesson_bidding = LessonBidding.query \
        .filter(LessonBidding.lesson_id == lesson_id) \
        .filter(LessonBidding.user_id == user_id).first()
    if lesson_bidding is None:
        raise NoSuchElementError(extra_message='User does not bid.')
    # 학생의 coin 값 차감
    #subtract_coins(owner_id,
    #               max(int((lesson.preferred_price - lesson_bidding.price) * 3),
    #                   CoinValueOfTransactionType.MIN_SELECT_USER),
    #               TransactionType.SELECT_TEACHER)

    subtract_coins(owner_id, CoinValueOfTransactionType.MIN_SELECT_USER, TransactionType.SELECT_TEACHER)
    lesson.status = LessonStatus.FINISHED
    lesson.selected_user_id = selected_user.id
    lesson_bidding.is_selected = True
    if not is_checked_phone_number(owner_id, user_id):
        check = CheckPhoneNumber()
        check.user_id = owner_id
        check.checked_user_id = user_id
        check.created_at = current_millis()
        db.session.add(check)
    if not is_checked_phone_number(user_id, owner_id):
        check = CheckPhoneNumber()
        check.user_id = user_id
        check.checked_user_id = owner_id
        check.created_at = current_millis()
        db.session.add(check)
    db.session.commit()

    # 선택되지 않은 teacher들 coin 값 반환
    unselected_biddings = LessonBidding.query \
        .filter(LessonBidding.lesson_id == lesson_id) \
        .filter(LessonBidding.user_id != user_id).all()
    for bidding in unselected_biddings:
        add_coins(bidding.user_id, CoinValueOfTransactionType.BIDDING, TransactionType.REFUND_FOR_UNSELECTED)
    add_notification(NotificationRequest()
                     .to(selected_user.id)
                     .on_user_selected(selected_user.id, lesson.id))
    return selected_user


def get_lesson_favorite(lesson_id, user_id):
    lesson_favorite = LessonFavorite.query \
        .filter(LessonFavorite.lesson_id == lesson_id) \
        .filter(LessonFavorite.user_id == user_id).first()
    return lesson_favorite


def get_favorites(options):
    query = LessonFavorite.query.filter(LessonFavorite.user_id == options.user_id)
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    favorites = query.order_by(LessonFavorite.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return favorites, Paging(total_count=total_count, next_token=offset + per_page)


def add_lesson_favorite(lesson_id, user_id):
    lesson_favorite = LessonFavorite.query \
        .filter(LessonFavorite.lesson_id == lesson_id) \
        .filter(LessonFavorite.user_id == user_id).first()
    if lesson_favorite is not None:
        raise ElementAlreadyExists(extra_message='Lesson Favorite already exists.')
    lesson = Lesson.query.get(lesson_id)
    lesson_favorite = LessonFavorite()
    lesson_favorite.lesson_id = lesson_id
    lesson_favorite.user_id = user_id
    lesson_favorite.created_at = current_millis()
    db.session.add(lesson_favorite)
    add_notification(NotificationRequest()
                     .to(lesson.owner_id)
                     .on_lesson_favorited(lesson_id, user_id))
    db.session.commit()
    return lesson_favorite


def remove_lesson_favorite(lesson_id, user_id):
    favorite = LessonFavorite.query \
        .filter(LessonFavorite.lesson_id == lesson_id) \
        .filter(LessonFavorite.user_id == user_id).first()
    if favorite is not None:
        db.session.delete(favorite)
        db.session.commit()
