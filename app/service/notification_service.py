# -*- coding: utf-8 -*-
from flask import current_app
from urllib.parse import urlunparse

from app import db, celery_app
from app.constant import NotificationType, NotificationWordType, UserType
from app.dto.notification import GCMDTO, GCMDataDTO, GCMNotificationDTO
from app.models import Notification, User, UserGCM
from app.utils import current_millis, gcm as gcm_service


class NotificationWordSegment:
    USER_NAME = '{{' + NotificationWordType.USER_NAME + '}}'


class NotificationMessageComposer:
    message_skeleton = {
        NotificationType.TEST_PING: NotificationWordSegment.USER_NAME + '에게 보내는 확인 메세지.',
        NotificationType.USER_FAVORITED: NotificationWordSegment.USER_NAME + '님이 찜하셨습니다.',
        NotificationType.LESSON_FAVORITED: NotificationWordSegment.USER_NAME + '님이 찜하셨습니다.',
        NotificationType.START_BIDDING: '경매를 시작하였습니다.',
        NotificationType.BIDDING_LESSON: NotificationWordSegment.USER_NAME + '님이 입찰하였습니다.',
        NotificationType.LESSON_CANCELED: NotificationWordSegment.USER_NAME + '님의 경매가 취소되었습니다.',
        NotificationType.LESSON_DEALING: NotificationWordSegment.USER_NAME + '님의 경매 입찰이 종료되었습니다.',
        NotificationType.LESSON_FINISHED: NotificationWordSegment.USER_NAME + '님의 경매가 최종 마감되었습니다.',
        NotificationType.USER_SELECTED: NotificationWordSegment.USER_NAME + '님이 회원님을 최종 선택하였습니다.',
        NotificationType.PHONE_NUMBER_CHECKED: NotificationWordSegment.USER_NAME + '님이 회원님의 연락처를 확인했습니다.'
    }

    def get_user_favorite_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.USER_FAVORITED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_lesson_favorite_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.LESSON_FAVORITED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_start_bidding_message(self):
        return self.message_skeleton.get(NotificationType.START_BIDDING)

    def get_bidding_lesson_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.BIDDING_LESSON)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_lesson_canceled_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.LESSON_CANCELED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_lesson_dealing_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.LESSON_DEALING)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_lesson_finished_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.LESSON_FINISHED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_user_selected_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.USER_SELECTED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_phone_number_checked_message(self, words):
        skeleton = self.message_skeleton.get(NotificationType.PHONE_NUMBER_CHECKED)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton

    def get_test_ping(self, words):
        skeleton = self.message_skeleton.get(NotificationType.TEST_PING)
        for key in words.keys():
            skeleton = skeleton.replace(key, words.get(key)[:1] + "oo")
        return skeleton


class NotificationMessageWords:
    @staticmethod
    def get_user_favorite_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_lesson_favorite_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_bidding_lesson_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_lesson_canceled_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_lesson_dealing_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_lesson_finished_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_user_selected_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }

    @staticmethod
    def get_phone_number_checked_words(user):
        return {
            NotificationWordSegment.USER_NAME: user.name
        }


class NotificationRequest:
    def __init__(self):
        self.notification = Notification()
        self.notification.created_at = current_millis()

    def to(self, user_id):
        self.notification.user_id = user_id
        return self

    def on_test(self, user_id):
        user = User.query.get(user_id)
        self.notification.type = NotificationType.TEST_PING
        self.notification.words = {
            NotificationWordSegment.USER_NAME: user.name
        }
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.TEST_PING)
        self.notification.images = user.profile_photos
        self.notification.deep_link = None
        return self

    def on_user_favorited(self, to_user_id, from_user_id):
        user = User.query.get(from_user_id)
        self.notification.type = NotificationType.USER_FAVORITED
        self.notification.words = NotificationMessageWords.get_user_favorite_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.USER_FAVORITED)
        self.notification.images = user.profile_photos
        self.notification.deep_link = None
        self.notification.deep_link = DeepLinkBuilder().teacher_favorite(to_user_id).build()
        return self

    def on_lesson_favorited(self, lesson_id, from_user_id):
        user = User.query.get(from_user_id)
        self.notification.type = NotificationType.LESSON_FAVORITED
        self.notification.words = NotificationMessageWords.get_lesson_favorite_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.LESSON_FAVORITED)
        self.notification.images = user.profile_photos
        self.notification.deep_link = None
        self.notification.deep_link = DeepLinkBuilder().lesson_favorite(lesson_id).build()
        return self

    def on_start_bidding(self, lesson_id):
        self.notification.type = NotificationType.START_BIDDING
        self.notification.words = None
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.START_BIDDING)
        self.notification.images = None
        self.notification.deep_link = DeepLinkBuilder().lesson_created(lesson_id).build()
        return self

    def on_bidding_lesson(self, bidding_user_id, lesson_id):
        user = User.query.get(bidding_user_id)
        self.notification.type = NotificationType.BIDDING_LESSON
        self.notification.words = NotificationMessageWords.get_user_favorite_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.BIDDING_LESSON)
        self.notification.deep_link = DeepLinkBuilder().lesson_bidding(lesson_id).build()
        return self

    def on_lesson_canceled(self, lesson_owner_id, lesson_id):
        user = User.query.get(lesson_owner_id)
        self.notification.type = NotificationType.LESSON_CANCELED
        self.notification.words = NotificationMessageWords.get_lesson_canceled_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.LESSON_CANCELED)
        self.notification.deep_link = DeepLinkBuilder().lesson_canceled(lesson_id).build()
        return self

    def on_lesson_dealing(self, lesson_owner_id, lesson_id):
        user = User.query.get(lesson_owner_id)
        self.notification.type = NotificationType.LESSON_DEALING
        self.notification.words = NotificationMessageWords.get_lesson_dealing_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.LESSON_DEALING)
        self.notification.deep_link = DeepLinkBuilder().lesson_dealing(lesson_id).build()
        return self

    def on_lesson_finished(self, lesson_owner_id, lesson_id):
        user = User.query.get(lesson_owner_id)
        self.notification.type = NotificationType.LESSON_FINISHED
        self.notification.words = NotificationMessageWords.get_lesson_finished_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.LESSON_FINISHED)
        self.notification.deep_link = DeepLinkBuilder().lesson_finished(lesson_id).build()
        return self

    def on_user_selected(self, teacher_id, lesson_id):
        user = User.query.get(teacher_id)
        self.notification.type = NotificationType.USER_SELECTED
        self.notification.words = NotificationMessageWords.get_user_selected_words(user)
        self.notification.skeleton = NotificationMessageComposer.message_skeleton.get(NotificationType.USER_SELECTED)
        self.notification.deep_link = DeepLinkBuilder().teacher_selected(lesson_id).build()
        return self

    def on_phone_number_checked(self, from_user_id):
        user = User.query.get(from_user_id)
        self.notification.type = NotificationType.PHONE_NUMBER_CHECKED
        self.notification.words = NotificationMessageWords.get_phone_number_checked_words(user)
        self.notification.skeleton = NotificationMessageComposer \
            .message_skeleton \
            .get(NotificationType.PHONE_NUMBER_CHECKED)
        self.notification.deep_link = None
        if user.type == UserType.TEACHER:
            self.notification.deep_link = DeepLinkBuilder().teacher_check_phone_number(from_user_id).build()
        return self


class DeepLinkBuilder:
    SCHEME = 'matchingtutor'

    def __init__(self):
        self.paths = []
        self.params = {}

    def teacher_favorite(self, user_id):
        self.paths.append('teacher')
        self.params['user_id'] = user_id
        return self

    def to_lesson(self, lesson_id):
        self.paths.append('lesson')
        self.params['lesson_id'] = lesson_id

    def lesson_favorite(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def lesson_created(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def lesson_bidding(self, lesson_id):
        self.to_lesson(lesson_id)
        self.paths.append('biddings')
        return self

    def lesson_dealing(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def lesson_canceled(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def lesson_finished(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def teacher_selected(self, lesson_id):
        self.to_lesson(lesson_id)
        return self

    def teacher_check_phone_number(self, user_id):
        self.paths.append('teacher')
        self.params['user_id'] = user_id
        return self

    def build(self):
        path = '/'.join(self.paths)
        param = '&'.join([key + '=' + str(self.params[key]) for key in self.params.keys()])
        return urlunparse((self.SCHEME, path, '', '', param, ''))


def get_message(type, words):
    composer = NotificationMessageComposer()
    if type == NotificationType.USER_FAVORITED:
        return composer.get_user_favorite_message(words)
    elif type == NotificationType.LESSON_FAVORITED:
        return composer.get_lesson_favorite_message(words)
    elif type == NotificationType.START_BIDDING:
        return composer.get_start_bidding_message()
    elif type == NotificationType.BIDDING_LESSON:
        return composer.get_bidding_lesson_message(words)
    elif type == NotificationType.LESSON_CANCELED:
        return composer.get_lesson_canceled_message(words)
    elif type == NotificationType.LESSON_DEALING:
        return composer.get_lesson_dealing_message(words)
    elif type == NotificationType.LESSON_FINISHED:
        return composer.get_lesson_finished_message(words)
    elif type == NotificationType.USER_SELECTED:
        return composer.get_user_selected_message(words)
    elif type == NotificationType.PHONE_NUMBER_CHECKED:
        return composer.get_phone_number_checked_message(words)
    elif type == NotificationType.TEST_PING:
        return composer.get_test_ping(words)


def add_notification(notification_request):
    notification = notification_request.notification
    db.session.add(notification)
    db.session.commit()
    send_push(notification)
    return notification


def send_push(notification):
    user_gcms = UserGCM.query.filter(UserGCM.user_id == notification.user_id).all()
    api_key = current_app.config['GCM_API_KEY']

    data_dto = GCMDataDTO(user_id=notification.user_id,
                          message=get_message(notification.type, notification.words),
                          deep_link=None,
                          icon_url=None,
                          created_at=current_millis())
    notification_dto = GCMNotificationDTO(title=None,
                                          text=get_message(notification.type, notification.words))
    for user_gcm in user_gcms:
        gcm_dto = GCMDTO(to=user_gcm.key,
                         data=data_dto)
        internal_send_push(gcm_service.GCMOptions(api_key=api_key,
                                                  data=vars(gcm_dto)))


@celery_app.task(max_tries=2)
def internal_send_push(options):
    gcm_service.send_gcm_message(options)
