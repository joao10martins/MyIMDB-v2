package com.example.myimdb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        WebView webView = findViewById(R.id.web_signUp);
        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webView.loadUrl("https://www.themoviedb.org/account/signup");
    }



    private class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // "https://www.themoviedb.org/"
            if (!url.contains("https://www.themoviedb.org/account/signup") || !url.contains("https://www.themoviedb.org/account/signup?language=en-US") ){
                Intent backToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(backToLogin);
            }
            view.loadUrl(url);
            return true;
        }
    }
}
