# -*- coding: utf-8 -*-
from app import db
from app.models import User, Teacher, Subject, DayOfWeek, UserFavorite, Lesson, LessonBidding, Notification, UserGCM, Review, \
    CheckPhoneNumber, CoinTransaction
from app.constant import UserType, TransactionType, CoinValueOfTransactionType, StudentStatus, Gender
from app.error import NoSuchElementError, InvalidArgumentError, ElementAlreadyExists, BadRequestError
from app.utils import StringUtils, ListUtils, current_millis
from app.service.notification_service import NotificationRequest, add_notification
from app.service.coin_service import subtract_coins
from app.dto.common import Paging
from sqlalchemy.sql import or_


class GetUserOptions:
    def __init__(self, **kwargs):
        self.email_pattern = kwargs.get('email_pattern')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetTeacherOptions:
    def __init__(self, **kwargs):
        self.universities = StringUtils.to_list(kwargs.get('universities'))
        self.major = kwargs.get('major')
        self.gender = kwargs.get('gender')
        self.subjects = StringUtils.to_list(kwargs.get('subjects'))
        self.more_than_price = StringUtils.to_int(kwargs.get('more_than_price'))
        self.less_than_price = StringUtils.to_int(kwargs.get('less_than_price'))
        self.max_zip_code = kwargs.get('max_zip_code')
        self.min_zip_code = kwargs.get('min_zip_code')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetUserFavoriteOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetUserLessonOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.statuses = kwargs.get('statuses')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetNotificationOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


class GetUserCoinTransactionOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


def get_user(user_id):
    user = User.query.get(user_id)
    if user is None:
        raise NoSuchElementError(id=user_id)
    return user


def get_users(options):
    query = User.query
    # admin에서 해당 email로 필터링 하기 위해 (특수문자는 제외)
    if options.email_pattern is not None:
        query = query.filter(User.email.contains(options.email_pattern))
    offset = 0
    if options.next_token:
        offset = options.next_token
    per_page = 0
    if options.per_page:
        per_page = options.per_page
    users = query.order_by(User.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return users, Paging(total_count=total_count, next_token=offset + per_page)


def get_user_transactions(options):
    query = CoinTransaction.query.filter(CoinTransaction.user_id == options.user_id)
    offset = 0
    if options.next_token:
        offset = options.next_token
    per_page = 0
    if options.per_page:
        per_page = options.per_page
    transactions = query.offset(offset).limit(per_page).all()
    total_count = query.count()
    return transactions, Paging(total_count=total_count, next_token=offset + per_page)


def patch_user(user_id, patch_request):
    user = User.query.get(user_id)
    try:
        if patch_request.user_type == UserType.STUDENT:
            student = user.student
            if student is None:
                raise InvalidArgumentError(extra_message='User is not Student.')
            student_basic_information = patch_request.student_basic_information
            if student_basic_information is not None:
                if student_basic_information.name is not None:
                    user.name = student_basic_information.name
                if student_basic_information.gender is not None:
                    user.gender = student_basic_information.gender
                if student_basic_information.phone_number is not None:
                    user.phone_number = student_basic_information.phone_number
                if student_basic_information.birth_year is not None:
                    user.birth_year = student_basic_information.birth_year
                if student_basic_information.location is not None:
                    location = student_basic_information.location
                    user.zip_code = location.zip_code
                    user.address = location.address
                    user.sido = location.sido
                    user.sigungu = location.sigungu
                    user.bname = location.bname
                    user.latitude = location.latitude
                    user.longitude = location.longitude
                if student_basic_information.student_status is not None:
                    # 유치원, 재수, 일반인은 학년이 없기 때문에 필드를 없애줘야 합니다.
                    remove_student_grade = [StudentStatus.KINDERGARTEN,
                                            StudentStatus.RETRY_UNIVERSITY,
                                            StudentStatus.ORDINARY]
                    student_status = student_basic_information.student_status
                    if student_status in remove_student_grade:
                        student.grade = None
                    student.student_status = student_basic_information.student_status
                if student_basic_information.grade is not None:
                    student.grade = student_basic_information.grade
                if student_basic_information.department is not None:
                    student.department = student_basic_information.department
            student_lesson_information = patch_request.student_lesson_information
            if student_lesson_information is not None:
                if not ListUtils.is_null_or_empty(student_lesson_information.subjects):
                    student.available_subjects[:] = []
                    for subject_id in student_lesson_information.subjects:
                        saved_subject = Subject.query.get(subject_id)
                        if saved_subject is not None:
                            student.available_subjects.append(saved_subject)
                if not ListUtils.is_null_or_empty(student_lesson_information.days_of_week):
                    student.available_days_of_week[:] = []
                    for day_of_week in student_lesson_information.days_of_week:
                        saved_day_of_week = DayOfWeek.query.filter_by(name=day_of_week).first()
                        if saved_day_of_week is not None:
                            student.available_days_of_week.append(saved_day_of_week)
                if student_lesson_information.class_available_count is not None:
                    student.class_available_count = student_lesson_information.class_available_count
                if student_lesson_information.class_time is not None:
                    student.class_time = student_lesson_information.class_time
                if student_lesson_information.preferred_gender is not None:
                    student.preferred_gender = student_lesson_information.preferred_gender
                if student_lesson_information.preferred_price is not None:
                    student.preferred_price = student_lesson_information.preferred_price
                if student_lesson_information.class_type is not None:
                    student.class_type = student_lesson_information.class_type
                if student_lesson_information.level is not None:
                    student.level = student_lesson_information.level
                if student_lesson_information.description is not None:
                    student.description = student_lesson_information.description
        elif patch_request.user_type == UserType.TEACHER:
            teacher = user.teacher
            if teacher is None:
                raise InvalidArgumentError(extra_message='User is not Teacher.')
            teacher_basic_information = patch_request.teacher_basic_information
            if teacher_basic_information is not None:
                if teacher_basic_information.name is not None:
                    user.name = teacher_basic_information.name
                if teacher_basic_information.gender is not None:
                    user.gender = teacher_basic_information.gender
                if teacher_basic_information.phone_number is not None:
                    user.phone_number = teacher_basic_information.phone_number
                if teacher_basic_information.birth_year is not None:
                    user.birth_year = teacher_basic_information.birth_year
                if teacher_basic_information.location is not None:
                    location = teacher_basic_information.location
                    user.zip_code = location.zip_code
                    user.address = location.address
                    user.sido = location.sido
                    user.sigungu = location.sigungu
                    user.bname = location.bname
                    user.latitude = location.latitude
                    user.longitude = location.longitude
                if teacher_basic_information.university is not None:
                    teacher.university = teacher_basic_information.university
                if teacher_basic_information.university_rank is not None:
                    teacher.university_rank = teacher_basic_information.university_rank
                if teacher_basic_information.university_status is not None:
                    teacher.university_status = teacher_basic_information.university_status
                if teacher_basic_information.major is not None:
                    teacher.major = teacher_basic_information.major
            teacher_lesson_information = patch_request.teacher_lesson_information
            if teacher_lesson_information is not None:
                if not ListUtils.is_null_or_empty(teacher_lesson_information.subjects):
                    teacher.available_subjects[:] = []
                    for subject_id in teacher_lesson_information.subjects:
                        saved_subject = Subject.query.get(subject_id)
                        if saved_subject is not None:
                            teacher.available_subjects.append(saved_subject)
                if not ListUtils.is_null_or_empty(teacher_lesson_information.days_of_week):
                    teacher.available_days_of_week[:] = []
                    for day_of_week in teacher_lesson_information.days_of_week:
                        saved_day_of_week = DayOfWeek.query.filter_by(name=day_of_week).first()
                        if saved_day_of_week is not None:
                            teacher.available_days_of_week.append(saved_day_of_week)
                if teacher_lesson_information.class_available_count is not None:
                    teacher.class_available_count = teacher_lesson_information.class_available_count
                if teacher_lesson_information.class_time is not None:
                    teacher.class_time = teacher_lesson_information.class_time
                if teacher_lesson_information.preferred_gender is not None:
                    teacher.preferred_gender = teacher_lesson_information.preferred_gender
                if teacher_lesson_information.preferred_price is not None:
                    teacher.preferred_price = teacher_lesson_information.preferred_price
                if teacher_lesson_information.career is not None:
                    teacher.career = teacher_lesson_information.career
                if teacher_lesson_information.description is not None:
                    teacher.description = teacher_lesson_information.description
    except Exception as error:
        db.session.rollback()
        raise error
    db.session.commit()
    return user


def get_teachers(user_id, options):
    query = User.query.join(Teacher)
    user = User.query.get(user_id)
    if options.universities is not None:
        query = query.filter(or_(Teacher.university == university_name for university_name in options.universities))
    if options.major is not None:
        query = query.filter(Teacher.major == options.major)
    if options.more_than_price is not None:
        query = query.filter(options.more_than_price < Teacher.preferred_price)
    if options.less_than_price is not None:
        query = query.filter(Teacher.preferred_price < options.less_than_price)
    if not ListUtils.is_null_or_empty(options.subjects):
        for subject_id in options.subjects:
            query = query.filter(Teacher.available_subjects.any(id=subject_id))
    if options.gender is not None:
        if options.gender != Gender.NONE:
            query = query.filter(User.gender == options.gender)
    if options.min_zip_code is not None:
        query = query.filter(options.min_zip_code <= User.zip_code)
    if options.max_zip_code is not None:
        query = query.filter(User.zip_code <= options.max_zip_code)
    query = query.filter(Teacher.hide_on_searching == False)

    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    users = query. \
        order_by(calculate_geodistance(user.latitude, user.longitude, User.latitude, User.longitude).asc()). \
        offset(offset).limit(per_page).all()
    #users = query. \
    #    order_by(Teacher.university_rank.asc()). \
    #    order_by(calculate_geodistance(user.latitude, user.longitude, User.latitude, User.longitude).asc()). \
    #    offset(offset).limit(per_page).all()
    #users = query.order_by(Teacher.university_rank.asc()).offset(offset).limit(per_page).all()
    #users = query.order_by(User.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return users, Paging(total_count=total_count, next_token=offset + per_page)


# Calculate sudo geo-distance between two points (Squared distance)
def calculate_geodistance(lat1, lon1, lat2, lon2):
    return (lat1-lat2)*(lat1-lat2)+(lon1-lon2)*(lon1-lon2)


def set_profile_photos(user_id, images):
    user = User.query.get(user_id)
    if ListUtils.is_null_or_empty(images):
        return user
    if user is None:
        raise NoSuchElementError(id=user_id)
    user.profile_photos = images
    db.session.commit()
    return user


def get_lessons(options):
    user_id = options.user_id
    user = User.query.get(user_id)
    if user.type == UserType.TEACHER:
        query = Lesson.query.join(LessonBidding).filter(LessonBidding.user_id == user.id)
    elif user.type == UserType.STUDENT:
        query = Lesson.query.filter(Lesson.owner_id == user.id)
    if options.statuses is not None:
        query = query.filter(or_(Lesson.status == status for status in options.statuses))
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    lessons = query.order_by(Lesson.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return lessons, Paging(total_count=total_count, next_token=offset + per_page)


def get_biddings(options):
    user_id = options.user_id
    user = User.query.get(user_id)
    if user.type != UserType.TEACHER:
        raise BadRequestError(message='User is not teacher.')
    query = LessonBidding.query.filter(LessonBidding.user_id == user.id)
    query = query.join(Lesson)
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
    biddings = query.order_by(LessonBidding.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return biddings, Paging(total_count=total_count, next_token=offset + per_page)


def get_favorites(options):
    query = UserFavorite.query.filter(UserFavorite.from_user_id == options.user_id)
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    favorites = query.order_by(UserFavorite.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return favorites, Paging(total_count=total_count, next_token=offset + per_page)


def get_favorite(from_user_id, to_user_id):
    user_favorite = UserFavorite.query \
        .filter(UserFavorite.from_user_id == from_user_id) \
        .filter(UserFavorite.to_user_id == to_user_id).first()
    return user_favorite


def add_favorite(from_user_id, to_user_id):
    favorite = get_favorite(from_user_id, to_user_id)
    if favorite is not None:
        raise ElementAlreadyExists(extra_message='Already favorites.')
    if User.query.get(from_user_id) is None or User.query.get(to_user_id) is None:
        raise NoSuchElementError(extra_message='User does not exist.')
    favorite = UserFavorite()
    favorite.from_user_id = from_user_id
    favorite.to_user_id = to_user_id
    favorite.created_at = current_millis()
    db.session.add(favorite)
    add_notification(NotificationRequest()
                     .to(to_user_id)
                     .on_user_favorited(to_user_id, from_user_id))
    db.session.commit()
    return favorite


def remove_favorite(from_user_id, to_user_id):
    favorite = get_favorite(from_user_id, to_user_id)
    if favorite is not None:
        db.session.delete(favorite)
        db.session.commit()


def get_notifications(options):
    query = Notification.query.filter(Notification.user_id == options.user_id)
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    limit = 0
    if options.per_page is not None:
        limit = options.per_page
    notifications = query.order_by(Notification.created_at.desc()).offset(offset).limit(limit).all()
    total_count = query.count()
    return notifications, Paging(total_count=total_count, next_token=offset + limit)


def add_gcm(user_id, key):
    if key is None:
        raise InvalidArgumentError(extra_message='GCM key must not be null.')
    gcm = UserGCM.query\
        .filter(UserGCM.user_id == user_id)\
        .filter(UserGCM.key == key).first()
    if gcm is not None:
        return gcm
    gcm = UserGCM()
    gcm.user_id = user_id
    gcm.key = key
    gcm.created_at = current_millis()
    db.session.add(gcm)
    db.session.commit()
    return gcm


def get_check_phone_number(user_id, checked_user_id):
    return CheckPhoneNumber.query \
        .filter(CheckPhoneNumber.user_id == user_id) \
        .filter(CheckPhoneNumber.checked_user_id == checked_user_id).first()


def is_checked_phone_number(user_id, checked_user_id):
    if user_id is None or checked_user_id is None:
        return False
    if user_id == checked_user_id:
        return True
    if get_check_phone_number(user_id, checked_user_id) is not None:
        return True
    return False


def check_phone_number(user_id, checked_user_id):
    user = User.query.get(user_id)
    checked_user = User.query.get(checked_user_id)
    if user is None or checked_user is None:
        raise NoSuchElementError(extra_message='User does not exist.')
    check = get_check_phone_number(user_id, checked_user_id)
    if check is not None:
        return checked_user

    # 전화번호를 확인하면 coin을 차감한다.
    subtract_coins(user_id, CoinValueOfTransactionType.CHECK_USER_PHONE_NUMBER, TransactionType.CHECK_USER_PHONE_NUMBER)
    check = CheckPhoneNumber()
    check.user_id = user_id
    check.checked_user_id = checked_user_id
    check.created_at = current_millis()
    db.session.add(check)
    db.session.commit()
    return checked_user


def set_preferences(user_id, preferences):
    user = User.query.get(user_id)
    if preferences.hide_on_searching is not None:
        user.teacher.hide_on_searching = preferences.hide_on_searching
    db.session.commit()
    return user

# 이거 뭔지 모르겠다....
def writeReview(user_id, review):
    user = Review.query.get(user_id)
    if review.rank is not None:
        user.review.rank = review.rank
    if review.review is not None:
        user.review.review = review.review
    if review.teacher_id is not None:
        user.review.teacher_id = review.teacher_id
    if review.created_at is not None:
        user.review.created_at = review.created_at
    if review.updated_at is not None:
        user.review.updated_at = review.updated_at
    db.session.commit()
    return user
