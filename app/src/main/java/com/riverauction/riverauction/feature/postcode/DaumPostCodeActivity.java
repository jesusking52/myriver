package com.riverauction.riverauction.feature.postcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.APIConstant;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DaumPostCodeActivity extends AppCompatActivity {
    private static final String DAUM_POST_CODE_HTML = "postcode.html";

    @Bind(R.id.web_view) WebView webView;
    // 처음에만 onPageFinished 가 호출되게한다
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_post_code);

        ButterKnife.bind(this);

        getSupportActionBar().setTitle(R.string.location_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isFirstLoad) {
                    view.loadUrl("javascript:loadPostCodeDaum();");
                    isFirstLoad = false;
                }
            }
        });
        webView.getSettings().setSupportZoom(false);
        webView.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);
        webView.loadUrl(APIConstant.API_ENDPOINT.getUrl() + DAUM_POST_CODE_HTML);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
