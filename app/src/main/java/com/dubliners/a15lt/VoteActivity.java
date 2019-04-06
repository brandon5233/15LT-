package com.dubliners.a15lt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class VoteActivity extends AppCompatActivity {
    private static final String TAG = "Database Writer";
    private  final Calendar calender = Calendar.getInstance();
    private final String week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));
    private final String day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    private final String current_year = String.valueOf(calender.get(Calendar.YEAR));
    private final String collectionID = current_year+".week"+week_of_year;
    private final String COLLECTION_DISHES = "Dishes";
    private final int MAX_CARDS = 6;
    private SwipeRefreshLayout swipeRefreshLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userDisplayName, uid, selectedDay;
    LinearLayout linear_layout_hasDishes, linear_layout_noDishes;
    QuerySnapshot documentList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        linear_layout_hasDishes = findViewById(R.id.linear_layout_hasDishes);
        linear_layout_noDishes = findViewById(R.id.linear_layout_noDishes);
        linear_layout_hasDishes.setVisibility(View.GONE);

        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        uid = intent.getStringExtra("uid");
        selectedDay = intent.getStringExtra("selectedDay");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(selectedDay + "\'s Dishes");
        setSupportActionBar(toolbar);


        getDishesFromServer();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                if(documentList!=null && documentList.size()<6){
                    showDialogBox();
                }
                else{
                    Toast.makeText(VoteActivity.this, "Too many dishes !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDishesFromServer();
                swipeRefreshLayout.setRefreshing(false);
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
                dishName = WordUtils.capitalizeFully(dishName);
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

        Log.d("Current Week:" , week_of_year);
        Log.d("Current Day:" , day_of_week);
        Log.d("Current year:" , current_year);

        Dish dish = new Dish();
        dish.setCreator(userDisplayName);
        dish.setCreator_uid(uid);
        dish.setDishName(dishName);
        dish.setVoteCount("1");
        dish.setVoterList(Arrays.asList(uid));
        db.collection(collectionID)
                .document(selectedDay)
                .collection(COLLECTION_DISHES)
                .document(dishName)
                .set(dish)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(VoteActivity.this, dishName + " added !", Toast.LENGTH_SHORT).show();
                        getDishesFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void getDishesFromServer(){
        db.collection(collectionID).document(selectedDay)
                .collection(COLLECTION_DISHES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            documentList = task.getResult();
                            displayCards();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void displayCards() {
        //set all cards to invisible


        for(int i=0;i<MAX_CARDS;i++){
            linear_layout_hasDishes.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        if(documentList!=null){

            if(documentList.size()==0){
                linear_layout_hasDishes.setVisibility(View.GONE);
                linear_layout_noDishes.setVisibility(View.VISIBLE);
            }
            else{
                linear_layout_hasDishes.setVisibility(View.VISIBLE);
                linear_layout_noDishes.setVisibility(View.GONE);
                Log.d(TAG, "list size" + String.valueOf(documentList.size()) );
                //Display each document as a card
                int counter = 0;
                for (final QueryDocumentSnapshot document : documentList) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    final Dish dish = document.toObject(Dish.class);

                    CardView card = (CardView)linear_layout_hasDishes.getChildAt(counter);


                    card.setVisibility(View.VISIBLE);
                    LinearLayout innerHorizontalLayout = (LinearLayout)card.getChildAt(0);
                    LinearLayout innerVerticalLayout = (LinearLayout) innerHorizontalLayout.getChildAt(1);
                    TextView dishName = (TextView) innerVerticalLayout.getChildAt(0);
                    TextView voteCount = (TextView) innerVerticalLayout.getChildAt(1);
                    Button buttonplus = (Button) innerHorizontalLayout.getChildAt(0);
                    Button buttonminus = (Button) innerHorizontalLayout.getChildAt(2);

                    dishName.setText(dish.getDishName());
                    voteCount.setText(dish.getVoteCount());

                    buttonplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dish.getVoterList().contains(uid)){
                                Toast.makeText(VoteActivity.this, "You've already voted for that", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                upVote(document.getId(), dish.getVoteCount());
                            }

                        }
                    });

                    buttonminus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dish.getVoterList().contains(uid)){
                                downVote(document.getId(), dish.getVoteCount());
                            }
                        }
                    });
                    counter = (counter<=4)?counter+1:5;
                }
            }


        }
        else{
            linear_layout_hasDishes.setVisibility(View.GONE);
            linear_layout_noDishes.setVisibility(View.VISIBLE);
        }

    }

    private void downVote(String documentId, String currentVoteCount) {
        DocumentReference dishReference =
                db.collection(collectionID).document(selectedDay)
                        .collection(COLLECTION_DISHES)
                        .document(documentId);

        dishReference.update("voterList", FieldValue.arrayRemove(uid));
        dishReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) - 1 ));
        getDishesFromServer();
    }

    private void upVote(String documentId, String currentVoteCount) {
        DocumentReference dishReference =
        db.collection(collectionID).document(selectedDay)
                .collection(COLLECTION_DISHES)
                .document(documentId);

        dishReference.update("voterList", FieldValue.arrayUnion(uid));
        dishReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) + 1 ));
        getDishesFromServer();
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
