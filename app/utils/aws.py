# -*- coding: utf-8 -*-
import os
from boto.s3.key import Key
from boto.s3.connection import S3Connection

# seoul region 에 버킷을 생성한 경우 aws, boto 사이에 이상한 에러가 있음..
# 참조 https://github.com/boto/boto/issues/2916
os.environ['S3_USE_SIGV4'] = 'True'
ACL_PUBLIC_READ = 'public-read'


class AWSUploadOptions:
    def __init__(self, **kwargs):
        self.aws_access_key = kwargs.get('aws_access_key')
        self.aws_secret_key = kwargs.get('aws_secret_key')
        self.aws_bucket = kwargs.get('aws_bucket')
        self.aws_region_host = kwargs.get('aws_region_host')


def upload_file(options, target_file, s3_key):
    conn = S3Connection(options.aws_access_key,
                        options.aws_secret_key,
                        host=options.aws_region_host)
    bucket = conn.get_bucket(options.aws_bucket)
    k = Key(bucket=bucket, name=s3_key)
    k.set_contents_from_filename(target_file)
    k.set_acl(ACL_PUBLIC_READ)
    url_format = 'https://{bucket}.{host}/{key}'
    url = url_format.format(host=conn.server_name(),
                            bucket=bucket.name,
                            key=k.name)
    return url
