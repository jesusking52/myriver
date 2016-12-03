# -*- coding: utf-8 -*-
from datetime import datetime


def is_all_attributes_not_none(obj):
    obj_dict = vars(obj)
    for key in obj_dict.keys():
        if obj_dict.get(key) is None:
            return False
    return True


def dtos(clazz, objects, **kwargs):
    dto_list = []
    for obj in objects:
        dto_list.append(clazz(obj, **kwargs))
    return dto_list


def current_millis():
    """
    모든 시간은 UTC로 계산한다.
    :return:
    """
    epoch = datetime.utcfromtimestamp(0)
    return int((datetime.utcnow() - epoch).total_seconds() * 1000)


class StringUtils:
    @staticmethod
    def to_int(str):
        if str is None:
            return None
        try:
            return int(str)
        except:
            return None

    @staticmethod
    def to_float(str):
        if str is None:
            return None
        try:
            return float(str)
        except:
            return None

    @staticmethod
    def to_bool(str):
        if str is None:
            return None
        try:
            if str == 'False' or str == 'false':
                return False
            return bool(str)
        except:
            return None

    @staticmethod
    def to_list(str):
        if str is None:
            return None
        try:
            str_list = [e.strip() for e in str.split(',')]
            return str_list
        except:
            return None


class ListUtils:
    @staticmethod
    def is_null_or_empty(data_list):
        if data_list is None:
            return True
        if len(data_list) == 0:
            return True
        return False

    @staticmethod
    def is_valid_form(data_list):
        if data_list is None:
            return False
        if not isinstance(data_list, list):
            return False
        if len(data_list) == 0:
            return False
        return True
