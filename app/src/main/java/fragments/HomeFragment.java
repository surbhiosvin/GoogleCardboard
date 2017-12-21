package fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.modla.andy.processingcardboard.Constants;
import com.modla.andy.processingcardboard.LoginActivity;
import com.modla.andy.processingcardboard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sandeep on 1/14/16.
 */
public class HomeFragment extends Fragment {

    View rootView;
    Typeface face;
    TextView congo_tv, static_tv;
    ImageView logout_img;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    SharedPreferences sp;

    String TAG = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        face = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        init();

        return rootView;
    }

    private void init() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        congo_tv = (TextView) rootView.findViewById(R.id.congo_tv);
        logout_img = (ImageView) rootView.findViewById(R.id.logout_img);
        static_tv = (TextView) rootView.findViewById(R.id.static_tv);

        congo_tv.setTypeface(face);
        static_tv.setTypeface(face);

        CallApitogetTotalMeditationTime();

        logout_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLayoutDailog("Are you sure you want to logout ?");
            }
        });
    }

    /**
     * APi to get total meditation time user medtiated so far
     */
    private void CallApitogetTotalMeditationTime() {

        //   http://phphosting.osvin.net/GoogleCardboard/WEB_API/countmeditationtime.php
        //user_id

        RequestParams params = new RequestParams();
        params.put("user_id", Constants.USER_ID);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.MEDITATION_TIME_URL + "?" + params.toString());
        client.post(getActivity(), Constants.MEDITATION_TIME_URL, params, new JsonHttpResponseHandler() {

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
                        String meditationTime = response.getString("Result");
                        congo_tv.setText("congratulations you have meditated for " + meditationTime);

                    } else {
                        congo_tv.setText("Start meditating today.");
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

    protected void showLayoutDailog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);
        TextView static_tv, message;
        Button yes_bt, no_bt;

        dialog.setContentView(R.layout.logout_dialog);
        static_tv = (TextView) dialog.findViewById(R.id.static_tv);
        message = (TextView) dialog.findViewById(R.id.message);
        yes_bt = (Button) dialog.findViewById(R.id.yes_bt);
        no_bt = (Button) dialog.findViewById(R.id.no_bt);

        static_tv.setTypeface(face);
        message.setTypeface(face);
        yes_bt.setTypeface(face);
        no_bt.setTypeface(face);

        message.setText(msg);

        dialog.show();

        no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //    logout_tv.setEnabled(true);
            }
        });

        yes_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //    logout_tv.setEnabled(true);
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("inHome", false);
                e.commit();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });

    }
}
