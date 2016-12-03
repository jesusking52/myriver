# -*- coding: utf-8 -*-
from app.dto import sorted_days_of_week_dto
from app.dto.common import Location, SubjectDTO


class UserDTO:
    def __init__(self, user, **kwargs):
        self.id = user.id
        self.type = user.type
        self.email = user.email
        self.name = user.name
        self.gender = user.gender
        self.is_checked_phone_number = False
        if kwargs.get('is_checked_phone_number') is not None and kwargs.get('is_checked_phone_number'):
            self.is_checked_phone_number = kwargs.get('is_checked_phone_number')
            self.phone_number = user.phone_number
        self.birth_year = user.birth_year
        self.profile_photos = user.profile_photos
        self.location = vars(Location({
            'address': user.address,
            'zip_code': user.zip_code,
            'sido': user.sido,
            'sigungu': user.sigungu,
            'bname': user.bname,
            'latitude': user.latitude,
            'longitude': user.longitude
        }))
        if kwargs.get('is_favorited') is not None:
            self.is_favorited = kwargs.get('is_favorited')
        if user.student is not None:
            self.student = vars(StudentDTO(user.student))
        if user.teacher is not None:
            self.teacher = vars(TeacherDTO(user.teacher))
        self.coins = user.coins


class StudentDTO:
    def __init__(self, student):
        self.student_status = student.student_status
        self.grade = student.grade
        self.department = student.department
        self.level = student.level
        self.class_available_count = student.class_available_count
        self.class_time = student.class_time
        self.class_type = student.class_type
        self.preferred_gender = student.preferred_gender
        self.preferred_price = student.preferred_price
        self.description = student.description
        self.available_subjects = []
        for subject in student.available_subjects:
            self.available_subjects.append(vars(SubjectDTO(subject)))
        self.available_days_of_week = []
        for day_of_week in sorted_days_of_week_dto(student.available_days_of_week):
            self.available_days_of_week.append(day_of_week.name)


class TeacherDTO:
    def __init__(self, teacher):
        self.university = teacher.university
        self.university_rank = teacher.university_rank
        self.university_status = teacher.university_status
        self.major = teacher.major
        self.career = teacher.career
        self.class_available_count = teacher.class_available_count
        self.class_time = teacher.class_time
        self.preferred_gender = teacher.preferred_gender
        self.preferred_price = teacher.preferred_price
        self.description = teacher.description
        self.available_subjects = []
        for subject in teacher.available_subjects:
            self.available_subjects.append(vars(SubjectDTO(subject)))
        self.available_days_of_week = []
        for day_of_week in sorted_days_of_week_dto(teacher.available_days_of_week):
            self.available_days_of_week.append(day_of_week.name)
        self.hide_on_searching = teacher.hide_on_searching


class UserFavoriteDTO:
    def __init__(self, favorite, **kwargs):
        self.from_user_id = favorite.from_user_id
        if kwargs.get('include_from_user') is not None and kwargs.get('include_from_user'):
            self.from_user = vars(UserDTO(favorite.from_user))
        self.to_user_id = favorite.to_user_id
        if kwargs.get('include_to_user') is not None and kwargs.get('include_to_user'):
            self.to_user = vars(UserDTO(favorite.to_user))
        self.created_at = favorite.created_at


class CoinTransactionDTO:
    def __init__(self, transaction):
        self.user_id = transaction.user_id
        self.value = transaction.value
        self.transaction_type = transaction.transaction_type
        self.created_at = transaction.created_at

class ReviewDTO:
    def __init__(self, user, **kwargs):
        self.user_id = user.user_id
        if kwargs.get('rank') is not None:
            self.rank = kwargs.get('rank')
        if kwargs.get('review') is not None:
            self.review = kwargs.get('review')
        if kwargs.get('teacherid') is not None:
            self.teacherid = kwargs.get('teacherid')
