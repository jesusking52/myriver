# -*- coding:utf8 -*-
from app.constant import UserType
from app.utils import ListUtils, StringUtils
from app.dto.common import Location


class EmailCredentialRequest:
    def __init__(self, request_json):
        self.email = request_json.get('email')
        self.password = request_json.get('password')

    def is_valid_form(self):
        if self.email is None \
                or self.password is None:
            return False
        return True


class StudentBasicInformationRequest:
    def __init__(self, request_json):
        self.type = request_json.get('type')
        self.name = request_json.get('name')
        self.gender = request_json.get('gender')
        self.phone_number = request_json.get('phone_number')
        self.birth_year = StringUtils.to_int(request_json.get('birth_year'))
        self.location = None
        if request_json.get('location') is not None:
            self.location = Location(request_json.get('location'))
        # 학교 상태 (유치원/초/중/고/재수/일반인)
        self.student_status = request_json.get('student_status')
        self.grade = StringUtils.to_int(request_json.get('grade'))
        # 계열 (문/이/예/실)
        self.department = request_json.get('department')

    def is_valid_form(self):
        if self.type is None \
                or self.name is None \
                or self.gender is None \
                or self.phone_number is None \
                or self.birth_year is None \
                or self.location is None \
                or self.location.zip_code is None \
                or self.location.address is None \
                or self.student_status is None \
                or self.department is None:
            return False
        return True


class StudentLessonInformationRequest:
    def __init__(self, request_json):
        self.subjects = request_json.get('subjects')
        self.days_of_week = request_json.get('days_of_week')
        self.class_available_count = StringUtils.to_int(request_json.get('class_available_count'))
        self.class_time = StringUtils.to_int(request_json.get('class_time'))
        self.preferred_gender = request_json.get('preferred_gender')
        self.preferred_price = StringUtils.to_int(request_json.get('preferred_price'))
        # 과외 형태
        self.class_type = request_json.get('class_type')
        # 학업수준
        self.level = request_json.get('level')
        self.description = request_json.get('description')

    def is_valid_form(self):
        if not ListUtils.is_valid_form(self.subjects) \
                or not ListUtils.is_valid_form(self.days_of_week) \
                or self.class_available_count is None \
                or self.class_time is None \
                or self.preferred_gender is None \
                or self.preferred_price is None \
                or self.class_type is None \
                or self.level is None:
            return False
        return True


class TeacherBasicInformationRequest:
    def __init__(self, request_json):
        self.type = request_json.get('type')
        self.name = request_json.get('name')
        self.gender = request_json.get('gender')
        self.phone_number = request_json.get('phone_number')
        self.birth_year = StringUtils.to_int(request_json.get('birth_year'))
        self.location = None
        if request_json.get('location') is not None:
            self.location = Location(request_json.get('location'))
        self.university = request_json.get('university')
        self.university_rank = request_json.get('university_rank')
        # 대학 상태(재학중/휴학중/졸업)
        self.university_status = request_json.get('university_status')
        self.major = request_json.get('major')

    def is_valid_form(self):
        if self.type is None \
                or self.name is None \
                or self.gender is None \
                or self.phone_number is None \
                or self.birth_year is None \
                or self.location is None \
                or self.location.zip_code is None \
                or self.location.address is None \
                or self.university is None \
                or self.university_rank is None \
                or self.university_status is None \
                or self.major is None:
            return False
        return True


class TeacherLessonInformationRequest:
    def __init__(self, request_json):
        self.subjects = request_json.get('subjects')
        self.days_of_week = request_json.get('days_of_week')
        self.class_available_count = StringUtils.to_int(request_json.get('class_available_count'))
        self.class_time = StringUtils.to_int(request_json.get('class_time'))
        self.preferred_gender = request_json.get('preferred_gender')
        self.preferred_price = StringUtils.to_int(request_json.get('preferred_price'))
        # 경력
        self.career = StringUtils.to_int(request_json.get('career'))
        self.description = request_json.get('description')

    def is_valid_form(self):
        if not ListUtils.is_valid_form(self.subjects) \
                or not ListUtils.is_valid_form(self.days_of_week) \
                or self.class_available_count is None \
                or self.class_time is None \
                or self.preferred_gender is None \
                or self.preferred_price is None \
                or self.career is None \
                or self.description is None:
            return False
        return True


class SignUpRequest:
    def __init__(self, request_json):
        self.user_type = request_json.get('type')
        self.email_credential = None
        self.student_basic_information = None
        self.student_lesson_information = None
        self.teacher_basic_information = None
        self.teacher_lesson_information = None
        if request_json.get('email_credential'):
            self.email_credential = EmailCredentialRequest(request_json.get('email_credential'))
        if self.user_type is not None:
            if self.user_type == UserType.STUDENT:
                if request_json.get('student_basic_information'):
                    self.student_basic_information = StudentBasicInformationRequest(
                            request_json.get('student_basic_information'))
                if request_json.get('student_lesson_information'):
                    self.student_lesson_information = StudentLessonInformationRequest(
                            request_json.get('student_lesson_information'))
            elif self.user_type == UserType.TEACHER:
                if request_json.get('teacher_basic_information'):
                    self.teacher_basic_information = TeacherBasicInformationRequest(
                            request_json.get('teacher_basic_information'))
                if request_json.get('teacher_lesson_information'):
                    self.teacher_lesson_information = TeacherLessonInformationRequest(
                            request_json.get('teacher_lesson_information'))


class UserPatchRequest:
    def __init__(self, request_json):
        self.user_type = request_json.get('type')
        self.student_basic_information = None
        self.student_lesson_information = None
        self.teacher_basic_information = None
        self.teacher_lesson_information = None
        if self.user_type is not None:
            if self.user_type == UserType.STUDENT:
                if request_json.get('student_basic_information'):
                    self.student_basic_information = StudentBasicInformationRequest(
                            request_json.get('student_basic_information'))
                if request_json.get('student_lesson_information'):
                    self.student_lesson_information = StudentLessonInformationRequest(
                            request_json.get('student_lesson_information'))
            elif self.user_type == UserType.TEACHER:
                if request_json.get('teacher_basic_information'):
                    self.teacher_basic_information = TeacherBasicInformationRequest(
                            request_json.get('teacher_basic_information'))
                if request_json.get('teacher_lesson_information'):
                    self.teacher_lesson_information = TeacherLessonInformationRequest(
                            request_json.get('teacher_lesson_information'))


class UserPreferencesRequest:
    def __init__(self, request_json):
        self.hide_on_searching = StringUtils.to_bool(request_json.get('hide_on_searching'))


class LessonBiddingRequest:
    def __init__(self, request_json):
        self.price = StringUtils.to_int(request_json.get('price'))


class UserGCMAddRequest:
    def __init__(self, request_json):
        self.key = request_json.get('key')


class CertifyPhoneNumberRequest:
    def __init__(self, request_json):
        self.phone_number = request_json.get('phone_number')
        self.auth_number = request_json.get('auth_number')


class SignInRequest:
    def __init__(self, request_json):
        self.email = request_json.get('email')
        self.password = request_json.get('password')

class UserPatchRequest:
    def __init__(self, request_json):
        self.user_type = request_json.get('type')
        self.student_basic_information = None
        self.student_lesson_information = None
        self.teacher_basic_information = None
        self.teacher_lesson_information = None
        if self.user_type is not None:
            if self.user_type == UserType.STUDENT:
                if request_json.get('student_basic_information'):
                    self.student_basic_information = StudentBasicInformationRequest(
                            request_json.get('student_basic_information'))
                if request_json.get('student_lesson_information'):
                    self.student_lesson_information = StudentLessonInformationRequest(
                            request_json.get('student_lesson_information'))
            elif self.user_type == UserType.TEACHER:
                if request_json.get('teacher_basic_information'):
                    self.teacher_basic_information = TeacherBasicInformationRequest(
                            request_json.get('teacher_basic_information'))
                if request_json.get('teacher_lesson_information'):
                    self.teacher_lesson_information = TeacherLessonInformationRequest(
                            request_json.get('teacher_lesson_information'))


class ReviewRequest:
    def __init__(self, request_json):
        self.rank = request_json.get('rank')
        self.review = request_json.get('review')
        self.teacher_id = request_json.get('teacherid')