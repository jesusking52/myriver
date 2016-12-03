package com.riverauction.riverauction.feature.profile.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.riverauction.riverauction.BuildConfig;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionConstant;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CReceipt;
import com.riverauction.riverauction.api.model.CShopItem;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.inapppurchase.util.IabHelper;
import com.riverauction.riverauction.inapppurchase.util.IabResult;
import com.riverauction.riverauction.inapppurchase.util.Inventory;
import com.riverauction.riverauction.inapppurchase.util.Purchase;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.recyclerview.DividerUtils;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class ShopActivity extends BaseActivity implements ShopMvpView {
    // Debug tag, for logging
    static final String TAG = "ShopActivity";

    static final String SKU_PURCHASED = "android.test.purchased";
    static final String SKU_CANCELED = "android.test.canceled";
    static final String SKU_REFUNDED = "android.test.refunded";
    static final String SKU_ITEM_UNAVAILABLE = "android.test.item_unavailable";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;
    @Inject ShopPresenter presenter;

    @Bind(R.id.shop_recycler_view) RecyclerView recyclerView;
    @Bind(R.id.shop_my_item_count) TextView myItemCount;

    private ShopItemAdapter adapter;
    private List<CShopItem> shopItems = Lists.newArrayList();

    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_shop;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        user = UserStates.USER.get(stateCtx);

        getSupportActionBar().setTitle(R.string.shop_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHelper = new IabHelper(this, RiverAuctionConstant.IN_APP_BILLING_KEY);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(result -> {
            Log.d(TAG, "Setup finished.");
            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                complain(getString(R.string.common_error_message_unknown));
                return;
            }
            if (mHelper == null) {
                return;
            }

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            mHelper.queryInventoryAsync(mGotInventoryListener);
        });

        if (BuildConfig.DEBUG) {
            // test 를 위한 skuID
            shopItems.add(makeShopItem(SKU_PURCHASED, -1, null, 999));
            shopItems.add(makeShopItem(SKU_CANCELED, -1, null, 999));
            shopItems.add(makeShopItem(SKU_REFUNDED, -1, null, 999));
            shopItems.add(makeShopItem(SKU_ITEM_UNAVAILABLE, -1, null, 999));
        }
        shopItems.add(makeShopItem(RiverAuctionConstant.SKU_ID_COST3000, 3, null, 3000));
        shopItems.add(makeShopItem(RiverAuctionConstant.SKU_ID_COST5000, 5, null, 5000));
        shopItems.add(makeShopItem(RiverAuctionConstant.SKU_ID_COST10000, 11, "10% 보너스", 10000));
        shopItems.add(makeShopItem(RiverAuctionConstant.SKU_ID_COST20000, 23, "15% 보너스", 20000));
        shopItems.add(makeShopItem(RiverAuctionConstant.SKU_ID_COST50000, 60, "20% 보너스", 50000));

        adapter = new ShopItemAdapter(shopItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = DividerUtils.getHorizontalDividerItemDecoration(context);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        presenter.getUser(user.getId());
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            if (BuildConfig.DEBUG) {
                consumeAsyncInInventory(inventory, SKU_PURCHASED);
                consumeAsyncInInventory(inventory, SKU_CANCELED);
                consumeAsyncInInventory(inventory, SKU_REFUNDED);
                consumeAsyncInInventory(inventory, SKU_ITEM_UNAVAILABLE);
            }
            consumeAsyncInInventory(inventory, RiverAuctionConstant.SKU_ID_COST3000);
            consumeAsyncInInventory(inventory, RiverAuctionConstant.SKU_ID_COST5000);
            consumeAsyncInInventory(inventory, RiverAuctionConstant.SKU_ID_COST10000);
            consumeAsyncInInventory(inventory, RiverAuctionConstant.SKU_ID_COST20000);
            consumeAsyncInInventory(inventory, RiverAuctionConstant.SKU_ID_COST50000);

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    private void consumeAsyncInInventory(Inventory inventory, String skuId) {
        Purchase gasPurchase = inventory.getPurchase(skuId);
        if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
            Log.d(TAG, "We have gas. Consuming it.");
            mHelper.consumeAsync(inventory.getPurchase(skuId), mConsumeFinishedListener);
            return;
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    Log.d(TAG, "ITEM_ALREADY_OWNED - start consuming this item");
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                }
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(getString(R.string.common_error_message_unknown));
                return;
            }

            Log.d(TAG, "Purchase successful.");
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                presenter.purchaseCoin(new CReceipt(purchase));
            } else {
                complain(getString(R.string.common_error_message_unknown));
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    public void purchaseItem(String skuId) {
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(this, skuId, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    private void setMyCoins(CUser user) {
        myItemCount.setText(getString(R.string.common_shop_item_count_unit, user.getCoins()));
    }

    private CShopItem makeShopItem(String skuId, Integer count, String bonus, Integer price) {
        CShopItem shopItem = new CShopItem();
        shopItem.setSkuId(skuId);
        shopItem.setCount(count);
        shopItem.setBonusDescription(bonus);
        shopItem.setPrice(price);
        return shopItem;
    }

    @Override
    public void successGetUser(CUser user) {
        this.user = user;
        setMyCoins(user);
    }

    @Override
    public boolean failGetUser(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successPurchaseCoin(Boolean result) {
        presenter.getUser(user.getId());
        Toast.makeText(context, R.string.shop_purchase_dialog_success_title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean failPurchaseCoin(CErrorCause errorCause) {
        if (CErrorCause.WRONG_RECEIPT == errorCause) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.shop_purchase_dialog_title)
                    .setMessage(R.string.common_error_message_unknown)
                    .setPositiveButton(R.string.common_button_ok, null)
                    .show();
            return true;
        }
        return false;
    }

    /**
     * ViewHolder
     */
    public static class ShopItemHolder extends RecyclerView.ViewHolder {
        public TextView countView;
        public TextView bonusDescriptionView;
        public TextView priceButton;

        public ShopItemHolder(View itemView) {
            super(itemView);

            countView = (TextView) itemView.findViewById(R.id.item_shop_count);
            bonusDescriptionView = (TextView) itemView.findViewById(R.id.item_shop_bonus_description);
            priceButton = (TextView) itemView.findViewById(R.id.item_shop_price_button);
        }
    }

    /**
     * Adapter
     */
    private class ShopItemAdapter extends RecyclerView.Adapter<ShopItemHolder> {
        private List<CShopItem> shopItems;

        public ShopItemAdapter(List<CShopItem> shopItems) {
            this.shopItems = shopItems;
        }

        @Override
        public ShopItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ShopItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_shop, parent, false));
        }

        @Override
        public void onBindViewHolder(ShopItemHolder holder, int position) {
            CShopItem shopItem = shopItems.get(position);
            if (shopItem.getSkuId().equals(SKU_PURCHASED)) {
                holder.countView.setText("결제 성공하는 테스트");
            } else if (shopItem.getSkuId().equals(SKU_CANCELED)) {
                holder.countView.setText("결제 실패하는 테스트");
            } else if (shopItem.getSkuId().equals(SKU_REFUNDED)) {
                holder.countView.setText("결제 환불된거 테스트");
            } else if (shopItem.getSkuId().equals(SKU_ITEM_UNAVAILABLE)) {
                holder.countView.setText("결제 불가능아이템 테스트");
            } else {
                holder.countView.setText(getString(R.string.common_shop_item_count_unit, shopItem.getCount()));
            }
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedString = formatter.format(shopItem.getPrice());
            holder.priceButton.setText(getString(R.string.common_shop_item_price_unit, formattedString));
            holder.priceButton.setOnClickListener(v -> {
                // Purchase and Consume
                new AlertDialog.Builder(context)
                        .setTitle(R.string.shop_purchase_dialog_title)
                        .setMessage(R.string.shop_purchase_dialog_message)
                        .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                            purchaseItem(shopItem.getSkuId());
                        })
                        .setCancelable(true)
                        .show();
            });

            if (!Strings.isNullOrEmpty(shopItem.getBonusDescription())) {
                holder.bonusDescriptionView.setText(shopItem.getBonusDescription());
                holder.bonusDescriptionView.setVisibility(View.VISIBLE);
            } else {
                holder.bonusDescriptionView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return shopItems.size();
        }
    }
}
