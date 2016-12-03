from external import coolsms


class SMSOptions:
    def __init__(self, **kwargs):
        self.key = kwargs.get('key')
        self.secret = kwargs.get('secret')
        self.sender = kwargs.get('sender')
        self.to = kwargs.get('to')
        self.message = kwargs.get('message')


def send_sms(options):
    cool = coolsms.rest(api_key=options.key, api_secret=options.secret)
    status = cool.send(options.to, options.message, options.sender)
    return status
