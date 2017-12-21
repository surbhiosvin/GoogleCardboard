package com.modla.andy.processingcardboard;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import fragments.FeedbackFragment;
import fragments.HomeFragment;
import fragments.InstructionFragment;
import fragments.PlayFragment;

/**
 * Created by sandeep on 1/14/16.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    SharedPreferences sp;

    ImageView home_img, feedback_img, play_img, instruction_img;

    String TAG = "HomeActivity";

    private AsyncHttpClient client;
    private ProgressDialog dialog;

    String enterTime;
    String exitTime;

    boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("inHome",true);
        e.commit();

        Constants.USER_ID = sp.getString("Id","");
        Constants.USER_NAME = sp.getString("Name","");
        Constants.USER_EMAIL = sp.getString("Email","");
        
        initialise();
    }

    private void initialise() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        home_img = (ImageView) findViewById(R.id.home_img);
        feedback_img = (ImageView) findViewById(R.id.feedback_img);
        play_img = (ImageView) findViewById(R.id.play_img);
        instruction_img = (ImageView) findViewById(R.id.instruction_img);

        home_img.setOnClickListener(this);
        feedback_img.setOnClickListener(this);
        play_img.setOnClickListener(this);
        instruction_img.setOnClickListener(this);

        home_img.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        feedback_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        play_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        instruction_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        Fragment fragment = null;
        fragment = new HomeFragment();
        addInitialFragment(fragment);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        enterTime = sdf.format(c.getTime());

        CallAPIToEnterInsideApp(enterTime,"1");
    }


    @Override
    public void onClick(View view) {
        if(view==home_img){

            home_img.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            feedback_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            play_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            instruction_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            Fragment fragment = null;
            fragment = new HomeFragment();
            addInitialFragment(fragment);

        } else if(view==feedback_img){

            home_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            feedback_img.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            play_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            instruction_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            Fragment fragment = null;
            fragment = new FeedbackFragment();
            addInitialFragment(fragment);

          /*  Intercom.client().displayMessageComposer();
            Intercom.client().displayConversationsList();*/

        } else if(view==play_img){

            home_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            feedback_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            play_img.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            instruction_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            Fragment fragment = null;
            fragment = new PlayFragment();
            addInitialFragment(fragment);

        } else if(view==instruction_img){

            home_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            feedback_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            play_img.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            instruction_img.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            Fragment fragment = null;
            fragment = new InstructionFragment();
            addInitialFragment(fragment);
        }
    }

    /**
     * to add initial fragment
     * @param f
     */

    private void addInitialFragment(Fragment f) {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = f;

        if (fragment != null) {
            ft.replace(R.id.frame_layout, fragment);
        } else {
            ft.add(R.id.frame_layout, fragment);
        }
        ft.commit();
    }

    /**
     * api called wehn user enter the app
     * @param time
     * @param type
     */
    private void CallAPIToEnterInsideApp(String time,String type) {
      //  http://phphosting.osvin.net/GoogleCardboard/WEB_API/app_log.php
        /*login....type =1

        1.user_id
        2.entry_date_time
        3.type

        logout...type = 2

        1.user_id
        2.login_id
        3.exit_date_time
        4.type*/


        RequestParams params = new RequestParams();
        params.put("user_id", Constants.USER_ID);
        params.put("type", type);
        if(isExit){
            params.put("login_id", Constants.LOGIN_ID);
            params.put("exit_date_time",time);
        }
        else {
            params.put("entry_date_time",time);
        }

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.ENTER_EXIT_URL + "?" + params.toString());
        client.post(this, Constants.ENTER_EXIT_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //   dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //  dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
                        //    {"ResponseCode":true,"Message":"login date&time Registered Successfully","Login Id":20}

                        if (!isExit) {
                            SharedPreferences.Editor e = sp.edit();
                            e.putString("login_id", response.getString("Login Id"));
                            e.commit();
                            Constants.LOGIN_ID = sp.getString("login_id", "");


                        }

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





    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        isExit = true;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        exitTime = sdf.format(c.getTime());

        CallAPIToEnterInsideApp(exitTime, "2");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            getSupportFragmentManager().popBackStack();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
