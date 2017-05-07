package com.naman.mobileapps.raptionary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public TextView mDisplay;
    public TextView mUrban;
    private GoogleApiClient client;
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public EditText mArtistEdit;
    public EditText mSongEdit;
    public EditText mUrbanEdit;
    public String artistName;
    public String songName;
    public String output;
    public boolean urbanUpdate;
    public boolean start;
    public boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        output="";
        isFirst = true;
        urbanUpdate = false;
        start = true;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mDisplay = (TextView) findViewById(R.id.lyrics);
        mUrban = (TextView) findViewById(R.id.urban);
        mArtistEdit = (EditText) findViewById(R.id.artistEdit);
        mSongEdit = (EditText) findViewById(R.id.songEdit);
        mUrbanEdit = (EditText) findViewById(R.id.urbanEdit);
        artistName = "";
        songName = "";
        mDisplay.setText("");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if((!(mArtistEdit.getText().toString().equals(""))) && !((mSongEdit.getText().toString().equals("")))){
                    mDisplay.setText((String) dataSnapshot.child("lyrics").getValue());
                }
                if(urbanUpdate) {
                    mUrban.setText(output);
                    urbanUpdate = false;
                    output = "";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Fail", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Loading ...", "Finding lyrics and definitions", true, false);
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String in = mUrbanEdit.getText().toString();
                        if(!in.equals("")) {
                            output += in + "\n\n-";
                            String html = null;
                            try {
                                html = Jsoup.connect("http://www.urbandictionary.com/define.php?term=" + in).get().html();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ArrayList<String> list = new ArrayList<>();
                            for (String word : html.split(" "))
                                list.add(word);
                            int start = 0;
                            int end = 0;
                            for (int i = list.size() - 1; i >= 0; i--)
                                if (list.get(i).contains("meaning"))
                                    start = i;
                            for (int i = list.size() - 1; i >= start; i--)
                                if (list.get(i).contains("example"))
                                    end = i;
                            for (int i = start + 1; i < end; i++)
                                if ((!list.get(i).contains("<div")) && (!list.get(i).equals("</div>")))
                                    if ((!list.get(i).contains("<br>")) && (!list.get(i).equals("</br>")))
                                        if ((!list.get(i).equals("")))
                                            output += list.get(i) + " ";
                            if(output.length() > 1500){
                                output = output.substring(0, 500);
                            }
                            while (output.contains("<")) {
                                int first = output.indexOf("<");
                                int last = output.indexOf(">");
                                output = output.substring(0, first) + output.substring(last + 1);
                            }
                            urbanUpdate = true;
                        }
                        artistName = mArtistEdit.getText().toString();
                        songName = mSongEdit.getText().toString();
                        myRef.child("artist").setValue(mArtistEdit.getText().toString());
                        myRef.child("song").setValue(mSongEdit.getText().toString());
                        new RequestTask().execute(artistName + "," + songName);
                        myRef.child("lyrics").setValue(mSongEdit.getText().toString());
                        // Doing UI related code in UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                // Showing response dialog
                                final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            }
                        });
                    }
                });
                thread.start();
            }
        });
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.naman.mobileapps.raptionary/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.naman.mobileapps.raptionary/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
