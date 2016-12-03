from flask import request, jsonify, current_app
from app.admin import api
from app.dto.request import SignInRequest
from app.models import User
from app.error import InsufficientPermissionError


@api.route('/auth/sign_in', methods=['POST'])
def sign_in():
    sign_in_request = SignInRequest(request_json=request.get_json())
    user = User.query.filter_by(email=sign_in_request.email).first()
    if not user.is_admin:
        raise InsufficientPermissionError()
    if user.verify_password(sign_in_request.password):
        token = user.generate_auth_token(expires_in=current_app.config['AUTH_TOKEN_EXPIRES_IN'])
        response = jsonify()
        response.set_cookie('m_t_atk', token)
        return response

    raise InsufficientPermissionError()
