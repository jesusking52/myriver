# -*- coding: utf-8 -*-
import functools

from flask import jsonify
from app.constant import HttpStatus, ErrorCause
from app.error import RiverError


def success_response(func):
    @functools.wraps(func)
    def wrapped(*args, **kwargs):
        data, paging = func(*args, **kwargs)
        response_dto = {
            'status': 'SUCCESS'
        }
        if data is not None:
            if isinstance(data, (dict, bool)):
                response_dto['data'] = data
            elif isinstance(data, list):
                response_dto['data'] = []
                for d in data:
                    element = d
                    if not isinstance(element, dict):
                        element = vars(element)
                    response_dto['data'].append(element)
            else:
                try:
                    # 객체인 경우 dictionary 로 만든다.
                    response_dto['data'] = vars(data)
                except Exception as e:
                    print(e)
                    pass
            if paging is not None:
                response_dto['paging'] = vars(paging)
        response = jsonify(response_dto)
        response.status_code = HttpStatus.SUCCESS
        return response

    return wrapped


def error_response(func):
    @functools.wraps(func)
    def wrapped(*args, **kwargs):
        error = func(*args, **kwargs)
        response_dto = {
            'status': 'ERROR',
            'error': {
                'cause': ErrorCause.UNKNOWN,
                'message': str(error)
            }
        }
        if isinstance(error, RiverError):
            response_dto['error']['cause'] = error.error_cause
            response_dto['error']['message'] = error.message
        response = jsonify(response_dto)
        response.status_code = 500
        if hasattr(error, 'status_code'):
            response.status_code = error.status_code
        return response

    return wrapped
