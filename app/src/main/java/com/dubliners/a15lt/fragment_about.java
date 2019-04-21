package com.dubliners.a15lt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_about extends Fragment {


    public fragment_about() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        CardView cardView = view.findViewById(R.id.about_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nav_drawer.EasterEggCounter>0)nav_drawer.EasterEggCounter--;
                if(nav_drawer.EasterEggCounter<=3 && nav_drawer.EasterEggCounter >0){
                    try{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "You are " + nav_drawer.EasterEggCounter+" steps away" +
                                        "from unlocking the Easter Egg !", Toast.LENGTH_SHORT).show();
                    }catch (NullPointerException e){e.printStackTrace();}
                }else if(nav_drawer.EasterEggCounter == 0){
                    try{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Congratulations!\nYour food votes are now worth 2x !\nHappy Easter!",
                                Toast.LENGTH_LONG).show();
                    }catch (NullPointerException e){e.printStackTrace();}
                }
            }
        });
        return view;

    }

}
