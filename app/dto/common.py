# -*- coding: utf-8 -*-
from app.utils import StringUtils


class Location:
    def __init__(self, request_json):
        self.zip_code = StringUtils.to_int(request_json.get('zip_code'))
        self.address = request_json.get('address')
        self.sido = request_json.get('sido')
        self.sigungu = request_json.get('sigungu')
        self.bname = request_json.get('bname')
        self.latitude = StringUtils.to_float(request_json.get('latitude'))
        self.longitude = StringUtils.to_float(request_json.get('longitude'))


class Paging:
    def __init__(self, **kwargs):
        self.total_count = kwargs.get('total_count')
        self.next_token = None
        next_token = kwargs.get('next_token')
        if next_token < self.total_count:
            self.next_token = next_token


class Action:
    def __init__(self, **kwargs):
        self.type = kwargs.get('type')
        self.url = kwargs.get('url')


class SubjectGroupDTO:
    def __init__(self, subject_group):
        self.id = subject_group.id
        self.name = subject_group.name
        self.subjects = []
        for subject in subject_group.subjects:
            self.subjects.append(vars(SubjectDTO(subject)))


class SubjectDTO:
    def __init__(self, subject):
        self.group_id = subject.subject_group_id
        self.id = subject.id
        self.type = subject.type
        self.name = subject.name


# Google Play 결제 관련 객체들
class Receipt:
    def __init__(self, request_json):
        self.order_id = request_json.get('order_id')
        self.package_name = request_json.get('package_name')
        self.product_id = request_json.get('product_id')
        self.purchase_time = StringUtils.to_int(request_json.get('purchase_time'))
        self.purchase_state = StringUtils.to_int(request_json.get('purchase_state'))
        self.purchase_token = request_json.get('purchase_token')


class Purchase:
    """
    {
      "kind": "androidpublisher#productPurchase",
      "purchaseTimeMillis": long,
      "purchaseState": integer, // 0 이면 구매 된 것, 1이면 cancel 된것
      "consumptionState": integer, // 0 이면 아직 consume이 되지 않았고, 1이면 consumed된 것.
      "developerPayload": string
    }
    """

    def __init__(self, purchase):
        self.kind = purchase.get('kind')
        self.purchase_time_millis = StringUtils.to_int(purchase.get('purchaseTimeMillis'))
        self.purchase_state = StringUtils.to_int(purchase.get('purchaseState'))
        self.consumptionState = StringUtils.to_int(purchase.get('consumptionState'))
        self.deveoper_payload = purchase.get('developerPayload')
