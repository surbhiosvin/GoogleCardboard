package com.modla.andy.processingcardboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sandeep on 1/8/16.
 */
public class SpeedSettingScreen extends Activity implements View.OnClickListener {

    TextView speed_tv;
    ImageView down_button, up_button;
    int SpeedValue = 5;
    Button play_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.speed_setting);

        inIt();
    }

    private void inIt() {
        speed_tv = (TextView) findViewById(R.id.speed_tv);
        down_button = (ImageView) findViewById(R.id.down_button);
        up_button = (ImageView) findViewById(R.id.up_button);
        play_bt = (Button) findViewById(R.id.play_bt);

        down_button.setOnClickListener(this);
        up_button.setOnClickListener(this);
        play_bt.setOnClickListener(this);

        speed_tv.setText(""+SpeedValue);
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
        }
    }

    private void GoToNextScreen() {
        Constants.TIME_VALUE = SpeedValue;
        Intent i = new Intent(SpeedSettingScreen.this, CardBoardActivity.class);
        startActivity(i);
    }

    /***
     * to set speed in speed_tv textview
     */
    private void changeTextValue() {
        speed_tv.setText(""+SpeedValue);
    }
}
