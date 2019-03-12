package com.example.matthias_pc.challengeme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.matthias_pc.challengeme.model.Challenge;
import com.example.matthias_pc.challengeme.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChallengeActivity extends AppCompatActivity {

    private static final String TAG = "ChallengeActivity";

    //Firebase
    DatabaseReference counterRef, mChallenge;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;
    FirebaseRecyclerOptions<User> options =
            new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(counterRef, User.class)
                    .build();

    //View
    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mgGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;
    private FusedLocationProviderClient mFusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Challenges");
        setSupportActionBar(toolbar);

        //Firebase
        mChallenge = FirebaseDatabase.getInstance().getReference("Challenges");

        final Button button = findViewById(R.id.buttonCreateChall);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(ChallengeActivity.this, ListOnline.class));
            }
        });

        checkForChallenge();
    }

    private void checkForChallenge(){
        final List <String> challengeList = new ArrayList<>();
        mChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI;
               SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);

                challengeList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Challenge challenge = postSnapshot.getValue(Challenge.class);
                    challengeList.add(challenge.getUser().getEmail());
                    // here you can access to name property like university.name

                }
                ArrayAdapter adapter = new ArrayAdapter(ChallengeActivity.this, android.R.layout.simple_list_item_1, challengeList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getApplicationContext());
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(255, 238,
                                0)));
                        deleteItem.setWidth(170);
                        deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
                        menu.addMenuItem(deleteItem);
                    }
                };
                listView.setMenuCreator(creator);

                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                Log.d(TAG, "onMenuItemClick: clicked item" + index);

                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
               // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

}
