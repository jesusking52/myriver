# -*- coding: utf-8 -*-
from flask import Blueprint

api = Blueprint('admin_api', __name__)


@api.route('/ping', methods=['GET'])
def ping():
    return 'pong'


@api.before_request
def before_request():
    pass


@api.after_request
def after_request(response):
    return response

from app.admin import auth, users
