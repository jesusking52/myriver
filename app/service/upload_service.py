# -*- coding: utf-8 -*-
import os
import uuid
from datetime import datetime
from wand.image import Image as WandImage
from app.utils import aws


class UploadResizeOptions:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.upload_folder = kwargs.get('upload_folder')
        self.aws_access_key = kwargs.get('aws_access_key')
        self.aws_secret_key = kwargs.get('aws_secret_key')
        self.aws_bucket = kwargs.get('aws_bucket')
        self.aws_region_host = kwargs.get('aws_region_host')


def upload_resize_image(options, request):
    """
    image를 resize해서 aws s3에 올린다.
    :param options:
    :param request:
    :return:
    """
    uploaded_temp_file = request.files['file']
    if uploaded_temp_file:
        filepath = os.path.join(options.upload_folder, str(uuid.uuid4()) + '.jpg')
        # upload folder가 없으면 생성한다.
        if not os.path.exists(os.path.dirname(filepath)):
            os.makedirs(os.path.dirname(filepath))
        uploaded_temp_file.save(filepath)

        resized_images = resize(filepath)

        date = datetime.now().isoformat().split('T')[0]
        images = []
        aws_options = aws.AWSUploadOptions(aws_access_key=options.aws_access_key,
                                           aws_secret_key=options.aws_secret_key,
                                           aws_bucket=options.aws_bucket,
                                           aws_region_host=options.aws_region_host)
        for resized_image in resized_images:
            url = aws.upload_file(aws_options,
                                  resized_image.get('source'),
                                  os.path.join(str(options.user_id),
                                               date,
                                               os.path.basename(resized_image.get('source'))))
            images.append({
                'width': resized_image.get('width'),
                'height': resized_image.get('height'),
                'source': url
            })
            os.remove(resized_image.get('source'))
    return images


def resize(original_filepath):
    """
    이미지를 30%, 60% 크기로 리사이즈한다.
    :param original_filepath:
    :return: 이미지 리스트 [30%, 60%, original image]
    """
    dirname = os.path.dirname(original_filepath)
    original_filename = os.path.basename(original_filepath)
    images = []
    with WandImage(filename=original_filepath) as image:
        original_size = image.size
        images.append({
            'width': original_size[0],
            'height': original_size[1],
            'source': original_filepath
        })
        for rate in 1, 2:
            with image.clone() as img:
                width = int(img.width * rate * 0.3)
                height = int(img.width * rate * 0.3)
                split_ext_filename = original_filename.rsplit('.', 1)
                resized_filename = ('.{0}x{1}.'.format(width, height)).join(split_ext_filename)
                resized_filepath = '/'.join([dirname, resized_filename])
                img.resize(width, height)
                img.save(filename=resized_filepath)
                images.append({
                    'width': width,
                    'height': height,
                    'source': resized_filepath
                })
    return images
