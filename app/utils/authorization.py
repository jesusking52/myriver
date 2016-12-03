# -*- coding: utf-8 -*-
from app.constant import AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX


def get_access_token(request):
    auth_token = request.headers.get(AUTHORIZATION_HEADER)
    if auth_token:
        auth_token = auth_token.strip()
        if not auth_token.startswith(ACCESS_TOKEN_PREFIX):
            return None
        return auth_token[len(ACCESS_TOKEN_PREFIX):]
    return None
