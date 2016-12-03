package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.inapppurchase.util.Purchase;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CReceipt {
    public CReceipt() {
    }

    public CReceipt(Purchase purchase) {
        if (purchase == null) {
            return;
        }
        setOrderId(purchase.getOrderId());
        setPackageName(purchase.getPackageName());
        setSku(purchase.getSku());
        setPurchaseTime(purchase.getPurchaseTime());
        setPurchaseState(purchase.getPurchaseState());
        setToken(purchase.getToken());
    }

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("package_name")
    private String packageName;

    @JsonProperty("product_id")
    private String sku;

    @JsonProperty("purchase_time")
    private Long purchaseTime;

    @JsonProperty("purchase_state")
    private Integer purchaseState;

    @JsonProperty("purchase_token")
    private String token;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Integer getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(Integer purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
