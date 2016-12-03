# -*- coding: utf-8 -*-
from app import db
from app.constant import SubjectGroup as C_SubjectGroup, DayOfWeek as C_DayOfWeek
from app.models import SubjectGroup, Subject, DayOfWeek, User, ZipCode
from app.service import notification_service


def init_subjects():
    for sg in list(C_SubjectGroup):
        saved_subject_group = SubjectGroup.query.filter_by(type=sg.name).first()
        if saved_subject_group is None:
            add_subject_group = SubjectGroup()
            add_subject_group.type = sg.name
            add_subject_group.name = sg.value
            db.session.add(add_subject_group)
            db.session.flush()
            saved_subject_group = add_subject_group
        for subj in C_SubjectGroup.get_subjects(saved_subject_group.type):
            saved_subject = Subject.query.filter_by(type=subj.name).first()
            if saved_subject is None:
                add_subject = Subject()
                add_subject.type = subj.name
                add_subject.name = subj.value
                add_subject.subject_group_id = saved_subject_group.id
                db.session.add(add_subject)
    db.session.commit()


def init_days_of_week():
    for day_of_week in list(C_DayOfWeek):
        saved_day_of_week = DayOfWeek.query.filter_by(name=day_of_week.name).first()
        if saved_day_of_week is None:
            add = DayOfWeek()
            add.name = day_of_week.name
            add.value = day_of_week.value
            db.session.add(add)
    db.session.commit()


def init_zip_code():
    try:
        ZipCode.query.delete()
        import openpyxl
        file_path = '20160406_zip_code_sido_sigungu.xlsx'
        workbook = openpyxl.load_workbook(file_path, use_iterators=True)
        sheet_names = workbook.get_sheet_names()
        if len(sheet_names) == 0:
            return
        sheet = workbook.get_sheet_by_name(name=sheet_names[0])
        idx = 0
        for row in sheet.rows:
            if idx == 0:
                idx += 1
                continue
            zip_code = ZipCode()
            zip_code.code = int(row[0].value)
            zip_code.sub_code = int(zip_code.code / 1000)
            zip_code.sido = row[1].value
            zip_code.sigungu = row[2].value
            db.session.add(zip_code)
        db.session.commit()
    except Exception as e:
        print(e)
    return


def get_zip_code(code):
    if code is None:
        return None
    try:
        sub_code = int(code / 1000)
        zip_code = ZipCode.query.filter(ZipCode.sub_code == sub_code).filter(ZipCode.code == code).first()
        return zip_code
    except:
        return None


import random
from flask import current_app
from app.models import Lesson
from app.service import auth_service, lesson_service
from app.constant import UserType, Gender, StudentStatus, Department, ClassType, ClassLevel, UniversityStatus
from app.dto.request import SignUpRequest, LessonBiddingRequest


def init_admin():
    email = 'admin'
    password = 'aocldxbxj'  # 매칭튜터
    user = User()
    user.email = email
    user.is_admin = True
    user.password = password
    db.session.add(user)
    db.session.commit()


def init_fake_users():
    days_of_week = []
    for attr in dir(C_DayOfWeek):
        if attr.startswith('__'):
            continue
        days_of_week.append(attr)

    subjects = Subject.query.all()

    locations = [
        {
            'zip_code': 5668,
            'address': '서울시 송파구 송파동 93-5',
            'sido': '서울',
            'sigungu': '송파구',
            'bname': '송파동',
            'latitude': 37.5040847,
            'longitude': 127.1024561
        },
        {
            'zip_code': 6164,
            'address': '서울시 강남구 영동대로 513 코엑스',
            'sido': '서울',
            'sigungu': '강남구',
            'bname': '삼성동',
            'latitude': 37.5123933,
            'longitude': 127.0567587
        },
        {

            'zip_code': 10390,
            'address': '경기도 고양시 일산서구 한류월드로 408 킨텍스',
            'latitude': 37.6674057,
            'sido': '경기',
            'sigungu': '고양시 일산서구',
            'bname': '대화동',
            'longitude': 126.7418795
        },
        {
            'zip_code': 48060,
            'address': '부산광역시 해운대구 APEC로 55 벡스코',
            'sido': '부산',
            'sigungu': '해운대구',
            'bname': '우동',
            'latitude': 35.168981,
            'longitude': 129.1338524
        }
    ]

    for idx in range(2000):
        req = SignUpRequest(request_json={
            'type': UserType.STUDENT,
            'email_credential': {
                'email': 'student+{0}'.format(idx) + '@test.com',
                'password': '1234'
            },
            'student_basic_information': {
                'type': UserType.STUDENT,
                'name': random.choice(['yskim', 'jhkim', 'thpark', 'dwkim']) + '{0}{1}'.format('student', idx),
                'gender': random.choice([Gender.MALE, Gender.FEMALE]),
                'phone_number': '010-1234-5678',
                'birth_year': random.choice(['1991', '1988', '1970', '1982']),
                'location': random.choice(locations),
                'student_status': random.choice([StudentStatus.KINDERGARTEN,
                                                 StudentStatus.ELEMENTARY_SCHOOL,
                                                 StudentStatus.MIDDLE_SCHOOL,
                                                 StudentStatus.HIGH_SCHOOL,
                                                 StudentStatus.RETRY_UNIVERSITY,
                                                 StudentStatus.ORDINARY]),
                'grade': random.randint(1, 3),
                'department': random.choice([Department.LIBERAL_ARTS,
                                             Department.ART_MUSIC_PHYSICAL,
                                             Department.COMMERCIAL_AND_TECHNICAL,
                                             Department.NATURAL_SCIENCES,
                                             Department.NONE])
            },
            'student_lesson_information': {
                'subjects': [sub.id for sub in random.sample(subjects, random.randint(1, len(subjects)))],
                'days_of_week': random.sample(days_of_week, random.randint(1, len(days_of_week))),
                'class_available_count': random.randint(1, 7),
                'class_time': random.randint(1, 8),
                'preferred_gender': random.choice([Gender.NONE, Gender.MALE, Gender.FEMALE]),
                'preferred_price': random.randint(1, 100),
                'class_type': random.choice([ClassType.GROUP, ClassType.INDIVIDUAL]),
                'level': random.choice([ClassLevel.HIGH, ClassLevel.MID, ClassLevel.LOW]),
                'description': '잘부탁드립니다.'
            }
        })
        req.default_profile_photos = current_app.config['USER_DEFAULT_PROFILE_PHOTOS']
        auth_service.sign_up(req)

    for idx in range(2000):
        req = SignUpRequest(request_json={
            'type': UserType.TEACHER,
            'email_credential': {
                'email': 'teacher+{0}'.format(idx) + '@test.com',
                'password': '1234'
            },
            'teacher_basic_information': {
                'type': UserType.TEACHER,
                'name': random.choice(['yskim', 'jhkim', 'thpark', 'dwkim']) + '{0}{1}'.format('teacher', idx),
                'gender': random.choice([Gender.MALE, Gender.FEMALE]),
                'phone_number': '010-1234-5678',
                'birth_year': '1991',
                'location': random.choice(locations),
                'university': random.choice(['서울대학교', '연세대학교', '고려대학교', '성균관대학교']),
                'university_rank': idx,
                'university_status': random.choice(
                        [UniversityStatus.GRADUATION, UniversityStatus.IN_SCHOOL, UniversityStatus.LEAVE_OF_ABSENCE]),
                'major': random.choice(['전기전자학과', '자동차융합전공', '조선시스템학과', '언론학과']),
            },
            'teacher_lesson_information': {
                'subjects': [sub.id for sub in random.sample(subjects, random.randint(1, len(subjects)))],
                'days_of_week': random.sample(days_of_week, random.randint(1, len(days_of_week))),
                'class_available_count': random.randint(1, 7),
                'class_time': random.randint(1, 8),
                'preferred_gender': random.choice([Gender.NONE, Gender.MALE, Gender.FEMALE]),
                'preferred_price': random.randint(1, 100),
                'career': 5,
                'description': '잘부탁드립니다.'
            }
        })
        req.default_profile_photos = current_app.config['USER_DEFAULT_PROFILE_PHOTOS']
        auth_service.sign_up(req)


def init_fake_lessons():
    users = [1]
    for i in range(100):
        users.append(random.randint(1, 1000))
    for idx in set(users):
        email = 'student+{0}'.format(idx) + '@test.com'
        user = User.query.filter(User.email == email).first()
        lesson_service.create_lesson(user.id)
    lessons = Lesson.query.all()
    for idx in set(users):
        email = 'teacher+{0}'.format(idx) + '@test.com'
        user = User.query.filter(User.email == email).first()
        lesson = random.choice(lessons)
        lesson_service.add_lesson_bidding(lesson.id, user.id, LessonBiddingRequest(
                request_json={'price': random.randint(1, 100)}))


def send_ping_push(user_id):
    notification_service.add_notification(notification_service.NotificationRequest()
                                          .to(user_id)
                                          .on_test(user_id))
