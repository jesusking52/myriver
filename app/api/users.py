# -*- coding: utf-8 -*-
from flask import request, current_app
from app.api import api
from app.service import auth_service, user_service, upload_service, lesson_service
from app.decorators import success_response
from app.error import ForbiddenError
from app.utils import dtos, StringUtils
from app.dto.request import UserPatchRequest, UserGCMAddRequest, UserPreferencesRequest, ReviewRequest
from app.dto.user import UserDTO, UserFavoriteDTO, ReviewDTO
from app.dto.lesson import LessonDTO, LessonFavoriteDTO
from app.dto.notification import NotificationDTO
from app.constant import LessonStatus


@api.route('/users/<int:user_id>', methods=['GET'])
@success_response
def get_user(user_id):
    auth_service.check_permission(user_id)
    user = user_service.get_user(user_id)
    return UserDTO(user, is_checked_phone_number=True), None


@api.route('/users/<int:user_id>', methods=['PATCH'])
@success_response
def patch_user(user_id):
    auth_service.check_permission(user_id)
    patch_request = UserPatchRequest(request_json=request.get_json())
    user = user_service.patch_user(user_id, patch_request)
    return UserDTO(user, is_checked_phone_number=True), None


@api.route('/users/<int:user_id>/profile', methods=['GET'])
@success_response
def get_user_profile(user_id):
    """
    :param user_id:
    :return:
    :parameter phone_number(Bool)
    :error
        NO_SUCH_ELEMENT
    """
    user_data = auth_service.check_permission()
    is_checked_phone_number = StringUtils.to_bool(request.args.get('phone_number'))
    if is_checked_phone_number and user_service.is_checked_phone_number(user_data.get('id'), user_id):
        is_checked_phone_number = True
    else:
        is_checked_phone_number = False
    user = user_service.get_user(user_id)
    is_favorited = False
    if user_service.get_favorite(user_data.get('id'), user.id) is not None:
        is_favorited = True
    return UserDTO(user, is_checked_phone_number=is_checked_phone_number, is_favorited=is_favorited), None


@api.route('/users/<int:user_id>/preferences', methods=['POST'])
@success_response
def set_preferences(user_id):
    """
    :param user_id:
    :return:
    """
    auth_service.check_permission(user_id)
    preference_request = UserPreferencesRequest(request.get_json())
    user = user_service.set_preferences(user_id, preference_request)
    return UserDTO(user), None


@api.route('/users/<int:user_id>/profile_photos', methods=['POST'])
@success_response
def set_profile_photos(user_id):
    """
    :param user_id:
    :return:
    :error
        TEMPORARY_UNAVAILABLE
        NO_SUCH_ELEMENT
    """
    auth_service.check_permission(user_id)
    try:
        options = upload_service.UploadResizeOptions(user_id=user_id,
                                                     upload_folder=current_app.config['UPLOAD_FOLDER'],
                                                     aws_access_key=current_app.config['AWS_ACCESS_KEY'],
                                                     aws_secret_key=current_app.config['AWS_SECRET_KEY'],
                                                     aws_bucket=current_app.config['AWS_DEFAULT_BUCKET'],
                                                     aws_region_host=current_app.config['AWS_REGION_HOST'])
        images = upload_service.upload_resize_image(options, request)
    except Exception as e:
        raise ForbiddenError(message=str(e))
    user = user_service.set_profile_photos(user_id, images)
    return UserDTO(user, is_checked_phone_number=True), None


@api.route('/users/<int:user_id>/profile_photos', methods=['DELETE'])
@success_response
def delete_profile_photos(user_id):
    """
    :param user_id:
    :return:
    :error
        NO_SUCH_ELEMENT
    """
    auth_service.check_permission(user_id)
    user = user_service.set_profile_photos(user_id, current_app.config['USER_DEFAULT_PROFILE_PHOTOS'])
    return UserDTO(user), None


@api.route('/users/<int:user_id>/lessons/active', methods=['GET'])
@success_response
def get_user_active_lessons(user_id):
    auth_service.check_permission(user_id)
    options = user_service.GetUserLessonOptions(user_id=user_id,
                                                statuses=[LessonStatus.BIDDING, LessonStatus.DEALING],
                                                next_token=request.args.get('next_token'),
                                                per_page=current_app.config['PER_PAGE'])
    lessons, paging = user_service.get_lessons(options)
    dto_list = dtos(LessonDTO, lessons)
    return dto_list, paging


@api.route('/users/<int:user_id>/lessons/history', methods=['GET'])
@success_response
def get_user_history_lessons(user_id):
    auth_service.check_permission(user_id)
    options = user_service.GetUserLessonOptions(user_id=user_id,
                                                statuses=[LessonStatus.CANCELED, LessonStatus.FINISHED],
                                                next_token=request.args.get('next_token'),
                                                per_page=current_app.config['PER_PAGE'])
    lessons, paging = user_service.get_lessons(options)
    dto_list = dtos(LessonDTO, lessons)
    return dto_list, paging


@api.route('/users/<int:user_id>/favorites/users', methods=['GET'])
@success_response
def get_user_favorites(user_id):
    auth_service.check_permission(user_id)
    options = user_service.GetUserFavoriteOptions(user_id=user_id,
                                                  next_token=request.args.get('next_token'),
                                                  per_page=current_app.config['PER_PAGE'])
    favorites, paging = user_service.get_favorites(options)
    dto_list = dtos(UserFavoriteDTO, favorites, include_to_user=True)
    return dto_list, paging


@api.route('/users/<int:user_id>/favorites/lessons', methods=['GET'])
@success_response
def get_lesson_favorites(user_id):
    auth_service.check_permission(user_id)
    options = lesson_service.GetLessonFavoriteOptions(user_id=user_id,
                                                      next_token=request.args.get('next_token'),
                                                      per_page=current_app.config['PER_PAGE'])
    favorites, paging = lesson_service.get_favorites(options)
    dto_list = dtos(LessonFavoriteDTO, favorites, include_lesson=True)
    return dto_list, paging


@api.route('/users/<int:user_id>/favorites', methods=['POST'])
@success_response
def favorite_user(user_id):
    """
    상대방을 찜할 때 부르는 API 로 get favorites 와는 다르게 상대방 user_id 입니다
    """
    user_data = auth_service.check_permission()
    favorite = user_service.add_favorite(user_data.get('id'), user_id)
    return UserFavoriteDTO(favorite), None


@api.route('/users/<int:user_id>/favorites', methods=['DELETE'])
@success_response
def unfavorite_user(user_id):
    """
    상대방 찜을 없앨 때 부르는 API 로 get favorites 와는 다르게 상대방 user_id 입니다
    """
    user_data = auth_service.check_permission()
    user_service.remove_favorite(user_data.get('id'), user_id)
    return None, None


@api.route('/users/<int:user_id>/notifications', methods=['GET'])
@success_response
def get_user_notifications(user_id):
    auth_service.check_permission(user_id)
    options = user_service.GetNotificationOptions(user_id=user_id,
                                                  next_token=request.args.get('next_token'),
                                                  per_page=current_app.config['PER_PAGE'])
    notifications, paging = user_service.get_notifications(options)
    dto_list = dtos(NotificationDTO, notifications)
    return dto_list, paging


@api.route('/users/<int:user_id>/gcms', methods=['POST'])
@success_response
def add_gcms(user_id):
    """
    :param user_id:
    :return:
    :error:
        INVALID_ARGUMENT
    """
    auth_service.check_permission(user_id)
    gcm_add_request = UserGCMAddRequest(request_json=request.get_json())
    gcm = user_service.add_gcm(user_id, gcm_add_request.key)
    return True, None


@api.route('/users/<int:user_id>/check_phone_number', methods=['POST'])
@success_response
def check_phone_number(user_id):
    """
    :param user_id:
    :return:
    """
    user_data = auth_service.check_permission()
    checked_user = user_service.check_phone_number(user_data.get('id'), user_id)
    return UserDTO(checked_user, is_checked_phone_number=True), None

@api.route('/users/<int:user_id>/review_write', methods=['PATCH'])
@success_response
def review_write_user(user_id):
    auth_service.check_permission(user_id)
    review_request = ReviewRequest(request_json=request.get_json())
    user = user_service.writeReview(user_id, review_request)
    return ReviewDTO(user_id, user), None
