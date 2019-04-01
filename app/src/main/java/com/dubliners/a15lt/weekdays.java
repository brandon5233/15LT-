package com.dubliners.a15lt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class weekdays extends AppCompatActivity {
    private static final int VOTING_INTENT_REQUEST_CODE = 124;
    private String userDisplayName, uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekdays);
        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        uid = intent.getStringExtra("uid");
    }


    public void launchVote(View view) {
        String selectedDay = view.getResources().getResourceEntryName(view.getId());
        Log.i("Clicked Button = ", selectedDay);
        Intent startVotingIntent = new Intent(weekdays.this, VoteActivity.class);
        startVotingIntent.putExtra("userDisplayName", userDisplayName);
        startVotingIntent.putExtra("uid", uid);
        startVotingIntent.putExtra("selectedDay", selectedDay);
        weekdays.this.startActivityForResult(startVotingIntent, VOTING_INTENT_REQUEST_CODE);
    }
}
