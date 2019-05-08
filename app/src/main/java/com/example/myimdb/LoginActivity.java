package com.example.myimdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.helpers.GsonRequest;
import com.example.myimdb.helpers.GsonRequestPost;
import com.example.myimdb.helpers.volley.GsonRequestT;
import com.example.myimdb.model.response.CreateRequestToken;
import com.example.myimdb.model.response.CreateSessionId;
import com.example.myimdb.model.response.ValidateRequestToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.StringTokenizer;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {



    /* Variables */
    private RequestQueue mRequestQueue;
    private String mUrl;
    private String mRequestToken;
    EditText username;
    EditText password;
    TextView sign_up;
    CircularProgressButton btn_login;
    private String mUsername;
    private String mPassword;
    private int mStatusCode;
    private String mStatusMessage;
    private String mSessionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.login_username_edittext);
        password = findViewById(R.id.login_password_edittext);
        btn_login = findViewById(R.id.btn_login);
        sign_up = findViewById(R.id.newAccount_signUp);

        username.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                username.setHint("");
            else
                username.setHint("Username");
        });

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                password.setHint("");
            else
                password.setHint("Password");
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.startAnimation();
                mUsername = username.getText().toString().trim();
                mPassword = password.getText().toString().trim();
                if (mUsername != null && mPassword != null){
                    createRequestToken();
                    //isLoginCorrect();
                    InputMethodManager inputManager =(InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });


        sign_up.setOnClickListener(v -> {
            // go to SignUp webpage.
            Intent signUp = new Intent(this, SignUpActivity.class);
            startActivity(signUp);
        });



    }

    // Converts a Vector image resource into a Bitmap.
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }




    private void isLoginCorrect(/*String username, String password*/) {
        // go to MainActivity and save user/pass or token.
        Intent login = new Intent(this, MainActivity.class);
        login.putExtra("session_id", mSessionId);
        startActivity(login);
    }


    private void createRequestToken() {

        mRequestQueue = Volley.newRequestQueue(this);

        try {
            mUrl = "https://api.themoviedb.org/3/authentication/token/new?api_key=07d93ad59393a99fe6bc8c1b8f0de23b";

            GsonRequest<CreateRequestToken> request = new GsonRequest<>(mUrl,
                    CreateRequestToken.class,
                    getTokenSuccessListener(),
                    getErrorListener());

            mRequestQueue.add(request);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private Response.Listener<CreateRequestToken> getTokenSuccessListener() {
        return new Response.Listener<CreateRequestToken>() {
            @Override
            public void onResponse(CreateRequestToken response) {
                try {
                    mRequestToken = response.getRequest_token();

                    validateRequestToken(mUsername, mPassword, mRequestToken);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void validateRequestToken(String username, String password, String token) {

        mRequestQueue = Volley.newRequestQueue(this);

        try {
            mUrl = "https://api.themoviedb.org/3/authentication/token/validate_with_login?api_key=07d93ad59393a99fe6bc8c1b8f0de23b";


            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("request_token", token);


            GsonRequestPost<ValidateRequestToken> request = new GsonRequestPost<ValidateRequestToken>(mUrl,
                    ValidateRequestToken.class,
                    params,
                    getValidateTokenSuccessListener(),
                    getErrorListener());

            mRequestQueue.add(request);
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    private Response.Listener<ValidateRequestToken> getValidateTokenSuccessListener() {
        return new Response.Listener<ValidateRequestToken>() {
            @Override
            public void onResponse(ValidateRequestToken response) {
                try {
                    if (response.isSuccess()){
                        mRequestToken = response.getRequest_token(); // token validated

                        if (mUsername != null && mPassword != null && mRequestToken != null){
                            createSessionId(mRequestToken);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }




    private void createSessionId(String token) {

        mRequestQueue = Volley.newRequestQueue(this);

        try {
            mUrl = "https://api.themoviedb.org/3/authentication/session/new?api_key=07d93ad59393a99fe6bc8c1b8f0de23b";

            // Post params to be sent to the server
            HashMap<String, String> params = new HashMap<>();
            params.put("request_token", token);



            GsonRequestPost<CreateSessionId> request = new GsonRequestPost<>(mUrl,
                    CreateSessionId.class,
                    params,
                    getSessionIdSuccessListener(),
                    getErrorListener());

            mRequestQueue.add(request);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Response.Listener<CreateSessionId> getSessionIdSuccessListener() {
        return new Response.Listener<CreateSessionId>() {
            @Override
            public void onResponse(CreateSessionId response) {
                try {
                    if (response.isSuccess()){
                        mSessionId = response.getSession_id(); // get session id

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    // do something later
                                    btn_login.doneLoadingAnimation(R.color.colorPrimaryDark, getBitmapFromVectorDrawable(LoginActivity.this, R.drawable.ic_done_yellow_24dp));
                                    // if user/pass exists, go to MainActivity
                                    // code here
                                    isLoginCorrect();
                                    // else
                                    // create alertDialog and revert button animation
                                }
                            }, 4500);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                String message;
                String responseBody;
                JSONObject data;


                btn_login.revertAnimation();
                btn_login.setBackground(getResources().getDrawable(R.drawable.login_button)); //@drawable/login_button

                    try {
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            switch(response.statusCode){
                                case 400:
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    data = new JSONObject(responseBody);

                                    message = data.getString("status_message");
                                    errorDialog(message);
                                    break;
                                case 401:
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    data = new JSONObject(responseBody);

                                    message = data.getString("status_message");
                                    errorDialog(message);
                                    break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

        };
    }


    private void errorDialog(String message) {

        final View dialogView = getLayoutInflater().inflate(R.layout.custom_login_error_alert_dialog, (ViewGroup) getWindow().getDecorView().getRootView(), false);
        TextView errorMessage = dialogView.findViewById(R.id.login_failed_message_textView);


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(true);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        errorMessage.setText(message);
        alertDialog.show();

        // Cancel Button
        final Button cancel = dialogView.findViewById(R.id.buttonCancel);
        cancel.setOnClickListener(v -> {
            //isCancel = !isCancel;
            alertDialog.dismiss();
        });


        // Retry button
        final Button retry = dialogView.findViewById(R.id.buttonRetry);
        retry.setOnClickListener(v -> {
            mUsername = null;
            mPassword = null;
            username.setText("");
            password.setText("");

            username.requestFocus();
            InputMethodManager inputManager =(InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            alertDialog.dismiss();
        });
    }

}
