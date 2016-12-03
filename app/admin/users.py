# -*- coding: utf-8 -*-
from flask import current_app, request
from app.admin import api
from app.decorators import success_response
from app.service import user_service, coin_service
from app.utils import dtos, StringUtils
from app.dto.user import UserDTO, CoinTransactionDTO
from app.constant import TransactionType


@api.route('/users', methods=['GET'])
@success_response
def get_users():
    options = user_service.GetUserOptions(email_pattern=request.args.get('email_pattern'),
                                          next_token=request.args.get('next_token'),
                                          per_page=current_app.config['PER_PAGE'])
    users, paging = user_service.get_users(options)
    dto_list = dtos(UserDTO, users, is_checked_phone_number=True)
    return dto_list, paging


@api.route('/users/<int:user_id>', methods=['GET'])
@success_response
def get_user(user_id):
    user = user_service.get_user(user_id)
    return UserDTO(user, is_checked_phone_number=True), None


@api.route('/users/<int:user_id>/coin_transactions', methods=['GET'])
@success_response
def get_user_coin_transactions(user_id):
    options = user_service.GetUserCoinTransactionOptions(user_id=user_id,
                                                         next_token=request.args.get('next_token'),
                                                         per_page=current_app.config['PER_PAGE'])
    transactions, paging = user_service.get_user_transactions(options)
    dto_list = dtos(CoinTransactionDTO, transactions)
    return dto_list, paging


@api.route('/users/<int:user_id>/charge_coins', methods=['POST'])
@success_response
def charge_coins(user_id):
    coins = StringUtils.to_int(request.get_json().get('coins'))
    if coins is not None and coins > 0:
        coin_service.add_coins(user_id, coins, TransactionType.ADMIN)
    return True, None
