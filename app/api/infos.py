# -*- coding: utf-8 -*-
from app.api import api
from app.service import info_service
from app.decorators import success_response
from app.dto.common import SubjectGroupDTO
from app.utils import dtos


@api.route('/info/subjects', methods=['GET'])
@success_response
def get_subjects():
    subject_groups = info_service.get_subject_groups()
    dto_list = dtos(SubjectGroupDTO, subject_groups)
    return dto_list, None
