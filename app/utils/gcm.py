import requests
import json


class GCMOptions:
    def __init__(self, **kwargs):
        self.api_key = kwargs.get('api_key')
        self.data = kwargs.get('data')


def send_gcm_message(options):
    gcm_url = 'https://gcm-http.googleapis.com/gcm/send'
    headers = {
        'Authorization': 'key=%s' % options.api_key,
        'Content-type': 'application/json'
    }
    response = requests.post(gcm_url, headers=headers, data=json.dumps(options.data))
    return response
