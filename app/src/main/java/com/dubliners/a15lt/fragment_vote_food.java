package com.dubliners.a15lt;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_vote_food.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_vote_food#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_vote_food extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters

    private Context mContext;
    private static final String TAG = "Database Writer";

    private  Calendar calender;
    private String week_of_year;
    private String day_of_week;
    private String current_year;
    private String collectionID;
    private final String COLLECTION_DISHES = "Dishes";
    private final int MAX_CARDS = 6;


    private Vibrator vibrator;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userDisplayName, uid, selectedDay;
    private LinearLayout linear_layout_hasDishes, linear_layout_noDishes;
    private QuerySnapshot documentList = null;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    public fragment_vote_food() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_vote_food.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_vote_food newInstance(String param1, String param2, String param3) {
        fragment_vote_food fragment = new fragment_vote_food();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDisplayName = getArguments().getString(ARG_PARAM1);
            uid = getArguments().getString(ARG_PARAM2);
            selectedDay = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vote_food, container, false);


        calender = Calendar.getInstance();
        calender.setFirstDayOfWeek(7);
        week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));

        day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        current_year = String.valueOf(calender.get(Calendar.YEAR));
        collectionID = current_year+".week"+week_of_year;



        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        linear_layout_hasDishes = view.findViewById(R.id.linear_layout_hasDishes);
        linear_layout_noDishes = view.findViewById(R.id.linear_layout_noDishes);
        linear_layout_hasDishes.setVisibility(View.GONE);

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        getDishesFromServer();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                if(documentList!=null && documentList.size()<6){
                    showDialogBox("What's cooking?", "", "", "add");
                }
                else{
                    Toast.makeText(getContext(), "Too many dishes !", Toast.LENGTH_SHORT).show();
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
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showDialogBox(String alertBoxTitle, final String documentId, String defaultString, final String task){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(alertBoxTitle.trim());

        // Set up the input
        final EditText input = new EditText(mContext);
        input.setText(defaultString);
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
                    Toast.makeText(mContext, "We can't vote for a blank dish!", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (task){
                    case "add": addNewDish(dishName);break;
                    case "edit": editDish(documentId, dishName);break;
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

    private void addNewDish(final String dishName) {

        Dish dish = new Dish();
        dish.setCreator(userDisplayName);
        dish.setCreator_uid(uid);
        dish.setDishName(dishName);
        dish.setVoteCount(((nav_drawer.EasterEggCounter==0)?"2":"1"));
        dish.setVoterList(Arrays.asList(uid));
        db.collection(collectionID)
                .document(selectedDay)
                .collection(COLLECTION_DISHES)
                .add(dish)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(mContext, dishName + " added !", Toast.LENGTH_SHORT).show();
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
        Log.d("fragment_vote_food", "CollectionId : " + collectionID);
        Log.d("fragment_vote_food", "week of year : " + week_of_year);
        Log.d("fragment_vote_food", String.valueOf(calender.DAY_OF_WEEK));
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
            linear_layout_hasDishes.getChildAt(i).setVisibility(View.GONE);
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

                    card.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    vibrate();
                                                    if (dish.getVoterList().contains(uid)){
                                                        downVote(document.getId(), dish.getVoteCount());
                                                    }
                                                    else{
                                                        upVote(document.getId(), dish.getVoteCount());
                                                    }
                                                }
                                            }
                    );

                    card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            vibrate();
                            if(dish.getCreator_uid().equals(uid)){
                                showDialogBox("Edit Dish", document.getId(), dish.getDishName(), "edit");
                            }
                            return false;
                        }
                    });

                    card.setVisibility(View.VISIBLE);

                    LinearLayout linearLayout = (LinearLayout)card.getChildAt(0);
                    TextView dishName = (TextView) linearLayout.getChildAt(0);
                    TextView voteCount = (TextView) linearLayout.getChildAt(1);

                    dishName.setText(dish.getDishName());
                    voteCount.setText(dish.getVoteCount());

                    if(dish.getVoterList().contains(uid)){
                        dishName.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        voteCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    }
                    else{
                        dishName.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                        voteCount.setTextColor(ContextCompat.getColor(mContext, R.color.textSecondary));
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

    private void editDish(String documentId, String dishName) {
        DocumentReference dishReference =
                db.collection(collectionID).document(selectedDay)
                        .collection(COLLECTION_DISHES)
                        .document(documentId);

        dishReference.update("dishName",dishName )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(mContext, "Dish Updated!", Toast.LENGTH_SHORT).show();
                        getDishesFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(mContext, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downVote(String documentId, String currentVoteCount) {
        DocumentReference dishReference =
                db.collection(collectionID).document(selectedDay)
                        .collection(COLLECTION_DISHES)
                        .document(documentId);

        dishReference.update("voterList", FieldValue.arrayRemove(uid));
        dishReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) - ((nav_drawer.EasterEggCounter==0)?2:1) ));
        getDishesFromServer();
    }

    private void upVote(String documentId, String currentVoteCount) {
        DocumentReference dishReference =
                db.collection(collectionID).document(selectedDay)
                        .collection(COLLECTION_DISHES)
                        .document(documentId);

        dishReference.update("voterList", FieldValue.arrayUnion(uid));
        dishReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) + ((nav_drawer.EasterEggCounter==0)?2:1) ));
        getDishesFromServer();
    }



    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
