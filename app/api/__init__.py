# -*- coding: utf-8 -*-
from flask import Blueprint, request, g
from app.utils.authorization import get_access_token

api = Blueprint('api', __name__)


@api.route('/ping', methods=['GET'])
def ping():
    return 'pong'


@api.before_request
def before_request():
    auth_token = get_access_token(request)
    if auth_token is not None:
        g.auth_token = auth_token
    pass


@api.after_request
def after_request(response):
    return response


# do this last to avoid circular dependencies
from app.api import auth, users, teachers, lessons, infos, payment
