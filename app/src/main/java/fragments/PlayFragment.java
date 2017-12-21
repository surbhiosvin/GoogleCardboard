package fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.modla.andy.processingcardboard.CardBoardActivity;
import com.modla.andy.processingcardboard.Constants;
import com.modla.andy.processingcardboard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sandeep on 1/14/16.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {


    View rootView;
    Typeface face ;
    TextView speed_tv;
    ImageView down_button, up_button;
    int SpeedValue = 5;
    Button play_bt,select_background_bt;
    String TAG = "PlayFragment";
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    SharedPreferences sp;

    String enterTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.play_fragment, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        init();

        return  rootView ;
    }

    private void init() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        speed_tv = (TextView) rootView.findViewById(R.id.speed_tv);
        down_button = (ImageView) rootView.findViewById(R.id.down_button);
        up_button = (ImageView) rootView.findViewById(R.id.up_button);
        play_bt = (Button) rootView.findViewById(R.id.play_bt);
        select_background_bt = (Button) rootView.findViewById(R.id.select_background_bt);

        down_button.setOnClickListener(this);
        up_button.setOnClickListener(this);
        play_bt.setOnClickListener(this);
        select_background_bt.setOnClickListener(this);

        speed_tv.setTypeface(face);
        play_bt.setTypeface(face);
        select_background_bt.setTypeface(face);
        play_bt.setTypeface(face);


        int initialSpeedValue = sp.getInt("speed_value",5);
        SpeedValue = initialSpeedValue;

        speed_tv.setText("" + SpeedValue);
    }

    @Override
    public void onClick(View view) {
        if(view==down_button){
            if(SpeedValue==3){
                changeTextValue();
            } else {
                SpeedValue--;
                changeTextValue();
            }

        }
        else if(view==up_button){
            if(SpeedValue==20){
                changeTextValue();
            }
            else {
                SpeedValue++;
                changeTextValue();
            }

        }
        else if(view==play_bt){
            GoToNextScreen();
        } else if(view == select_background_bt){
            SetBackground();
        }

    }

    private void SetBackground() {
        Constants.TIME_VALUE = SpeedValue;
        SharedPreferences.Editor e = sp.edit();
        e.putInt("speed_value", SpeedValue);
        e.commit();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;
        fragment = new SetBackground();

        if (fragment != null) {
            ft.replace(R.id.frame_layout, fragment);
        } else {
            ft.add(R.id.frame_layout, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    private void GoToNextScreen() {

        SharedPreferences.Editor e = sp.edit();
        e.putInt("speed_value", SpeedValue);
        e.commit();

        Constants.TIME_VALUE = SpeedValue;
        Constants.BACKGROUND_VALUE = sp.getInt("background_value",R.drawable.background);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        enterTime = sdf.format(c.getTime());

        CallApiForStartMeditationTime(enterTime);
    }

    /**
     * to set speed_tv textview text
     */
    private void changeTextValue() {
        speed_tv.setText("" + SpeedValue);
    }


    /**
     * api to store starting time of meditation at backend
     * @param time
     */
    private void CallApiForStartMeditationTime(String time) {
        // http://phphosting.osvin.net/GoogleCardboard/WEB_API/meditation_info.php
        /*1.user_id
        2.enter_time-2015-12-22 12:54:09
        3.type-1*/

        RequestParams params = new RequestParams();
        params.put("user_id", Constants.USER_ID);
        params.put("type", "1");
        params.put("enter_time",time);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.MEDITATION_URL + "?" + params.toString());
        client.post(getActivity(), Constants.MEDITATION_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
              //  dialog.show();
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
                      //  {"ResponseCode":true,"Message":"Entry time Registered Successfully","Login Id":4}
                       Constants.MEDITATION_LOGIN_ID = response.getString("Login Id");
                        Intent i = new Intent(getActivity(), CardBoardActivity.class);
                        startActivity(i);

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

