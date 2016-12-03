# -*- coding: utf-8 -*-
import traceback
from flask import request
from app import logger
from app.api import api
from app.service import auth_service, coin_service
from app.decorators import success_response
from app.constant import ObjectType
from app.dto.common import Receipt
from app.utils import current_millis
from app.log import LogItem, WhoLogItem, WhatLogItem, WhenLogItem, HowLogItem


@api.route('/payment/coin_purchase', methods=['POST'])
@success_response
def coin_purchase():
    """
    :return:
    :error
        WRONG_RECEIPT
        ELEMENT_ALREADY_EXISTS
    """
    try:
        user_data = auth_service.check_permission()
        coin_service.charge_coins(user_data.get('id'), Receipt(request_json=request.get_json()))
        logger.debug(LogItem(who=WhoLogItem(request=request),
                             what=WhatLogItem(object_type=ObjectType.PAYMENT),
                             when=WhenLogItem(millis=current_millis()),
                             how=HowLogItem(action='COIN_PURCHASE')).__dict__)
    except:
        logger.error(traceback.format_exc())
        raise
    return True, None
