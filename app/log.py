# -*- coding: utf-8 -*-
class LogItem:
    def __init__(self, who=None, what=None, when=None, how=None):
        if who:
            self.who = who.__dict__
        if what:
            self.what = what.__dict__
        if when:
            self.when = when.__dict__
        if how:
            self.how = how.__dict__


class WhoLogItem:
    def __init__(self, request=None, user=None, id=None, email=None):
        if request:
            self.user_agent = request.headers.get('User-Agent')
        if user and isinstance(user, dict):
            self.id = user.get('id')
            self.email = user.get('email')
        else:
            self.id = id
            self.email = email


class WhatLogItem:
    def __init__(self, id=None, object_type=None):
        self.id = id
        self.object_type = object_type


class WhenLogItem:
    def __init__(self, millis=0):
        self.millis = millis


class HowLogItem:
    def __init__(self, action=None):
        self.action = action