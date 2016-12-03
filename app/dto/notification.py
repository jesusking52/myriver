class NotificationDTO:
    def __init__(self, notification):
        self.id = notification.id
        self.type = notification.type
        self.words = notification.words
        self.skeleton = notification.skeleton
        self.deep_link = notification.deep_link
        self.created_at = notification.created_at


class GCMDTO:
    def __init__(self, **kwargs):
        self.to = kwargs.get('to')
        self.data = vars(kwargs.get('data'))  # GCMDataDTO
        # self.notification = vars(kwargs.get('notification'))  # GCMNotificationDTO


class GCMDataDTO:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.message = kwargs.get('message')
        self.deep_link = kwargs.get('deep_link')
        self.icon_url = kwargs.get('icon_url')
        self.created_at = kwargs.get('created_at')


class GCMNotificationDTO:
    def __init__(self, **kwargs):
        self.title = kwargs.get('title')
        self.text = kwargs.get('text')
