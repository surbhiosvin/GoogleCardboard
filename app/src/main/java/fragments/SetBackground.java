package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.modla.andy.processingcardboard.CardBoardActivity;
import com.modla.andy.processingcardboard.Constants;
import com.modla.andy.processingcardboard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sandeep on 1/21/16.
 */
public class SetBackground extends Fragment {

    View rootView;
    Typeface face;
    ListView listview;
    MyAdapter mAdapter;
    ArrayList<Integer> imgList = new ArrayList<Integer>();
    SharedPreferences sp;
    String TAG = "SetBackground";
    private AsyncHttpClient client;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.set_background_fragment, container, false);
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


        listview = (ListView) rootView.findViewById(R.id.listview);

//        imgList.add(R.drawable.bg_one);
//        imgList.add(R.drawable.bg_two);
        imgList.add(R.drawable.bg_twofive);
//        imgList.add(R.drawable.bg_three);
//        imgList.add(R.drawable.bg_four);
        imgList.add(R.drawable.bg_five);
        imgList.add(R.drawable.bg_six);
        imgList.add(R.drawable.bg_seven);
        imgList.add(R.drawable.bg_eight);
        imgList.add(R.drawable.bg_nine);
        imgList.add(R.drawable.bg_twothree);
        imgList.add(R.drawable.bg_ten);
        imgList.add(R.drawable.bg_twozero);
        imgList.add(R.drawable.bg_oneone);
        imgList.add(R.drawable.bg_onetwo);
        imgList.add(R.drawable.bg_onethree);
        imgList.add(R.drawable.bg_onefour);
        imgList.add(R.drawable.bg_twotwo);
        imgList.add(R.drawable.bg_onefive);
        imgList.add(R.drawable.bg_twofour);
        imgList.add(R.drawable.bg_onesix);
        imgList.add(R.drawable.bg_oneseven);
        imgList.add(R.drawable.bg_oneeight);
        imgList.add(R.drawable.bg_onenine);
        imgList.add(R.drawable.bg_twosix);


        mAdapter = new MyAdapter(getActivity(), imgList);
        listview.setAdapter(mAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor e = sp.edit();
                e.putInt("background_value",imgList.get(i));
                Constants.BACKGROUND_VALUE = imgList.get(i);
                e.commit();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String enterTime = sdf.format(c.getTime());

                CallApiForStartMeditationTime(enterTime);
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;

        public MyAdapter(Context context,
                         ArrayList<Integer> list) {
            mInflater = LayoutInflater.from(getActivity());
        }


        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public Object getItem(int position) {
            return imgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;


            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item,
                        null);

                holder.background_img = (ImageView) convertView.findViewById(R.id.background_img);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.background_img.setImageResource(imgList.get(position));

            return convertView;
        }


        class ViewHolder {
           ImageView background_img;
        }

    }

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
