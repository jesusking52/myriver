# -*- coding: utf-8 -*-
from flask import request, current_app
from app.api import api
from app.service import auth_service, lesson_service
from app.decorators import success_response
from app.dto.request import LessonBiddingRequest
from app.dto.user import UserDTO
from app.dto.lesson import LessonDTO, LessonBiddingDTO, LessonFavoriteDTO
from app.utils import dtos
from app.constant import LessonStatus


@api.route('/lessons', methods=['GET'])
@success_response
def get_lessons():
    """
    :param
        gender: Enum
        subjects: List(Enum)
        more_than_price: Integer(exclude)
        less_than_price: Integer(exclude)
        student_status: Enum
        grade: Integer
        max_zip_code: Integer(include)
        min_zip_code: Integer(include)
        nextToken: Integer
    :return:
    """
    auth_service.check_permission()
    options = lesson_service.GetLessonOptions(gender=request.args.get('gender'),
                                              subjects=request.args.get('subjects'),
                                              more_than_price=request.args.get('more_than_price'),
                                              less_than_price=request.args.get('less_than_price'),
                                              student_status=request.args.get('student_status'),
                                              grade=request.args.get('grade'),
                                              max_zip_code=request.args.get('max_zip_code'),
                                              min_zip_code=request.args.get('min_zip_code'),
                                              statuses=[LessonStatus.BIDDING, LessonStatus.DEALING],
                                              next_token=request.args.get('next_token'),
                                              per_page=current_app.config['PER_PAGE'])
    lessons, paging = lesson_service.get_lessons(options)
    dto_list = dtos(LessonDTO, lessons)
    return dto_list, paging


@api.route('/lessons', methods=['POST'])
@success_response
def create_lesson():
    """
    :return:
    :error
        INVALID_ARGUMENT
        ELEMENT_ALREADY_EXIST
    """
    user_data = auth_service.check_permission()
    lesson = lesson_service.create_lesson(user_data.get('id'))
    return LessonDTO(lesson), None


@api.route('/lessons/<int:lesson_id>', methods=['GET'])
@success_response
def get_lesson(lesson_id):
    user_data = auth_service.check_permission()
    lesson = lesson_service.get_lesson(lesson_id)
    biddings_count = lesson_service.get_lesson_biddings_count(lesson_id)
    is_favorited = False
    if lesson_service.get_lesson_favorite(lesson_id, user_data.get('id')) is not None:
        is_favorited = True
    is_bid = False
    users_bidding = lesson_service.get_lesson_bidding(lesson_id, user_data.get('id'))
    if users_bidding is not None:
        is_bid = True
    is_checked_owner_phone_number = False
    if lesson_service.is_checked_phone_number(user_data.get('id'), lesson.owner_id) is not None:
        is_checked_owner_phone_number = True
    is_checked_selected_user_phone_number = False
    if lesson_service.is_checked_phone_number(user_data.get('id'), lesson.selected_user_id) is not None:
        is_checked_selected_user_phone_number = True
    return LessonDTO(lesson,
                     biddings_count=biddings_count,
                     is_favorited=is_favorited,
                     is_bid=is_bid,
                     users_bidding=users_bidding,
                     is_checked_owner_phone_number=is_checked_owner_phone_number,
                     is_checked_selected_user_phone_number=is_checked_selected_user_phone_number), None


@api.route('/lessons/<int:lesson_id>', methods=['DELETE'])
@success_response
def remove_lesson(lesson_id):
    user_data = auth_service.check_permission()
    lesson_service.delete_lesson(user_data.get('id'), lesson_id)
    return True, None


@api.route('/lessons/<int:lesson_id>/cancel', methods=['POST'])
@success_response
def cancel_lesson(lesson_id):
    user_data = auth_service.check_permission()
    canceled_lesson = lesson_service.cancel_lesson(user_data.get('id'), lesson_id)
    biddings_count = lesson_service.get_lesson_biddings_count(lesson_id)
    is_checked_owner_phone_number = False
    if lesson_service.is_checked_phone_number(user_data.get('id'), canceled_lesson.owner_id) is not None:
        is_checked_owner_phone_number = True
    return LessonDTO(canceled_lesson,
                     biddings_count=biddings_count,
                     is_checked_owner_phone_number=is_checked_owner_phone_number), None


@api.route('/lessons/<int:lesson_id>/select/<int:user_id>', methods=['POST'])
@success_response
def select_teacher(lesson_id, user_id):
    """
    :param lesson_id:
    :param user_id:
    :return:
    :error
        EXPIRED_LESSON_DEALING
        NO_SUCH_ELEMENT
    """
    user_data = auth_service.check_permission()
    user = lesson_service.select_teacher(lesson_id, user_data.get('id'), user_id)
    return UserDTO(user, is_checked_phone_number=True), None


@api.route('/lessons/<int:lesson_id>/favorites', methods=['POST'])
@success_response
def favorite_lesson(lesson_id):
    """
    :param lesson_id:
    :return:
    :error
        ELEMENT_ALREADY_EXISTS
    """
    user_data = auth_service.check_permission()
    lesson_favorite = lesson_service.add_lesson_favorite(lesson_id, user_data.get('id'))
    return LessonFavoriteDTO(lesson_favorite, include_lesson=True), None


@api.route('/lessons/<int:lesson_id>/favorites', methods=['DELETE'])
@success_response
def unfavorite_lesson(lesson_id):
    user_data = auth_service.check_permission()
    lesson_service.remove_lesson_favorite(lesson_id, user_data.get('id'))
    return None, None


@api.route('/lessons/<int:lesson_id>/biddings', methods=['GET'])
@success_response
def get_biddings(lesson_id):
    """
    :param
        next_token: Integer
    :return:
    """
    auth_service.check_permission()
    options = lesson_service.GetLessonBiddingOptions(lesson_id=lesson_id,
                                                     next_token=request.args.get('next_token'),
                                                     per_page=current_app.config['PER_PAGE'])
    lesson_biddings, paging = lesson_service.get_lesson_biddings(options)
    dto_list = dtos(LessonBiddingDTO, lesson_biddings, include_user=True)
    return dto_list, paging


@api.route('/lessons/<int:lesson_id>/biddings', methods=['POST'])
@success_response
def add_lesson_bidding(lesson_id):
    """
    :param lesson_id:
    :return:
    :error
        NO_SUCH_ELEMENT
        EXPIRED_LESSON_BIDDING
        INVALID_ARGUMENT
        ELEMENT_ALREADY_EXISTS
    """
    user_data = auth_service.check_permission()
    lesson_bidding_request = LessonBiddingRequest(request_json=request.get_json())
    lesson_bidding = lesson_service.add_lesson_bidding(lesson_id,
                                                       user_data.get('id'),
                                                       lesson_bidding_request)
    return LessonBiddingDTO(lesson_bidding, include_lesson=True), None


@api.route('/lessons/<int:lesson_id>/biddings', methods=['DELETE'])
@success_response
def remove_lesson_bidding(lesson_id):
    user_data = auth_service.check_permission()
    lesson_service.remove_lesson_bidding(lesson_id, user_data.get('id'))
    return None, None
