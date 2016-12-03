from httplib2 import Http
from apiclient.discovery import build as google_service_build
from app import db, credentials
from app.models import User, CoinTransaction, Receipt
from app.constant import TransactionType
from app.dto.common import Paging, Purchase
from app.error import InvalidArgumentError, InsufficientCoins, ElementAlreadyExists, WrongReceiptError
from app.utils import current_millis, StringUtils


class GetCoinTransaction:
    def __init__(self, **kwargs):
        self.user_id = kwargs.get('user_id')
        self.next_token = StringUtils.to_int(kwargs.get('next_token'))
        self.per_page = kwargs.get('per_page')


def get_transactions(options):
    query = CoinTransaction.query.filter(CoinTransaction.user_id == options.user_id)
    offset = 0
    if options.next_token is not None:
        offset = options.next_token
    per_page = 0
    if options.per_page is not None:
        per_page = options.per_page
    transactions = query.order_by(CoinTransaction.created_at.desc()).offset(offset).limit(per_page).all()
    total_count = query.count()
    return transactions, Paging(total_count=total_count, next_token=offset + per_page)


def add_coins(user_id, coins, transaction_type):
    user = User.query.get(user_id)
    if user is None:
        raise InvalidArgumentError(extra_message='User does not exist.')
    c_transaction = CoinTransaction()
    c_transaction.user_id = user.id
    c_transaction.transaction_type = transaction_type
    c_transaction.value = coins
    c_transaction.created_at = current_millis()
    user.coins = user.coins + coins
    db.session.add(c_transaction)
    db.session.commit()


def subtract_coins(user_id, coins, transaction_type):
    user = User.query.get(user_id)
    if user is None:
        raise InvalidArgumentError(extra_message='User does not exist.')
    if user.coins < coins:
        raise InsufficientCoins()
    c_transaction = CoinTransaction()
    c_transaction.user_id = user_id
    c_transaction.transaction_type = transaction_type
    c_transaction.value = -coins
    c_transaction.created_at = current_millis()
    user.coins -= coins
    db.session.add(c_transaction)
    db.session.commit()


def check_purchase(receipt):
    http_auth = credentials.authorize(Http())
    google_service = google_service_build('androidpublisher', 'v2', credentials=credentials, http=http_auth)
    if receipt is None \
            or receipt.product_id is None \
            or receipt.package_name is None \
            or receipt.purchase_token is None:
        raise WrongReceiptError(extra_message='Receipt must not be null.')
    product = google_service.purchases().products().get(productId=receipt.product_id,
                                                        packageName=receipt.package_name,
                                                        token=receipt.purchase_token)
    try:
        purchase = Purchase(product.execute())
    except:
        return None
    # purchase_state 0 이면 구매 된 것, 1이면 cancel 된 것
    # consumption_state 0 이면 아직 consume이 되지 않았고, 1이면 consumed된 것.
    if purchase.purchase_state is not None and purchase.purchase_state == 0:
        return purchase
    return None


def charge_coins(user_id, receipt):
    # 신뢰성 있는 영수증인지 확인한다.
    purchase = check_purchase(receipt)
    if purchase is None:
        # wrong receipt
        raise WrongReceiptError()
    # 구글 결제 테스트 시에는 order_id 가 할당되지 않는다.
    # 타당한 receipt이지만 order_id가 없을 수도 있다.
    if receipt.order_id is not None and receipt.order_id.strip():
        saved_receipt = Receipt.query.filter(Receipt.order_id == receipt.order_id).first() 
        if saved_receipt is not None:
            # already handled
            raise ElementAlreadyExists()
        handled_receipt = Receipt()
        handled_receipt.user_id = user_id
        handled_receipt.order_id = receipt.order_id
        handled_receipt.package_name = receipt.package_name
        handled_receipt.product_id = receipt.product_id
        handled_receipt.purchase_time = receipt.purchase_time
        handled_receipt.purchase_state = receipt.purchase_state
        handled_receipt.purchase_token = receipt.purchase_token
        db.session.add(handled_receipt)
    try:
        coin = 0
        if receipt.product_id == 'android.test.purchased':
            coin = 100
        else:
            # product_id가 com.river_auction.coin.3 와 같은 형태로
            # 마지막 끝이 coin 갯수가 된다.
            coin = int(receipt.product_id.split('.')[-1])
        add_coins(user_id, coin, TransactionType.CHARGING)
    except:
        raise InvalidArgumentError(extra_message='product id is wrong')
    db.session.commit()
