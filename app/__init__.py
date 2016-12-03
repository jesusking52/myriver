# -*- coding: utf-8 -*-
import os
import logging
from flask import Flask, request, render_template
from flask_sqlalchemy import SQLAlchemy
from flask.ext.compress import Compress
from celery import Celery
from app.error import RiverError
from app.decorators import error_response
from oauth2client.service_account import ServiceAccountCredentials

db = SQLAlchemy()
compress = Compress()

# TODO: celery broker: remove password
celery_app = Celery('app',
                    broker='amqp://guest:guest@localhost:5672',
                    # backend='redis://localhost:6379/0',
                    include=['app.service.notification_service'])
credentials = None

logger = logging.getLogger('APILogger')

formatter = logging.Formatter('[%(levelname)s|%(pathname)s:%(lineno)s] %(asctime)s > %(message)s')
file_handler = logging.FileHandler('./logs/api.log')
file_handler.setFormatter(formatter)
stream_handler = logging.StreamHandler()
stream_handler.setFormatter(formatter)

logger.addHandler(file_handler)
logger.addHandler(stream_handler)
logger.setLevel(logging.DEBUG)

# do this last to avoid circular dependencies
from app import models


def create_app(config_module=None):
    app = Flask(__name__)
    app.config.from_object(config_module or
                           os.environ.get('FLASK_CONFIG') or 'config')
    db.init_app(app)
    if app.config['COMPRESS']:
        compress.init_app(app)

    # Google 결제 확인을 위한 client credential
    global credentials
    google_client_key_path = os.path.dirname(os.path.abspath(__file__)) + '/../matchingtutor-google-client-key.json'
    credentials = ServiceAccountCredentials.from_json_keyfile_name(google_client_key_path,
                                                                   app.config['GOOGLE_CLIENT_SCOPES'])

    @app.errorhandler(Exception)
    @error_response
    def default_error_handler(exception):
        return exception

    @app.route('/')
    def index():
        token = request.cookies.get('m_t_atk')
        if token:
            user = models.User.verify_auth_token(token)
            if user:
                return render_template('index.html')
        return render_template('auth.html')

    if app.config['ADMIN']:
        from app.admin import api as admin_api
        app.register_blueprint(admin_api, url_prefix='/admin')

    from app.api import api
    app.register_blueprint(api, url_prefix='/api')

    return app
