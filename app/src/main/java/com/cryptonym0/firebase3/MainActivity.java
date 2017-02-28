package com.cryptonym0.firebase3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /****************************
     * Declare all views and variables
     * ***************************/
    //Adapet
    private MainAdapter ada;
    private MainActivity mContext;

    //DB
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    //Analitics -TODO!!
    private FirebaseAnalytics mFirebaseAnalytics;
    private String username;

    private TextView tvTyping;
    private TimeMe timeTyping = new TimeMe(1000, 1000);
    private Button sendBtn;
    private EditText msg;
    private boolean isTyping = false;

    //Messages
    private ArrayList<Message> listMsg = new ArrayList<>();
    private ListView lvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set the context
        mContext = MainActivity.this;

        //get token
        //Start registration service
        Intent i = new Intent(this, FirebaseIDService.class);
        startService(i);


        //get my cool analytics so google can reign supreme
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //mFirebaseAnalytics.setUserProperty("user_type", "author");

        //Set up views
        sendBtn             = (Button) findViewById(R.id.button_send);
        msg                 = (EditText) findViewById(R.id.editText_message);
        tvTyping            = (TextView) findViewById(R.id.textView_is_typing);
        lvMsg               = (ListView) findViewById(R.id.main_listview);
        username            = getSharedPreferences("PREFS", 0).getString("username", "Anonymous");

        //Who is typing? hehe
        tvTyping.setVisibility(View.INVISIBLE);

        //Get my database
        database            = FirebaseDatabase.getInstance();
        myRef               = database.getReference();

        //Get the adapter set up
        ada                 = new MainAdapter(mContext, listMsg);
        lvMsg.setAdapter(ada);

        /*****************************************************
         * Send button on click listener
         * ***************************************************/
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                //removes the "is typing"
                timeTyping.cancel();
                myRef.child("room-typing").child("irc").child(username).setValue(false);
                processMessage(msg.getText().toString().trim());
                msg.setText("");
            }
        });

        /*****************************************************
         * Database change listener
         * ***************************************************/
        myRef.child("users").child(MyUtils.generateUniqueUserId(mContext)).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);
                if (username == null) {
                    username = "Egg";
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        //Snapshot everything
        myRef.child("db_messages").limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                listMsg.add(message);
                ada.notifyDataSetChanged();
                Log.d("message", message.toString());
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.toString());
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildRemoved", dataSnapshot.toString());
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildMoved", dataSnapshot.toString());
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", databaseError.toString());
            }
        });

        msg.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //TODO!!
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO!!
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeTyping.cancel();
                timeTyping.start();
                if (!isTyping) {
                    myRef.child("room-typing").child("irc").child(username).setValue(true);
                    isTyping = true;
                }
            }
        });

        myRef.child("room-typing").child("irc").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Boolean> hashMap = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (hashMap == null) {
                    return;
                }
                if (hashMap.containsKey(username)) {
                    hashMap.remove(username);
                }
                String output = "";
                for (String key : hashMap.keySet()) {
                    if (hashMap.get(key).equals(true)) {
                        output = output + key + " ";
                    }
                }
                if (!output.isEmpty()) {
                    tvTyping.setText(output + " is typing");
                    tvTyping.setVisibility(View.VISIBLE);
                } else {
                    tvTyping.setVisibility(View.INVISIBLE);
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //TODO!!
            }
        });
    }

    private void processMessage(String message) {
        if (message.length() < 1) {
            return;
        }

        //send to DB. Very critial
        String key                          = myRef.child("db_messages").push().getKey();
        Message post                        = new Message(MyUtils.generateUniqueUserId(mContext), username, message, System.currentTimeMillis() / 1000L);
        Map<String, Object> postValues      = post.toMap();
        Map<String, Object> childUpdates    = new HashMap<>();
        childUpdates.put("/db_messages/" + key, postValues);
        myRef.updateChildren(childUpdates);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_activity, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.username:

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                alert.setMessage("Do you want to change your username ?");
                alert.setTitle(null);

                username = getSharedPreferences("PREFS", 0).getString("username", "Anonymous");
                edittext.setText(username);
                alert.setView(edittext);
                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String new_username = edittext.getText().toString();
                        mContext.getSharedPreferences("PREFS", 0).edit().putString("username", new_username).commit();
                        username = new_username;
                        myRef.child("users").child(MyUtils.generateUniqueUserId(mContext)).setValue(username);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");



    }//end on create

    /*********************************
     * TIMER!
     * *******************************/
    public class TimeMe extends CountDownTimer {

        public TimeMe(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override public void onTick(long l) {

        }

        @Override public void onFinish() {
            myRef.child("room-typing").child("irc").child(username).setValue(false);
            isTyping = false;
        }
    }
}
