package com.example.matthias_pc.challengeme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuLayout;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.matthias_pc.challengeme.model.Challenge;
import com.example.matthias_pc.challengeme.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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

        createChallengeListWithSwipeMenu();
    }

    private void createChallengeListWithSwipeMenu(){
        final List <Challenge> challengeList = new ArrayList<>();
        final List <String> keysList = new ArrayList<>();
        mChallenge.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI;
               final SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);

                challengeList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Challenge challenge = postSnapshot.getValue(Challenge.class);
                    challengeList.add(challenge);
                    keysList.add(postSnapshot.getKey());
                }
                final ArrayAdapter adapter = new ArrayAdapter(ChallengeActivity.this, android.R.layout.simple_list_item_1, challengeList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {
                        SwipeMenuItem acceptChallenge = new SwipeMenuItem(
                                getApplicationContext());
                        acceptChallenge.setBackground(new ColorDrawable(Color.rgb(255, 238,
                                0)));
                        acceptChallenge.setWidth(170);
                        acceptChallenge.setIcon(R.drawable.ic_add_circle_outline_black_24dp);
                        menu.addMenuItem(acceptChallenge);
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
                            case 1:
                                listView.setItemChecked(position, false);
                                mChallenge.child(keysList.get(position)).removeValue();
                                break;
                        }
                        return false;
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //String item = ((SwipeMenuLayout)view).getMenuView().toString();
                        //Toast.makeText(getBaseContext(), item,Toast.LENGTH_LONG).show();
                        openDialog(view);
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

    public void openDialog(final View view){


        final RelativeLayout container =(RelativeLayout)findViewById(R.id.Challenge_List_layout);
        final RelativeLayout rl = new RelativeLayout(getApplicationContext());
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to accept the Challenge");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(ChallengeActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();

                rl.setLayoutParams(lp);

                final TextView tv = new TextView(getApplicationContext());

                RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);

                tv.setLayoutParams(lp_tv);
                tv.setTextSize(30);
                tv.setTextColor(Color.parseColor("#FFFFEE00"));
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);

                rl.addView(tv);

                setContentView(rl);
                new CountDownTimer(3000, 1000) {
                    final TextView text = tv;

                    public void onTick(long millisUntilFinished) {
                        tv.setText("Start in: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        tv.setText(null);
                        setContentView(container);
                    }
                }.start();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



}
