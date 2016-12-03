# -*- coding: utf-8 -*-
from app.constant import HttpStatus, ErrorCause


class RiverError(Exception):
    status_code = HttpStatus.INTERNAL_SERVER_ERROR
    error_cause = ErrorCause.UNKNOWN
    message = (
        'Unknown error'
    )

    def __init__(self, cause=None, extra_message=None, **words):
        if cause:
            self.error_cause = cause
        if words:
            try:
                self.message = self.message.format(**words)
            except KeyError:
                pass
        if extra_message:
            self.message = self.message + ' ' + extra_message


class ForbiddenError(RiverError):
    status_code = HttpStatus.FORBIDDEN
    error_cause = ErrorCause.TEMPORARY_UNAVAILABLE
    message = (
        '{message}'
    )


class BadRequestError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.BAD_REQUEST
    message = (
        '{message}'
    )


class InvalidArgumentError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.INVALID_ARGUMENT
    message = (
        'argument is insufficient.'
    )


class NoSuchElementError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.NO_SUCH_ELEMENT
    message = (
        'Element does not exist.'
    )


class ElementAlreadyExists(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.ELEMENT_ALREADY_EXISTS
    message = (
        'Element is already exists.'
    )


class InsufficientPermissionError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.INSUFFICIENT_PERMISSION
    message = (
        'insufficient permission'
    )


class InvalidTokenError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.INVALID_TOKEN
    message = (
        'Token is invalid.'
    )


class ExpiredTokenError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.EXPIRED_TOKEN
    message = (
        'Token is expired.'
    )


class TemporarilyUnavailable(RiverError):
    status_code = HttpStatus.FORBIDDEN
    error_cause = ErrorCause.TEMPORARY_UNAVAILABLE
    message = (
        'Service is temporarily unavailable.'
    )


class InsufficientCoins(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.INSUFFICIENT_COINS
    message = (
        'User has not enough coins.'
    )


class WrongReceiptError(RiverError):
    status_code = HttpStatus.BAD_REQUEST
    error_cause = ErrorCause.WRONG_RECEIPT
    message = (
        'It is wrong receipt or canceled receipt.'
    )
