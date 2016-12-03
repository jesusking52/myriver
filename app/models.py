# -*- coding: utf-8 -*-
import json
from flask import current_app
from app import db
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from werkzeug.security import generate_password_hash, check_password_hash


class JsonType(db.TypeDecorator):
    """
    json 을 string 으로 저장한다
    """
    impl = db.Text

    def process_bind_param(self, value, dialect):
        if value is not None:
            return json.dumps(value)

    def process_result_value(self, value, dialect):
        if value is not None:
            return json.loads(value)


class SubjectGroup(db.Model):
    __tablename__ = 'subject_groups'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    type = db.Column(db.String(64), nullable=False)
    name = db.Column(db.String(64), nullable=False)

    subjects = db.relationship('Subject', cascade='all')


class Subject(db.Model):
    __tablename__ = 'subjects'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    subject_group_id = db.Column(db.BigInteger, db.ForeignKey('subject_groups.id'))
    type = db.Column(db.String(64), nullable=False)
    name = db.Column(db.String(64), nullable=False)


class DayOfWeek(db.Model):
    __tablename__ = 'days_of_week'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    name = db.Column(db.String(32), nullable=False)
    value = db.Column(db.Integer)


students_available_subjects = db.Table('students_available_subjects',
                                       db.Column('student_id', db.BigInteger, db.ForeignKey('students.id')),
                                       db.Column('subject_id', db.BigInteger, db.ForeignKey('subjects.id')))
teachers_available_subjects = db.Table('teachers_available_subjects',
                                       db.Column('teacher_id', db.BigInteger, db.ForeignKey('teachers.id')),
                                       db.Column('subject_id', db.BigInteger, db.ForeignKey('subjects.id')))
students_available_days_of_week = db.Table('students_available_days_of_week',
                                           db.Column('student_id', db.BigInteger, db.ForeignKey('students.id')),
                                           db.Column('day_of_week_id', db.BigInteger, db.ForeignKey('days_of_week.id')))
teachers_available_days_of_week = db.Table('teachers_available_days_of_week',
                                           db.Column('teacher_id', db.BigInteger, db.ForeignKey('teachers.id')),
                                           db.Column('day_of_week_id', db.BigInteger, db.ForeignKey('days_of_week.id')))

lessons_subjects = db.Table('lessons_subjects',
                            db.Column('lesson_id', db.BigInteger, db.ForeignKey('lessons.id')),
                            db.Column('subject_id', db.BigInteger, db.ForeignKey('subjects.id')))

lessons_days_of_week = db.Table('lessons_days_of_week',
                                db.Column('lesson_id', db.BigInteger, db.ForeignKey('lessons.id')),
                                db.Column('day_of_week_id', db.BigInteger, db.ForeignKey('days_of_week.id')))


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    type = db.Column(db.String(16), index=True)
    is_admin = db.Column(db.Boolean, default=False)
    email = db.Column(db.String(128), nullable=False)
    password_hash = db.Column(db.String(128), nullable=False)
    name = db.Column(db.String(64))
    gender = db.Column(db.String(16), index=True)
    phone_number = db.Column(db.String(32))
    birth_year = db.Column(db.Integer)

    zip_code = db.Column(db.Integer)
    address = db.Column(db.String(256))
    sido = db.Column(db.String(64), nullable=True)
    sigungu = db.Column(db.String(64), nullable=True)
    bname = db.Column(db.String(64), nullable=True)
    latitude = db.Column(db.Float)
    longitude = db.Column(db.Float)

    profile_photos = db.Column(JsonType)
    created_at = db.Column(db.BigInteger)

    coins = db.Column(db.Integer, default=0)

    student = db.relationship('Student', uselist=False, cascade='all')
    teacher = db.relationship('Teacher', uselist=False, cascade='all')
    gcms = db.relationship('UserGCM', cascade='all')

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute.')

    @password.setter
    def password(self, password):
        self.password_hash = generate_password_hash(password)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def generate_auth_token(self, expires_in=3600):
        s = Serializer(current_app.config['SECRET_KEY'], expires_in=expires_in)
        return s.dumps({
            'id': self.id,
            'email': self.email,
            'name': self.name
        }).decode('utf-8')

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except:
            return None
        return data


class Student(db.Model):
    __tablename__ = 'students'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'))
    student_status = db.Column(db.String(32))
    grade = db.Column(db.Integer)
    department = db.Column(db.String(32))
    level = db.Column(db.String(16))
    class_available_count = db.Column(db.Integer)
    class_time = db.Column(db.Integer)
    class_type = db.Column(db.String(16))
    preferred_gender = db.Column(db.String(16))
    preferred_price = db.Column(db.BigInteger)
    description = db.Column(db.Text)
    created_at = db.Column(db.BigInteger)

    user = db.relationship('User', uselist=False, cascade='all')
    available_subjects = db.relationship('Subject', secondary=students_available_subjects, cascade='all')
    available_days_of_week = db.relationship('DayOfWeek', secondary=students_available_days_of_week, cascade='all')


class Teacher(db.Model):
    __tablename__ = 'teachers'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'))
    university = db.Column(db.String(64), index=True)
    university_rank = db.Column(db.BigInteger)
    university_status = db.Column(db.String(32))
    major = db.Column(db.String(64))
    career = db.Column(db.Integer)
    class_available_count = db.Column(db.Integer)
    class_time = db.Column(db.Integer)
    preferred_gender = db.Column(db.String(16))
    preferred_price = db.Column(db.BigInteger)
    description = db.Column(db.Text)
    hide_on_searching = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.BigInteger)

    user = db.relationship('User', uselist=False, cascade='all')
    available_subjects = db.relationship('Subject', secondary=teachers_available_subjects, cascade='all')
    available_days_of_week = db.relationship('DayOfWeek', secondary=teachers_available_days_of_week, cascade='all')


class ZipCode(db.Model):
    __tablename__ = 'zip_codes'
    code = db.Column(db.Integer, primary_key=True)
    # sub_code로 index를 만들어 시도/시군구를 빠르게 검색한다.
    # sub_code는 code 앞 2자리를 따서 만든다. (ex. 51023 -> 51)
    sub_code = db.Column(db.Integer, index=True)
    sido = db.Column(db.String(64))
    sigungu = db.Column(db.String(64))


class Lesson(db.Model):
    __tablename__ = 'lessons'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    owner_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), index=True)
    selected_user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), index=True)
    status = db.Column(db.String(32), index=True)
    created_at = db.Column(db.BigInteger)  # 경매 시작 시간
    created_time = db.Column(db.DateTime)

    # 히스토리 정보에서는 과거의 유저정보를 담고 있어야 하기 때문에 User 정보를 따로 저장한다.
    # User 정보
    email = db.Column(db.String(128), nullable=False)
    name = db.Column(db.String(64))
    gender = db.Column(db.String(16))
    phone_number = db.Column(db.String(32))
    birth_year = db.Column(db.Integer)
    zip_code = db.Column(db.Integer)
    address = db.Column(db.String(256))
    sido = db.Column(db.String(64), nullable=True)
    sigungu = db.Column(db.String(64), nullable=True)
    bname = db.Column(db.String(64), nullable=True)
    latitude = db.Column(db.Float)
    longitude = db.Column(db.Float)

    # Student 정보
    student_status = db.Column(db.String(32))
    grade = db.Column(db.Integer)
    department = db.Column(db.String(32))
    level = db.Column(db.String(16))
    class_available_count = db.Column(db.Integer)
    class_time = db.Column(db.Integer)
    class_type = db.Column(db.String(16))
    preferred_gender = db.Column(db.String(16))
    preferred_price = db.Column(db.BigInteger)
    description = db.Column(db.Text)

    available_subjects = db.relationship('Subject', secondary=lessons_subjects, cascade='all')
    available_days_of_week = db.relationship('DayOfWeek', secondary=lessons_days_of_week, cascade='all')
    owner = db.relationship('User', foreign_keys=owner_id, uselist=False)
    selected_user = db.relationship('User', foreign_keys=selected_user_id, uselist=False)


class LessonBidding(db.Model):
    __tablename__ = 'lesson_biddings'
    lesson_id = db.Column(db.BigInteger, db.ForeignKey('lessons.id'), primary_key=True, index=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    is_selected = db.Column(db.Boolean, default=False)
    price = db.Column(db.Integer)
    created_at = db.Column(db.BigInteger)  # 입찰 시간

    lesson = db.relationship('Lesson', uselist=False)
    user = db.relationship('User', uselist=False)


class UserFavorite(db.Model):
    __tablename__ = 'user_favorites'
    from_user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    to_user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    created_at = db.Column(db.BigInteger)

    from_user = db.relationship('User', foreign_keys=from_user_id, uselist=False)
    to_user = db.relationship('User', foreign_keys=to_user_id, uselist=False)


class LessonFavorite(db.Model):
    __tablename__ = 'lesson_favorites'
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    lesson_id = db.Column(db.BigInteger, db.ForeignKey('lessons.id'), primary_key=True, index=True)
    created_at = db.Column(db.BigInteger)

    user = db.relationship('User', uselist=False)
    lesson = db.relationship('Lesson', uselist=False)


class Notification(db.Model):
    __tablename__ = 'notifications'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    user_id = db.Column(db.BigInteger, index=True)
    type = db.Column(db.String(64))
    words = db.Column(JsonType)
    skeleton = db.Column(db.Text)
    images = db.Column(JsonType)
    deep_link = db.Column(db.String(128))
    created_at = db.Column(db.BigInteger)


class UserGCM(db.Model):
    __tablename__ = 'user_gcms'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), index=True)
    key = db.Column(db.String(256), nullable=False)
    created_at = db.Column(db.BigInteger)


class CheckPhoneNumber(db.Model):
    __tablename__ = 'check_phone_number'
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    checked_user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True, index=True)
    created_at = db.Column(db.BigInteger)

    user = db.relationship('User', foreign_keys=user_id, uselist=False)
    checked_user = db.relationship('User', foreign_keys=checked_user_id, uselist=False)


class CertificatePhoneNumber(db.Model):
    __tablename__ = 'phone_number_certifications'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    phone_number = db.Column(db.String(32), index=True)
    auth_number = db.Column(db.Integer, index=True)
    created_at = db.Column(db.BigInteger)
    created_time = db.Column(db.DateTime)


class LessonStatusChangedLog(db.Model):
    """
    푸시를 보내기 위해 저장하는 로그 (mysql event)
    """
    __tablename__ = 'lesson_status_changed_logs'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    lesson_id = db.Column(db.BigInteger, db.ForeignKey('lessons.id'))
    changed_status = db.Column(db.String(32))
    changed_time = db.Column(db.DateTime)


class CoinTransaction(db.Model):
    __tablename__ = 'coin_transactions'
    id = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), index=True)
    value = db.Column(db.Integer)
    transaction_type = db.Column(db.String(32))
    created_at = db.Column(db.BigInteger)


class Receipt(db.Model):
    """
    {
       "orderId":"12999763169054705758.1371079406387615",
       "packageName":"com.example.app",
       "productId":"exampleSku",
       "purchaseTime":1345678900000,
       "purchaseState":0,
       "developerPayload":"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ",
       "purchaseToken":"rojeslcdyyiapnqcynkjyyjh"
     }
    """
    __tablename__ = 'purchase_receipts'
    order_id = db.Column(db.String(128), primary_key=True)
    user_id = db.Column(db.BigInteger, db.ForeignKey('users.id'), index=True)
    package_name = db.Column(db.String(64))
    product_id = db.Column(db.String(64))
    purchase_time = db.Column(db.BigInteger)
    purchase_state = db.Column(db.Integer)
    purchase_token = db.Column(db.String(128))

class Review(db.Model):
    __tablename__ = 'teachers_review'
    reviewidx = db.Column(db.BigInteger, primary_key=True, autoincrement=True)
    teacher_id = db.Column(db.BigInteger, primary_key=True)
    user_id = db.Column(db.BigInteger, primary_key=True)
    rank = db.Column(db.BigInteger)
    review = db.Column(db.String(300))
    created_at = db.Column(db.BigInteger)
    updated_at = db.Column(db.BigInteger)

