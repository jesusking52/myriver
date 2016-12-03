# -*- coding: utf-8 -*-
from enum import Enum

AUTHORIZATION_HEADER = 'Authorization'
ACCESS_TOKEN_PREFIX = 'RiverAuction access_token='


class HttpStatus:
    SUCCESS = 200
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    FORBIDDEN = 403
    NOT_FOUND = 404
    TOO_MANY_REQUESTS = 429
    INTERNAL_SERVER_ERROR = 500


class ErrorCause:
    UNKNOWN = 'UNKNOWN'
    INSUFFICIENT_PERMISSION = 'INSUFFICIENT_PERMISSION'
    NO_SUCH_ELEMENT = 'NO_SUCH_ELEMENT'
    BAD_REQUEST = 'BAD_REQUEST'
    NOT_ALLOWED = 'NOT_ALLOWED'
    TEMPORARY_UNAVAILABLE = 'TEMPORARY_UNAVAILABLE'
    INTERNAL_SERVER_ERROR = 'INTERNAL_SERVER_ERROR'

    INVALID_ARGUMENT = 'INVALID_ARGUMENT'

    INVALID_TOKEN = 'INVALID_TOKEN'
    EXPIRED_TOKEN = 'EXPIRED_TOKEN'
    REVOKED_TOKEN = 'REVOKED_TOKEN'
    INVALID_CERTIFICATION = 'INVALID_CERTIFICATION'

    PASSWORD_NOT_MATCHED = 'PASSWORD_NOT_MATCHED'
    ELEMENT_ALREADY_EXISTS = 'ELEMENT_ALREADY_EXISTS'
    EXPIRED_LESSON_BIDDING = 'EXPIRED_LESSON_BIDDING'
    EXPIRED_LESSON_DEALING = 'EXPIRED_LESSON_DEALING'

    INSUFFICIENT_COINS = 'INSUFFICIENT_COINS'
    WRONG_RECEIPT = 'WRONG_RECEIPT'


class OrderBy:
    RECENTLY_CREATED = 'RECENTLY_CREATED '
    REVERSE_RECENTLY_CREATED = 'REVERSE_RECENTLY_CREATED'


class UserType:
    STUDENT = 'STUDENT'
    TEACHER = 'TEACHER'


class Gender:
    NONE = 'NONE'
    MALE = 'MALE'
    FEMALE = 'FEMALE'


class Subject(Enum):
    KOREAN = '국어'
    ENGLISH = '영어'
    MATH = '수학'
    SCIENCE = '과학'
    SOCIETY = '사회'
    DISCUSSION = '논술'
    SAT = 'SAT'

    ENGLISH_CONVERSATION = '영어회화'
    CHINESE = '중국어'
    SPANISH = '스페인어'
    GERMAN = '독일어'
    FRENCH = '프랑스어'
    JPT = 'JPT'
    HSK = 'HSK'
    TOEIC = 'TOEIC'
    TOEFL = 'TOEFL'
    TEPS = 'TEPS'

    PIANO = '피아노'
    GUITAR = '기타'
    FLUTE = '플루트'
    VIOLIN = '바이올린'
    UKULELE = '우쿨렐레'
    DRUM = '드럼'
    CELLO = '첼로'
    VOCAL = '보컬'
    DANCE = '춤'
    EDM = 'EDM'


class SubjectGroup(Enum):
    CURRICULUM_SUBJECT = '교과목'
    FOREIGN_LANGUAGE = '외국어'
    MUSIC = '음악'

    @staticmethod
    def get_subjects(key):
        if key == SubjectGroup.CURRICULUM_SUBJECT.name:
            return [Subject.KOREAN,
                    Subject.ENGLISH,
                    Subject.MATH,
                    Subject.SCIENCE,
                    Subject.SOCIETY,
                    Subject.DISCUSSION,
                    Subject.SAT]
        elif key == SubjectGroup.FOREIGN_LANGUAGE.name:
            return [Subject.ENGLISH_CONVERSATION,
                    Subject.CHINESE,
                    Subject.SPANISH,
                    Subject.GERMAN,
                    Subject.FRENCH,
                    Subject.JPT,
                    Subject.HSK,
                    Subject.TOEIC,
                    Subject.TOEFL,
                    Subject.TEPS]
        elif key == SubjectGroup.MUSIC.name:
            return [Subject.PIANO,
                    Subject.GUITAR,
                    Subject.FLUTE,
                    Subject.VIOLIN,
                    Subject.UKULELE,
                    Subject.DRUM,
                    Subject.CELLO,
                    Subject.VOCAL,
                    Subject.DANCE,
                    Subject.EDM]


# 학교 계열
class Department:
    LIBERAL_ARTS = 'LIBERAL_ARTS'  # 문과
    NATURAL_SCIENCES = 'NATURAL_SCIENCES'  # 이과
    ART_MUSIC_PHYSICAL = 'ART_MUSIC_PHYSICAL'  # 예체능
    COMMERCIAL_AND_TECHNICAL = 'COMMERCIAL_AND_TECHNICAL'  # 실업계
    NONE = 'NONE'  # 미정


class ClassLevel:
    HIGH = 'HIGH'
    MID = 'MIDDLE'
    LOW = 'LOW'


class ClassType:
    INDIVIDUAL = 'INDIVIDUAL'
    GROUP = 'GROUP'


class UniversityStatus:
    IN_SCHOOL = 'IN_SCHOOL'
    LEAVE_OF_ABSENCE = 'LEAVE_OF_ABSENCE'
    GRADUATION = 'GRADUATION'


class StudentStatus:
    KINDERGARTEN = 'KINDERGARTEN'
    ELEMENTARY_SCHOOL = 'ELEMENTARY_SCHOOL'
    MIDDLE_SCHOOL = 'MIDDLE_SCHOOL'
    HIGH_SCHOOL = 'HIGH_SCHOOL'
    RETRY_UNIVERSITY = 'RETRY_UNIVERSITY'
    UNIVERSITY = 'UNIVERSITY'
    ORDINARY = 'ORDINARY'


class LessonStatus:
    BIDDING = 'BIDDING'
    DEALING = 'DEALING'
    FINISHED = 'FINISHED'
    CANCELED = 'CANCELED'


class DayOfWeek(Enum):
    SUN = 0
    MON = 1
    TUE = 2
    WED = 3
    THU = 4
    FRI = 5
    SAT = 6


class NotificationType:
    """
    - 상대방이 찜한 경우
        - {name}님이 회원님을 찜했습니다.
    - 경매 시작
        - 경매 시작한 학생에게 '경매를 시작하였습니다.'
    - 입찰 시
        - 경매 시작한 학생에게 '{name}님이 입찰하였습니다.'
    - 경매 취소
        - 참여한 선생님들에게 '{name}의 경매가 취소되었습니다.'
    - 걍메 입찰 종료
        - 참여한 선생님들에게 '{name}의 경매 입찰이 종료되었습니다.'
    - 경매 종료
        - 참여한 선생님들에게 '{name}의 경매가 마감되었습니다.'
    - 최종 선택
        - 선택된 선생님에게 '{name}님이 회원님을 최종 선택하였습니다.'
    - 연락처 확인
        - 선생님에게 '{name}님이 회원님의 연락처를 확인했습니다.'
        - 학생에게 '{name}님이 회원님의 연락처를 확인했습니다.'
    """
    TEST_PING = 'TEST_PING'
    USER_FAVORITED = 'USER_FAVORITED'
    LESSON_FAVORITED = 'LESSON_FAVORITED'
    START_BIDDING = 'START_BIDDING'
    BIDDING_LESSON = 'BIDDING_LESSON'
    LESSON_CANCELED = 'LESSON_CANCELED'
    LESSON_DEALING = 'LESSON_DEALING'
    LESSON_FINISHED = 'LESSON_FINISHED'
    USER_SELECTED = 'USER_SELECTED'
    PHONE_NUMBER_CHECKED = 'PHONE_NUMBER_CHECKED'


class NotificationWordType:
    USER_NAME = 'USER_NAME'


class CoinValueOfTransactionType:
    """
    선생님이 경매 입찰시: 1개
    학생이 선생님 연락처보기: 3개
    학생이 경매완료후 선생님 선택시: (희망금액 - 선생님 비딩 금액)*30% (단, 선생님 비딩금액이 희망금액 보다 크면 3개 결제)
    """
    BIDDING = 1
    CHECK_USER_PHONE_NUMBER = 3
    MIN_SELECT_USER = 3


class TransactionType:
    CHARGING = 'CHARGING'
    BIDDING = 'BIDDING'
    CANCEL_BIDDING = 'CANCEL_BIDDING'
    SELECT_TEACHER = 'SELECT_TEACHER'
    CHECK_USER_PHONE_NUMBER = 'CHECK_USER_PHONE_NUMBER'
    REFUND_FOR_UNSELECTED = 'REFUND_FOR_UNSELECTED '
    REFUND = 'REFUND'
    ADMIN = 'ADMIN'


class ObjectType:
    PAYMENT = 32
