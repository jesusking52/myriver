# -*- coding: utf-8 -*-
from app.dto.user import UserDTO


class SignUpResult:
    def __init__(self, user, token=''):
        self.user = vars(UserDTO(user, is_checked_phone_number=True))
        self.token = token


class IssueTokenResult:
    def __init__(self, user, token=''):
        self.user = vars(UserDTO(user, is_checked_phone_number=True))
        self.token = token
