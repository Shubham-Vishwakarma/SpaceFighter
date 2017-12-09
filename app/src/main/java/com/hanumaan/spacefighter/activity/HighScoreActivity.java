package com.hanumaan.spacefighter.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hanumaan.spacefighter.R;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        TextView score0 = (TextView)findViewById(R.id.score0);
        TextView score1 = (TextView)findViewById(R.id.score1);
        TextView score2 = (TextView)findViewById(R.id.score2);
        TextView score3 = (TextView)findViewById(R.id.score3);

        SharedPreferences sharedPreferences = getSharedPreferences("SPACE_FIGHTER",MODE_PRIVATE);

        //setting the values
        score0.setText("1. " + sharedPreferences.getInt("score0",0));
        score1.setText("2. " + sharedPreferences.getInt("score1",0));
        score2.setText("3. " + sharedPreferences.getInt("score2",0));
        score3.setText("4. " + sharedPreferences.getInt("score3",0));
    }
}
