package com.dubliners.a15lt;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_food.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_food#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_food extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String userDisplayName, uid;
    private Context mContext;
    private  Calendar calender;
    private String week_of_year;
    private String day_of_week;
    private OnFragmentInteractionListener mListener;
    Vibrator vibrator;
    public fragment_food() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_food.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_food newInstance(String param1, String param2) {
        fragment_food fragment = new fragment_food();
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

            if(nav_drawer.firstFoodLaunchCount==0){
                nav_drawer.firstFoodLaunchCount++;
                Log.e("Fragment Food", "Forwarding @ count = " + nav_drawer.firstFoodLaunchCount);
                calender = Calendar.getInstance();
                calender.setFirstDayOfWeek(7);
                day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                try{
                    Fragment voteFragment = fragment_vote_food.newInstance(userDisplayName,uid,day_of_week);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    //FragmentManager fragmentManager = getChildFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.nav_default_content, voteFragment).commit();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }


            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        calender = Calendar.getInstance();
        calender.setFirstDayOfWeek(7);
        week_of_year = String.valueOf(calender.get(Calendar.WEEK_OF_YEAR));

        day_of_week = calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Log.d("fragment_food", "day of week : " + day_of_week);
        return inflater.inflate(R.layout.fragment_food, container, false);
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

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        View view = getView();
        if(view!=null){
            vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            int currentDayButtonId = getResources().getIdentifier(day_of_week, "id", mContext.getPackageName());
            if(!userDisplayName.contains("divina")){
                view.findViewById(currentDayButtonId).setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccentAlternate));
            }

        }
    }

    public void launchVote(View view) {
        vibrate();
        String selectedDay = view.getResources().getResourceEntryName(view.getId());
        Fragment voteFragment = fragment_vote_food.newInstance(userDisplayName,uid,selectedDay);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_default_content, voteFragment).commit();
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(40);
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
}
