package com.riverauction.riverauction.feature.profile.shop;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.MvpView;

public interface ShopMvpView extends MvpView {
    void successGetUser(CUser user);
    boolean failGetUser(CErrorCause errorCause);

    void successPurchaseCoin(Boolean result);
    boolean failPurchaseCoin(CErrorCause errorCause);
}
