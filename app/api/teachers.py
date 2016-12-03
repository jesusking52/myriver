# -*- coding: utf-8 -*-
from flask import request, current_app
from app.api import api
from app.decorators import success_response
from app.service import auth_service, user_service
from app.utils import dtos
from app.dto.user import UserDTO


@api.route('/teachers/<int:user_id>', methods=['GET'])
@success_response
def get_teachers(user_id):
    """
    :param
        universities: List(String)
        major: String
        gender: Enum
        subjects: List(Enum)
        more_than_price: Integer(exclude)
        less_than_price: Integer(exclude)
        max_zip_code: Integer(include)
        min_zip_code: Integer(include)
        next_token: Integer
    :return:
    """
    auth_service.check_permission()
    options = user_service.GetTeacherOptions(universities=request.args.get('universities'),
                                             gender=request.args.get('gender'),
                                             subjects=request.args.get('subjects'),
                                             more_than_price=request.args.get('more_than_price'),
                                             less_than_price=request.args.get('less_than_price'),
                                             # sido=request.args.get('sido'),
                                             # sigungu=request.args.get('sigungu'),
                                             # bname=request.args.get('bname'),
                                             max_zip_code=request.args.get('max_zip_code'),
                                             min_zip_code=request.args.get('min_zip_code'),
                                             next_token=request.args.get('next_token'),
                                             per_page=current_app.config['PER_PAGE'])
    teachers, paging = user_service.get_teachers(user_id, options)
    dto_list = dtos(UserDTO, teachers, is_checked_phone_number=False)
    return dto_list, paging
