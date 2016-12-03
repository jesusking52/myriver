package com.riverauction.riverauction.api.service.payment;

import com.riverauction.riverauction.api.model.CReceipt;
import com.riverauction.riverauction.api.service.APISuccessResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface PaymentService {

    @POST("/api/payment/coin_purchase")
    Observable<APISuccessResponse<Boolean>> purchaseCoin(@Body CReceipt receipt);
}
