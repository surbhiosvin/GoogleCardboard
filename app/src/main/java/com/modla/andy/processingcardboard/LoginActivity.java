package com.modla.andy.processingcardboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by sandeep on 1/14/16.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    EditText email_edt;

    EditText name_edt;

    Button play_bt;

    Typeface face;
    Typeface face1;

    LinearLayout linear_layout;

    ImageView image;

    String TAG = "LoginActivity";

    private AsyncHttpClient client;
    private ProgressDialog dialog;

    Animation slideUp, fade_in;

    Boolean isConnected;

    SharedPreferences sp;

    TextView static_tv;

    String countryName, countryCode, androidAppVersion, androidDeviceType, androidOsVersion, androidDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        Intercom.initialize(getApplication(), "android_sdk-09a2f9f20e32d66431ade2080668a72b9cb662f6", "y9g4od6o");

        face = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        face1 = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        initialise();
    }

    private void initialise() {
        email_edt = (EditText) findViewById(R.id.email_edt);
        name_edt = (EditText) findViewById(R.id.name_edt);
        play_bt = (Button) findViewById(R.id.play_bt);
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);
        image = (ImageView) findViewById(R.id.image);
        static_tv = (TextView)findViewById(R.id.static_tv);

        email_edt.setTypeface(face1);
        name_edt.setTypeface(face1);
        play_bt.setTypeface(face1);
        static_tv.setTypeface(face);

        play_bt.setOnClickListener(this);

        boolean inHome = sp.getBoolean("inHome", false);

        getDetailsToSendAtBackend();

        if (inHome) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // We're logged in, we can register the user with Intercom
                    Intercom.client().registerIdentifiedUser(new Registration().withUserId(sp.getString("Id", "")));
                    Map userMap = new HashMap<>();
                    userMap.put("name", sp.getString("Name", ""));
                    userMap.put("email", sp.getString("Email", ""));
                    Intercom.client().updateUser(userMap);
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    image.startAnimation(slideUp);
                }
            }, 2000);


        }


        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                linear_layout.setVisibility(View.VISIBLE);
                static_tv.setVisibility(View.VISIBLE);
                linear_layout.startAnimation(fade_in);
                static_tv.startAnimation(fade_in);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * get phone details like os version , device name , device type
     */
    private void getDetailsToSendAtBackend() {

        countryCode = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        Locale loc = new Locale("", countryCode);
        countryName = loc.getDisplayCountry();
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        androidAppVersion = pinfo.versionName;

        androidDeviceType = android.os.Build.MODEL;
        androidOsVersion = Build.VERSION.RELEASE;

        androidDeviceName = android.os.Build.MANUFACTURER;

        Log.e(TAG, "Country_code=>" + countryCode + ", country_name=>" + countryName);
        Log.e(TAG, "androidAppVersion=>" + androidAppVersion);
        Log.e(TAG, "androidDeviceType=>" + androidDeviceType);
        Log.e(TAG, "androidOsVersion=>" + androidOsVersion);
        Log.e(TAG, "androidDeviceName=>" + androidDeviceName);
    }

    @Override
    public void onClick(View view) {
        if (view == play_bt) {
            CheckValidations();
        }
    }

    private void CheckValidations() {
        String email = email_edt.getText().toString();
        String name = name_edt.getText().toString();

        if (email.trim().length() < 1) {
            email_edt.setError("Please enter email address.");
        } else if (!isValidEmail(email)) {
            email_edt.setError("Please enter valid email address.");
        } else if (name.trim().length() < 1) {
            name_edt.setError("Please enter name.");
        } else {
            if (isConnected) {
                CallLoginAPI(name, email);
            } else {
                StringUtils.showDialog(Constants.NO_INTERNET, LoginActivity.this);
            }

        }
    }


    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * login APi to store username and email at backend
     * @param name
     * @param email
     */

    private void CallLoginAPI(String name, String email) {
        // http://phphosting.osvin.net/GoogleCardboard/WEB_API/sign_up.php?name=neha&email=neha@gmail.com

        /*  1.name-james
            2.email-abc@gmail.com
            3.country
            4.app_version
            5.device_type
            6.device_name
            7.android_version */

        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("email", email);
        params.put("country", countryName);
        params.put("app_version", androidAppVersion);
        params.put("device_type", androidDeviceType);
        params.put("android_version", androidOsVersion);
        params.put("device_name", androidDeviceName);


        Log.e("parameters", params.toString());
        Log.e("URL", Constants.SIGNUP_URL + "?" + params.toString());
        client.post(this, Constants.SIGNUP_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("Id", response.getString("Id"));
                        e.putString("Name", response.getString("Name"));
                        e.putString("Email", response.getString("Email"));
                        e.putString("Country", response.getString("Country"));
                        e.putString("App_version", response.getString("App_version"));
                        e.putString("device_type", response.getString("device_type"));
                        e.putString("Device_name", response.getString("Device_name"));
                        e.putString("Android_version", response.getString("Android_version"));
                        e.commit();

                        successfulLogin(sp.getString("Id", ""), sp.getString("Email", ""),
                                sp.getString("Name", ""));

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), LoginActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    private void successfulLogin(String id, String email, String name) {
        // Registering with Intercom is easy. For best results, use a unique user_id if you have one.
        Intercom.client().registerIdentifiedUser(new Registration().withUserId(id));
        // You can send attributes of any name/value
        Map userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        Intercom.client().updateUser(userMap);
    }

}
