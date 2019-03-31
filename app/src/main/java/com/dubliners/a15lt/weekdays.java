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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekdays);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String uid = intent.getStringExtra("uid");
        Toast.makeText(weekdays.this, uid, Toast.LENGTH_SHORT).show();
    }


    public void launchVote(View view) {
        String clickedButton = view.getResources().getResourceEntryName(view.getId());
        Log.i("Clicked Button = ", clickedButton);
        Intent startVotingIntent = new Intent(weekdays.this, VoteActivity.class);
        startVotingIntent.putExtra("clickedButton", clickedButton);
        weekdays.this.startActivityForResult(startVotingIntent, VOTING_INTENT_REQUEST_CODE);
    }
}
