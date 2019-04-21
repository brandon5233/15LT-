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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_vote_shopping.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_vote_shopping#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_vote_shopping extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int MAX_CARDS = 30;
    // TODO: Rename and change types of parameters
    private final String TAG = "FRAGMENT_VOTE_SHOPPING";
    private final String COLLECTION_NAME = "Shopping";

    private OnFragmentInteractionListener mListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userDisplayName, uid, selectedDay;
    LinearLayout linear_layout_hasItems, linear_layout_noItems;
    QuerySnapshot documentList = null;
    Vibrator vibrator;

    public fragment_vote_shopping() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_vote_shopping.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_vote_shopping newInstance(String param1, String param2) {
        fragment_vote_shopping fragment = new fragment_vote_shopping();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDisplayName = getArguments().getString(ARG_PARAM1);
            uid = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote_shopping, container, false);


    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        FloatingActionButton fab;
        final SwipeRefreshLayout swipeRefreshLayout;
        if(view!=null){
            linear_layout_hasItems = view.findViewById(R.id.linear_layout_hasItems);
            linear_layout_noItems = view.findViewById(R.id.linear_layout_noItems);
            linear_layout_hasItems.setVisibility(View.GONE);

            fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //vibrate();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                    if(documentList!=null && documentList.size()<30){
                        showDialogBox("Add an item", "", "", "add");
                    }
                    else{
                        Toast.makeText(getContext(), "Too many items!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout1);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getItemsFromServer();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        }

        getItemsFromServer();



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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(alertBoxTitle.trim());

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setText(defaultString);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = input.getText().toString();
                itemName = WordUtils.capitalizeFully(itemName);
                hideKeyboard();
                if(itemName.trim().equals("")){
                    Toast.makeText(getContext(), "We can't vote for a blank item!", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (task){
                    case "add": addNewItem(itemName);break;
                    case "edit": editItem(documentId, itemName);break;
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

    private void addNewItem(final String itemName) {

        Movie item = new Movie();
        item.setCreator(userDisplayName);
        item.setCreator_uid(uid);
        item.setMovieName(itemName);
        item.setVoteCount("1");
        item.setVoterList(Arrays.asList(uid));
        db.collection(COLLECTION_NAME)
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), itemName + " added !", Toast.LENGTH_SHORT).show();
                        getItemsFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void getItemsFromServer(){
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
            linear_layout_hasItems.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        if(documentList!=null){

            if(documentList.size()==0){
                linear_layout_hasItems.setVisibility(View.GONE);
                linear_layout_noItems.setVisibility(View.VISIBLE);
            }
            else{
                linear_layout_hasItems.setVisibility(View.VISIBLE);
                linear_layout_noItems.setVisibility(View.GONE);
                Log.d(TAG, "list size" + String.valueOf(documentList.size()) );
                //Display each document as a card
                int counter = 0;
                for (final QueryDocumentSnapshot document : documentList) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    final Movie item = document.toObject(Movie.class);

                    CardView card = (CardView) linear_layout_hasItems.getChildAt(counter);

                    card.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    vibrate();
                                                    if (item.getVoterList().contains(uid)){
                                                        downVote(document.getId(), item.getVoteCount());
                                                    }
                                                    else{
                                                        upVote(document.getId(), item.getVoteCount());
                                                    }
                                                }
                                            }
                    );

                    card.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            vibrate();
                            if(item.getCreator_uid().equals(uid)){
                                showDialogBox("Edit Dish", document.getId(), item.getMovieName(), "edit");
                            }
                            return false;
                        }
                    });

                    card.setVisibility(View.VISIBLE);

                    LinearLayout linearLayout = (LinearLayout)card.getChildAt(0);
                    TextView itemName = (TextView) linearLayout.getChildAt(0);
                    TextView voteCount = (TextView) linearLayout.getChildAt(1);

                    itemName.setText(item.getMovieName());
                    voteCount.setText(item.getVoteCount());

                    if(item.getVoterList().contains(uid)){
                        itemName.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        voteCount.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                    }
                    else{
                        itemName.setTextColor(ContextCompat.getColor(getContext(),R.color.textPrimary));
                        voteCount.setTextColor(ContextCompat.getColor(getContext(),R.color.textSecondary));
                    }

                    counter = (counter<=28)?counter+1:29;
                }
            }


        }
        else{
            linear_layout_hasItems.setVisibility(View.GONE);
            linear_layout_noItems.setVisibility(View.VISIBLE);
        }

    }

    private void editItem(String documentId, String itemName) {
        DocumentReference itemReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        itemReference.update("itemName",itemName )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(getContext(), "Item Updated!", Toast.LENGTH_SHORT).show();
                        getItemsFromServer();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(getContext(), "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downVote(String documentId, String currentVoteCount) {
        DocumentReference itemReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        itemReference.update("voterList", FieldValue.arrayRemove(uid));
        itemReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) - 1 ));
        getItemsFromServer();
    }

    private void upVote(String documentId, String currentVoteCount) {
        DocumentReference movieReference =
                db.collection(COLLECTION_NAME)
                        .document(documentId);

        movieReference.update("voterList", FieldValue.arrayUnion(uid));
        movieReference.update("voteCount", String.valueOf(Integer.parseInt(currentVoteCount) + 1 ));
        getItemsFromServer();
    }



    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
