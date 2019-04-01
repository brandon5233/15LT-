package com.dubliners.a15lt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VoteActivity extends AppCompatActivity {
    private static final String TAG = "Database Writer";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userDisplayName, uid, selectedDay;
    private final String COLLECTION_DISHES = "Dishes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        uid = intent.getStringExtra("uid");
        selectedDay = intent.getStringExtra("selectedDay");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
              showDialogBox();
            }
        });
    }

    private void showDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
        builder.setTitle("What's cooking?");

        // Set up the input
        final EditText input = new EditText(VoteActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dishName = input.getText().toString();
                hideKeyboard();
                if(dishName.trim().equals("")){
                    Toast.makeText(VoteActivity.this, "We can't vote for a blank dish!", Toast.LENGTH_SHORT).show();
                    return;
                }

                addNewDish(dishName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                hideKeyboard();
            }
        });

        builder.show();
        showKeyboard();
    }

    private void addNewDish(final String dishName) {
        Calendar calender = Calendar.getInstance();
        String week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));
        String day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String current_year = String.valueOf(calender.get(Calendar.YEAR));
        String collectionID = current_year+".week"+week_of_year;
        Log.d("Current Week:" , week_of_year);
        Log.d("Current Day:" , day_of_week);
        Log.d("Current year:" , current_year);
        Map<String, Object> newDish = new HashMap<>();
        newDish.put("creator", userDisplayName);
        newDish.put("creator_uid", uid);
        newDish.put("dishName", dishName);
        db.collection(collectionID)
                .document(selectedDay)
                .collection(COLLECTION_DISHES)
                .document(dishName)
                .set(newDish)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(VoteActivity.this, dishName + " added !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        db.collection(collectionID).document(selectedDay)
                .collection(COLLECTION_DISHES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            displayCards(task.getResult());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void displayCards(QuerySnapshot documentList) {
        ListView
        //set all cards to invisible

        Log.d(TAG, "list size" + String.valueOf(documentList.size()) );

        if(documentList.size()>0){
            //Display each document as a card
            for (QueryDocumentSnapshot document : documentList) {
                Log.d(TAG, document.getId() + " => " + document.getData());
            }
        }

    }

    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
