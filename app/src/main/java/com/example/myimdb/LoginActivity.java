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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.helpers.GsonRequest;
import com.example.myimdb.helpers.volley.GsonRequestT;
import com.example.myimdb.model.response.CreateRequestToken;
import com.example.myimdb.model.response.ValidateRequestToken;

import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {



    /* Variables */
    private RequestQueue mRequestQueue;
    private String mUrl;
    private String mRequestToken;
    EditText username;
    EditText password;
    CircularProgressButton btn_login;
    private String mUsername;
    private String mPassword;
    private int mStatusCode;
    private String mStatusMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.login_username_edittext);
        password = findViewById(R.id.login_password_edittext);
        btn_login = findViewById(R.id.btn_login);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    username.setHint("");
                else
                    username.setHint("Username");
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    password.setHint("");
                else
                    password.setHint("Password");
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.startAnimation();
                mUsername = username.getText().toString().trim();
                mPassword = password.getText().toString().trim();
                if (mUsername != null && mPassword != null){
                    createRequestToken();
                }





            }
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


    /*private void isLoginEmpty(String username, String password) {
        // alertDialog for empty text fields.
    }*/

    /*private void isLoginIncorrect(String username, String password) {
        // alertDialog for non-existent login.
    }*/

    private void isLoginCorrect(/*String username, String password*/) {
        // go to MainActivity and save user/pass or token.
        Intent login = new Intent(this, MainActivity.class);
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
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("request_token", token);

            GsonRequestT<ValidateRequestToken> request = new GsonRequestT<ValidateRequestToken>(mUrl,
                    ValidateRequestToken.class,
                    jsonObject,
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
                        //createNewSession();
                        if (mUsername != null && mPassword != null && mRequestToken != null){
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
                    } else {
                        mStatusCode = response.getStatus_code();
                        mStatusMessage = response.getStatus_message();
                        //alertDialog -> mStatusMessage
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
                error.printStackTrace();
            }
        };
    }

}
