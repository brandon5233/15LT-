/*package com.dubliners.a15lt;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class vote_movie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_movie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
*/

package com.dubliners.a15lt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
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

public class vote_movies extends AppCompatActivity {
    private static final String TAG = "Database Writer";

    //private  final Calendar calender = Calendar.getInstance();
    //private final String week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));
    //private final String day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    //private final String current_year = String.valueOf(calender.get(Calendar.YEAR));
    //private final String collectionID = current_year+".week"+week_of_year;
    private final String COLLECTION_NAME = "Movies";
    private final int MAX_CARDS = 6;
    private SwipeRefreshLayout swipeRefreshLayout;

    Vibrator vibrator;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userDisplayName, uid;
    LinearLayout linear_layout_hasDishes, linear_layout_noDishes;
    QuerySnapshot documentList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_movie);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);
        linear_layout_hasDishes = findViewById(R.id.linear_layout_hasMovies);
        linear_layout_noDishes = findViewById(R.id.linear_layout_noMovies);
        linear_layout_hasDishes.setVisibility(View.GONE);

        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        Log.i("DISPLAY NAME", userDisplayName);
        uid = intent.getStringExtra("uid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_vote_movie);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        getMoviesFromServer();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                if(documentList!=null && documentList.size()<6){
                    showDialogBox("Add a movie", "", "", "add");
                }
                else{
                    Toast.makeText(vote_movies.this, "Too many movies !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoviesFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showDialogBox(String alertBoxTitle, final String documentId, String defaultString, final String task){
        AlertDialog.Builder builder = new AlertDialog.Builder(vote_movies.this);
        builder.setTitle(alertBoxTitle.trim());

        // Set up the input
        final EditText input = new EditText(vote_movies.this);
        input.setText(defaultString);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String movieName = input.getText().toString();
                movieName = WordUtils.capitalizeFully(movieName);
                hideKeyboard();
                if(movieName.trim().equals("")){
                    Toast.makeText(vote_movies.this, "We can't vote for a blank movie!", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (task){
                    case "add": addNewMovie(movieName);break;
                    case "edit": editMovie(documentId, movieName);break;
                }

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

    private void addNewMovie(final String movieName) {

        Movie movie = new Movie();
        movie.setCreator(userDisplayName);
        movie.setCreator_uid(uid);
        movie.setMovieName(movieName);
        movie.setVoteCount("1");
        movie.setVoterList(Arrays.asList(uid));
        db.collection(COLLECTION_NAME)
                .add(movie)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(vote_movies.this, movieName + " added !", Toast.LENGTH_SHORT).show();
                        getMoviesFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void getMoviesFromServer(){
        db.collection(COLLECTION_NAME)
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
                    final Movie movie = document.toObject(Movie.class);

                    CardView card = (CardView)linear_layout_hasDishes.getChildAt(counter);

                    card.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    vibrate();
                                                    if (movie.getVoterList().contains(uid)){
                                                        downVote(document.getId(), movie.getVoteCount());
                                                    }
                                                    else{
                                                        upVote(document.getId(), movie.getVoteCount());
                                                    }
                                                }
                                            }
                    );

                    card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            vibrate();
                            if(movie.getCreator_uid().equals(uid)){
                                showDialogBox("Edit Dish", document.getId(), movie.getMovieName(), "edit");
                            }
                            return false;
                        }
                    });

                    card.setVisibility(View.VISIBLE);

                    LinearLayout linearLayout = (LinearLayout)card.getChildAt(0);
                    TextView movieName = (TextView) linearLayout.getChildAt(0);
                    TextView voteCount = (TextView) linearLayout.getChildAt(1);

                    movieName.setText(movie.getMovieName());
                    voteCount.setText(movie.getVoteCount());

                    if(movie.getVoterList().contains(uid)){
                        movieName.setTextColor(getColor(R.color.colorAccent));
                        voteCount.setTextColor(getColor(R.color.colorAccent));
                    }
                    else{
                        movieName.setTextColor(getColor(R.color.textPrimary));
                        voteCount.setTextColor(getColor(R.color.textSecondary));
                    }

                    counter = (counter<=4)?counter+1:5;
                }
            }


        }
        else{
            linear_layout_hasDishes.setVisibility(View.GONE);
            linear_layout_noDishes.setVisibility(View.VISIBLE);
        }

    }

    private void editMovie(String documentId, String movieName) {
        DocumentReference dishReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        dishReference.update("movieName",movieName )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(vote_movies.this, "Dish Updated!", Toast.LENGTH_SHORT).show();
                        getMoviesFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(vote_movies.this, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downVote(String documentId, String currentVoteCount) {
        DocumentReference movieReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        movieReference.update("voterList", FieldValue.arrayRemove(uid));
        movieReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) - 1 ));
        getMoviesFromServer();
    }

    private void upVote(String documentId, String currentVoteCount) {
        DocumentReference movieReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        movieReference.update("voterList", FieldValue.arrayUnion(uid));
        movieReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) + 1 ));
        getMoviesFromServer();
    }



    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    private void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(40);
        }
    }


}
