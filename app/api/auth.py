# -*- coding: utf-8 -*-
from flask import request, current_app
from app.api import api
from app.service import auth_service
from app.decorators import success_response
from app.dto.request import SignUpRequest, EmailCredentialRequest, CertifyPhoneNumberRequest


@api.route('/auth/sign_up', methods=['POST'])
@success_response
def sign_up():
    sign_up_request = SignUpRequest(request_json=request.get_json())
    sign_up_request.default_profile_photos = current_app.config['USER_DEFAULT_PROFILE_PHOTOS']
    sign_up_result = auth_service.sign_up(sign_up_request)
    return sign_up_result, None


@api.route('/auth/issue_token', methods=['POST'])
@success_response
def issue_token():
    email_credential_req = EmailCredentialRequest(request_json=request.get_json())
    email_credential_res = auth_service.issue_token(email_credential_req)
    return email_credential_res, None


@api.route('/auth/phone_number/request_auth_number', methods=['POST'])
@success_response
def request_temporary_auth_number():
    """
    :return:
    :error:
        INVALID_ARGUMENT
        TEMPORARY_UNAVAILABLE
    """
    certify_request = CertifyPhoneNumberRequest(request_json=request.get_json())
    auth_service.request_auth_number(certify_request.phone_number)
    return True, None


@api.route('/auth/phone_number/certify_auth_number', methods=['POST'])
@success_response
def complete_auth_number():
    """
    :return:
    :error:
        INVALID_ARGUMENT
        INVALID_CERTIFICATION
    """
    certify_request = CertifyPhoneNumberRequest(request_json=request.get_json())
    auth_service.certify_auth_number(certify_request.phone_number, certify_request.auth_number)
    return True, None
