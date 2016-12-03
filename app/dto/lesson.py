# -*- coding: utf-8 -*-
from flask import current_app
from app.utils import current_millis
from app.constant import LessonStatus
from app.dto.common import Location, SubjectDTO
from app.dto.user import UserDTO
from app.dto import sorted_days_of_week_dto


class LessonDTO:
    def __init__(self, lesson, **kwargs):
        self.id = lesson.id
        self.status = lesson.status
        self.created_at = lesson.created_at
        self.expires_in = 0
        self.expired_at = 0
        if lesson.status == LessonStatus.BIDDING:
            self.expires_in = lesson.created_at \
                              + current_app.config['LESSON_BIDDING_EXPIRES_IN'] \
                              - current_millis()
            self.expired_at = lesson.created_at \
                              + current_app.config['LESSON_BIDDING_EXPIRES_IN']
        elif lesson.status == LessonStatus.DEALING:
            self.expires_in = lesson.created_at \
                              + current_app.config['LESSON_BIDDING_EXPIRES_IN'] \
                              + current_app.config['LESSON_DEALING_EXPIRES_IN'] \
                              - current_millis()
            self.expired_at = lesson.created_at \
                              + current_app.config['LESSON_BIDDING_EXPIRES_IN'] \
                              + current_app.config['LESSON_DEALING_EXPIRES_IN']

        # 프로필 사진만 현재 유저의 사진을 내려준다.
        owner = lesson.owner
        self.profile_photos = owner.profile_photos
        self.email = lesson.email
        self.name = lesson.name
        self.gender = lesson.gender
        self.phone_number = lesson.phone_number
        self.birth_year = lesson.birth_year
        self.location = vars(Location({
            'address': lesson.address,
            'zip_code': lesson.zip_code,
            'sido': lesson.sido,
            'sigungu': lesson.sigungu,
            'bname': lesson.bname,
            'latitude': lesson.latitude,
            'longitude': lesson.longitude
        }))
        self.student_status = lesson.student_status
        self.grade = lesson.grade
        self.department = lesson.department
        self.level = lesson.level
        self.class_available_count = lesson.class_available_count
        self.class_time = lesson.class_time
        self.class_type = lesson.class_type
        self.preferred_gender = lesson.preferred_gender
        self.preferred_price = lesson.preferred_price
        self.description = lesson.description
        self.available_subjects = []
        for subject in lesson.available_subjects:
            self.available_subjects.append(vars(SubjectDTO(subject)))
        self.available_days_of_week = []
        for day_of_week in sorted_days_of_week_dto(lesson.available_days_of_week):
            self.available_days_of_week.append(day_of_week.name)

        self.is_favorited = False
        if kwargs.get('is_favorited') is not None and kwargs.get('is_favorited'):
            self.is_favorited = True
        self.is_bid = False
        if kwargs.get('is_bid') is not None and kwargs.get('is_bid'):
            self.is_bid = True
            if kwargs.get('users_bidding') is not None:
                self.bidding = vars(LessonBiddingDTO(kwargs.get('users_bidding')))
        self.biddings_count = kwargs.get('biddings_count')

        self.owner = vars(UserDTO(owner, is_checked_phone_number=kwargs.get('is_checked_owner_phone_number')))
        selected_user = lesson.selected_user
        if selected_user is not None:
            self.selected_user = vars(UserDTO(lesson.selected_user,
                                              is_checked_phone_number=kwargs.get(
                                                  'is_checked_selected_user_phone_number'
                                              )))


class LessonBiddingDTO:
    def __init__(self, lesson_bidding, **kwargs):
        self.price = lesson_bidding.price
        self.user = None
        user = lesson_bidding.user
        if kwargs.get('include_user') is not None and kwargs.get('include_user'):
            self.user = vars(UserDTO(user))
        self.lesson = None
        lesson = lesson_bidding.lesson
        if kwargs.get('include_lesson') is not None and kwargs.get('include_lesson'):
            self.lesson = vars(LessonDTO(lesson))
        self.is_selected = lesson_bidding.is_selected
        self.create_at = lesson_bidding.created_at


class LessonFavoriteDTO:
    def __init__(self, favorite, **kwargs):
        self.user_id = favorite.user_id
        if kwargs.get('include_user') is not None and kwargs.get('include_user'):
            self.user = vars(UserDTO(favorite.user))
        self.lesson_id = favorite.lesson_id
        if kwargs.get('include_lesson') is not None and kwargs.get('include_lesson'):
            self.lesson = vars(LessonDTO(favorite.lesson))
        self.created_at = favorite.created_at
