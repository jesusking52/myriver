# -*- coding: utf-8 -*-
from app.models import SubjectGroup


def get_subject_groups():
    subject_groups = SubjectGroup.query.all()
    return subject_groups
