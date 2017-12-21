package fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.modla.andy.processingcardboard.Constants;
import com.modla.andy.processingcardboard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import utils.StringUtils;

/**
 * Created by sandeep on 1/14/16.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Typeface face;
    EditText feedback_edt;
    Button submit_bt;
    String TAG = "FeedbackFragment";
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    SharedPreferences sp;
    Spinner option_spinner;
    LinearLayout linear_layout;
    MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.feedback_fragment, container, false);
        face = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        init();

        return rootView;
    }

    private void init() {
        feedback_edt = (EditText) rootView.findViewById(R.id.feedback_edt);
        submit_bt = (Button) rootView.findViewById(R.id.submit_bt);
        linear_layout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
        option_spinner = (Spinner) rootView.findViewById(R.id.option_spinner);

        linear_layout.setVisibility(View.GONE);

        feedback_edt.setTypeface(face);
        submit_bt.setTypeface(face);

        submit_bt.setOnClickListener(this);

        ArrayList<String> menuItems = new ArrayList<>();
        menuItems.add("- Select option -");
        menuItems.add("Suggestions");
        menuItems.add("Technical issues");
        menuItems.add("Delete user info");
        menuItems.add("Other");

        mAdapter = new MyAdapter(getActivity(),
                menuItems);
        option_spinner.setAdapter(mAdapter);

        option_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    linear_layout.setVisibility(View.VISIBLE);
                } else if (i == 3) {
                    linear_layout.setVisibility(View.GONE);
                    showLayoutDailog("Are you sure you want to delete user info ?");
                } else {
                    linear_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == submit_bt) {
            String feedback = feedback_edt.getText().toString();
            if (feedback.trim().length() < 1) {
                feedback_edt.setError("Please enter feedback.");
            } else {
                CallFeedbackAPI(feedback);
            }
        }
    }

    /**
     * to send user feedback at backend
     *
     * @param feedback
     */
    private void CallFeedbackAPI(String feedback) {
        // http://phphosting.osvin.net/GoogleCardboard/WEB_API/feedback.php
        //1. user_id-
        //2. comment-

        RequestParams params = new RequestParams();
        params.put("comment", feedback);
        params.put("user_id", sp.getString("Id", ""));
        params.put("type", option_spinner.getSelectedItemPosition()+"");

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.FEEDBACK_URL + "?" + params.toString());
        client.post(getActivity(), Constants.FEEDBACK_URL, params, new JsonHttpResponseHandler() {

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
                        StringUtils.showDialog(response.getString("message"), getActivity());
                        feedback_edt.setText("");
                    } else {
                        StringUtils.showDialog(response.getString("message"), getActivity());
                        feedback_edt.setText("");
                    }
                } catch (Exception e) {
                    feedback_edt.setText("");
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

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;

        ArrayList<String> menuItems = new ArrayList<String>();

        public MyAdapter(Context context,
                         ArrayList<String> menuList) {
            mInflater = LayoutInflater.from(getActivity());
            menuItems = menuList;

        }


        @Override
        public int getCount() {

            return menuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item, true);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item, false);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent, int spinnerRow, boolean isDefaultRow) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(spinnerRow, parent, false);
            TextView txt = (TextView) row.findViewById(R.id.text);

            txt.setText(menuItems.get(position));
            txt.setTypeface(face);

            return row;
        }

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

        static_tv.setText("Delete");


        message.setText(msg);

        dialog.show();

        no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                option_spinner.setSelection(0);
            }
        });

        yes_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                CallAPIToDeleteUserInfo();

            }
        });


    }

    /***
     * API to delete user info from backend
     */

    private void CallAPIToDeleteUserInfo() {
        // http://phphosting.osvin.net/GoogleCardboard/WEB_API/delete_info.php
        //1. user_id-

        RequestParams params = new RequestParams();
        params.put("user_id", sp.getString("Id", ""));

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.DELETE_INFO_URL + "?" + params.toString());

        client.post(getActivity(), Constants.DELETE_INFO_URL, params, new JsonHttpResponseHandler() {

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
                        StringUtils.showDialog(response.getString("Message"), getActivity());

                    } else {
                        StringUtils.showDialog(response.getString("Message"), getActivity());
                    }
                } catch (Exception e) {
                    feedback_edt.setText("");
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
