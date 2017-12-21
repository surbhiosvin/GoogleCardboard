package com.modla.andy.processingcardboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;

import cz.msebera.android.httpclient.Header;


/**
 * Created by sandeep on 7/12/15.
 */
public class CardBoardActivity extends CardboardActivity implements
        CardboardView.StereoRenderer {

    static Vibrator mVibrator;

    private static final String TAG = "CardBoardActivity";


    static int mScore = 0;


    static Context ctx ;

   static  CardboardOverlayView mOverlayView;

    CardboardView cardboardView;

    private AsyncHttpClient client;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cardboard_activity);

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        ctx = CardBoardActivity.this;
         cardboardView = (CardboardView) findViewById(R.id.cardboard_view1);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
        cardboardView.setDistortionCorrectionEnabled(false);
        cardboardView.setChromaticAberrationCorrectionEnabled(false);
        setConvertTapIntoTrigger(true);


        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



           mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);


        mOverlayView.show3DImage(mScore++, CardBoardActivity.this);


       // mVibrator.vibrate(50);

    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {

    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {

        mOverlayView.show3DImage(mScore++, CardBoardActivity.this);

    //    mVibrator.vibrate(50);

        mOverlayView.setAnimation(CardBoardActivity.this);

    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exitTime = sdf.format(c.getTime());
        CallApiToExitMeditationTime(exitTime);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exitTime = sdf.format(c.getTime());
        CallApiToExitMeditationTime(exitTime);
    }

    /**
     * this api is called when user exit meditation screen
     * @param time
     */
    private void CallApiToExitMeditationTime(String time) {
        // http://phphosting.osvin.net/GoogleCardboard/WEB_API/meditation_info.php
        /*1.user_id
        2.exit_time-2015-12-22 12:54:09
        3.type-2
        4..speed
        5.login_id*/

        RequestParams params = new RequestParams();
        params.put("user_id", Constants.USER_ID);
        params.put("type", "2");
        params.put("exit_time",time);
        params.put("speed",""+ Constants.TIME_VALUE);
        params.put("login_id",Constants.MEDITATION_LOGIN_ID);


        Log.e("parameters", params.toString());
        Log.e("URL", Constants.MEDITATION_URL + "?" + params.toString());
        client.post(getApplicationContext(), Constants.MEDITATION_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
               // dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
               // dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
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
}
