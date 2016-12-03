# -*- coding: utf-8 -*-
from random import randint
from datetime import datetime
from flask import current_app, g
from itsdangerous import SignatureExpired
from app import db
from app.error import BadRequestError, InvalidArgumentError, NoSuchElementError, InvalidTokenError, \
    InsufficientPermissionError, ExpiredTokenError, ElementAlreadyExists, TemporarilyUnavailable
from app.models import User, Student, Teacher, Subject, DayOfWeek, CertificatePhoneNumber
from app.constant import ErrorCause, UserType
from app.dto.response import SignUpResult, IssueTokenResult
from app.utils import is_all_attributes_not_none, current_millis, sms


def sign_up(sign_up_request):
    user_type = sign_up_request.user_type
    if user_type is None or not (user_type == UserType.STUDENT or user_type == UserType.TEACHER):
        raise InvalidArgumentError(extra_message='User type is wrong.')
    email_credential = sign_up_request.email_credential
    student_basic_information = sign_up_request.student_basic_information
    student_lesson_information = sign_up_request.student_lesson_information
    teacher_basic_information = sign_up_request.teacher_basic_information
    teacher_lesson_information = sign_up_request.teacher_lesson_information

    if email_credential is None or not email_credential.is_valid_form():
        raise InvalidArgumentError(extra_message='Email credential is wrong.')
    user = User.query.filter_by(email=email_credential.email).first()
    if user is not None:
        raise ElementAlreadyExists(extra_message='Email is already existed.')
    user = User()
    user.email = email_credential.email
    user.password = email_credential.password
    user.profile_photos = sign_up_request.default_profile_photos
    current_time = current_millis()
    try:
        if user_type == UserType.STUDENT:
            if student_basic_information is None or not student_basic_information.is_valid_form():
                raise InvalidArgumentError(extra_message='Student basic information is wrong.')
            if student_lesson_information is None or not student_lesson_information.is_valid_form():
                raise InvalidArgumentError(extra_message='Student lesson information is wrong.')
            user.type = UserType.STUDENT
            user.name = student_basic_information.name
            user.gender = student_basic_information.gender
            user.phone_number = student_basic_information.phone_number
            user.birth_year = student_basic_information.birth_year
            location = student_basic_information.location
            user.zip_code = location.zip_code
            user.address = location.address
            user.sido = location.sido
            user.sigungu = location.sigungu
            user.bname = location.bname
            user.latitude = location.latitude
            user.longitude = location.longitude
            db.session.add(user)
            db.session.flush()

            student = Student()
            student.user_id = user.id
            student.student_status = student_basic_information.student_status
            student.grade = student_basic_information.grade
            student.department = student_basic_information.department
            student.available_subjects = []
            for subject_id in student_lesson_information.subjects:
                saved_subject = Subject.query.get(subject_id)
                if saved_subject is not None:
                    student.available_subjects.append(saved_subject)
            student.available_days_of_week = []
            for day_of_week in student_lesson_information.days_of_week:
                saved_day_of_week = DayOfWeek.query.filter_by(name=day_of_week).first()
                if saved_day_of_week is not None:
                    student.available_days_of_week.append(saved_day_of_week)
            student.class_available_count = student_lesson_information.class_available_count
            student.class_time = student_lesson_information.class_time
            student.preferred_gender = student_lesson_information.preferred_gender
            student.preferred_price = student_lesson_information.preferred_price
            student.class_type = student_lesson_information.class_type
            student.level = student_lesson_information.level
            student.description = student_lesson_information.description
            student.created_at = current_time
            db.session.add(student)
        elif user_type == UserType.TEACHER:
            if teacher_basic_information is None or not teacher_basic_information.is_valid_form():
                raise InvalidArgumentError(extra_message='Teacher basic information is wrong.')
            if teacher_lesson_information is None or not teacher_lesson_information.is_valid_form():
                raise InvalidArgumentError(extra_message='Teacher lesson information is wrong.')
            user.type = UserType.TEACHER
            user.name = teacher_basic_information.name
            user.gender = teacher_basic_information.gender
            user.phone_number = teacher_basic_information.phone_number
            user.birth_year = teacher_basic_information.birth_year
            location = teacher_basic_information.location
            user.zip_code = location.zip_code
            user.address = location.address
            user.sido = location.sido
            user.sigungu = location.sigungu
            user.bname = location.bname
            user.latitude = location.latitude
            user.longitude = location.longitude
            db.session.add(user)
            db.session.flush()

            teacher = Teacher()
            teacher.user_id = user.id
            teacher.university = teacher_basic_information.university
            teacher.university_rank = teacher_basic_information.university_rank
            teacher.university_status = teacher_basic_information.university_status
            teacher.major = teacher_basic_information.major
            teacher.available_subjects = []
            for subject_id in teacher_lesson_information.subjects:
                saved_subject = Subject.query.get(subject_id)
                if saved_subject is not None:
                    teacher.available_subjects.append(saved_subject)
            teacher.available_days_of_week = []
            for day_of_week in teacher_lesson_information.days_of_week:
                saved_day_of_week = DayOfWeek.query.filter_by(name=day_of_week).first()
                if saved_day_of_week is not None:
                    teacher.available_days_of_week.append(saved_day_of_week)
            teacher.class_available_count = teacher_lesson_information.class_available_count
            teacher.class_time = teacher_lesson_information.class_time
            teacher.preferred_gender = teacher_lesson_information.preferred_gender
            teacher.preferred_price = teacher_lesson_information.preferred_price
            teacher.career = teacher_lesson_information.career
            teacher.description = teacher_lesson_information.description
            teacher.created_at = current_time
            db.session.add(teacher)
    except Exception as error:
        db.session.rollback()
        raise error
    user.created_at = current_time
    user.coins = 10
    db.session.commit()
    token = user.generate_auth_token(current_app.config['AUTH_TOKEN_EXPIRES_IN'])
    return SignUpResult(user=user, token=token)


def issue_token(email_credential_req):
    if not is_all_attributes_not_none(email_credential_req):
        raise InvalidArgumentError()
    email = email_credential_req.email
    password = email_credential_req.password
    user = User.query.filter_by(email=email).first()
    if user is None:
        raise NoSuchElementError()
    if not user.verify_password(password):
        raise BadRequestError(cause=ErrorCause.PASSWORD_NOT_MATCHED, message='Password not matched.')
    token = user.generate_auth_token(current_app.config['AUTH_TOKEN_EXPIRES_IN'])
    return IssueTokenResult(user=user, token=token)


def check_permission(user_id=None):
    try:
        if not hasattr(g, 'auth_token') or g.auth_token is None:
            raise InvalidTokenError(extra_message='Token is empty.')
        data = User.verify_auth_token(g.auth_token)
        if user_id is not None:
            if data['id'] != user_id:
                raise InsufficientPermissionError(extra_message='Request is over allowed permission.')
        if data is None:
            raise InsufficientPermissionError(extra_message='Request is over allowed permission.')
        return data
    except SignatureExpired:
        raise ExpiredTokenError()
    except InvalidTokenError as ie:
        raise ie
    except:
        raise InsufficientPermissionError()


def request_auth_number(phone_number):
    """
    전화번호 인증을 위해 임의의 번호를 생성해 해당 전화번호로 문자를 보낸다.
    :param phone_number:
    :return:
    """
    if phone_number is None:
        raise InvalidArgumentError(extra_message='Phone number must not be null.')
    auth_number = randint(1000, 9999)
    # sql event scheduler 를 통해 expire 한다.
    certificate = CertificatePhoneNumber()
    certificate.phone_number = phone_number
    certificate.auth_number = auth_number
    certificate.created_at = current_millis()
    certificate.created_time = datetime.utcnow()
    db.session.add(certificate)

    # send SMS
    sms_options = sms.SMSOptions()
    sms_options.key = current_app.config['COOL_SMS_KEY']
    sms_options.secret = current_app.config['COOL_SMS_SECRET']
    sms_options.message = '매칭튜터 인증번호는 {0} 입니다.'.format(auth_number)
    sms_options.to = phone_number.replace('-', '')
    sms_options.sender = current_app.config['COOL_SMS_SENDER']
    status = sms.send_sms(sms_options)
    if not status:
        raise TemporarilyUnavailable()
    db.session.commit()
    return certificate


def certify_auth_number(phone_number, auth_number):
    """
    전화번호 인증을 위해 임의의 번호를 할당했는지 인증하는 과정
    :param phone_number:
    :param auth_number:
    :return:
    """
    if phone_number is None or auth_number is None:
        raise InvalidArgumentError(extra_message='Phone number or auth number must not be null.')
    certificate = CertificatePhoneNumber.query \
        .filter(CertificatePhoneNumber.phone_number == phone_number) \
        .filter(CertificatePhoneNumber.auth_number == auth_number).first()
    if certificate is None:
        raise BadRequestError(cause=ErrorCause.INVALID_CERTIFICATION, message='It is wrong certification.')
    if certificate.created_at + current_app.config['CERTIFY_PHONE_NUMBER_EXPIRES_IN'] < current_millis():
        raise BadRequestError(cause=ErrorCause.INVALID_CERTIFICATION, message='Certification is expired.')
    db.session.delete(certificate)
    db.session.commit()
    return
