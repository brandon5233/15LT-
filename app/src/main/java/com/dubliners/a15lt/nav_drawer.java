package com.dubliners.a15lt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class nav_drawer extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    fragment_vote_movies.OnFragmentInteractionListener,
                    fragment_vote_food.OnFragmentInteractionListener,
                    fragment_food.OnFragmentInteractionListener,
                    fragment_vote_shopping.OnFragmentInteractionListener
{
    String userDisplayName, uid, profilePicUrl, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        userDisplayName = intent.getStringExtra("userDisplayName");
        Log.i("DISPLAY NAME", userDisplayName);
        uid = intent.getStringExtra("uid");
        profilePicUrl = intent.getStringExtra("profilePicUrl");
        userEmail = intent.getStringExtra("userEmail");
        //profilePicUrl = profilePicUrl.replace("s96-c", "s400-c");

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        Log.d("nav_drawer", profilePicUrl);
        Picasso.with(this).load(profilePicUrl).resize(200,200).into(imageView);

        TextView tv_userDisplayName = navigationView.getHeaderView(0).findViewById(R.id.tv_userDisplayName);
        TextView tv_userEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_userEmail);

        tv_userDisplayName.setText(userDisplayName);
        tv_userEmail.setText(userEmail);

       Fragment fragment = fragment_food.newInstance(userDisplayName, uid);
       getSupportFragmentManager().beginTransaction().replace(R.id.nav_default_content, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            try {
                fragment = fragment_vote_movies.newInstance(userDisplayName, uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_food) {
            fragment = fragment_food.newInstance(userDisplayName, uid);

        } else if (id == R.id.nav_shopping) {
            fragment = fragment_vote_shopping.newInstance(userDisplayName,uid);
        } else if (id == R.id.nav_misc) {

        } else if (id == R.id.nav_about) {
                fragment = new fragment_about();
        } else if (id == R.id.nav_send) {

        }



        FragmentManager fragmentManager = getSupportFragmentManager();
        /*getFragmentManager().beginTransaction()
                .replace(R.id.linear_temp, fragment)
                .commit();*/
        fragmentManager.beginTransaction().replace(R.id.nav_default_content, fragment).commit();



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void launchVote(View view){
        Log.d("BUTTON CLICKED", String.valueOf(view.getId()));
        //vibrate();
        String selectedDay = getResources().getResourceEntryName(view.getId());
        Fragment voteFragment = fragment_vote_food.newInstance(userDisplayName,uid,selectedDay);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_default_content, voteFragment).commit();
    }


}
