package com.dubliners.a15lt;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class weekdays extends AppCompatActivity {
    private static final int VOTING_INTENT_REQUEST_CODE = 124;
    private String userDisplayName, uid;

    private  final Calendar calender = Calendar.getInstance();
    private final String week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));
    private final String day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    private final String current_year = String.valueOf(calender.get(Calendar.YEAR));
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekdays);
        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        uid = intent.getStringExtra("uid");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //change background of current day button
        //change background using states in styles
        int currentDayButtonId = getResources().getIdentifier(day_of_week, "id", getBaseContext().getPackageName());
        findViewById(currentDayButtonId).setBackgroundColor(getResources().getColor(R.color.colorAccentAlternate));

    }


    public void launchVote(View view) {
        vibrate();
        String selectedDay = view.getResources().getResourceEntryName(view.getId());
        Log.i("Clicked Button = ", selectedDay);
        Intent startVotingIntent = new Intent(weekdays.this, VoteActivity.class);
        startVotingIntent.putExtra("userDisplayName", userDisplayName);
        startVotingIntent.putExtra("uid", uid);
        startVotingIntent.putExtra("selectedDay", selectedDay);
        weekdays.this.startActivityForResult(startVotingIntent, VOTING_INTENT_REQUEST_CODE);
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(50);
        }
    }
}
