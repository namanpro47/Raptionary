package com.naman.mobileapps.raptionary;

import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
/**
 * Created by Naman on 2/18/2017.
 */
public class RequestTask extends AsyncTask<Object, Object, Object> {
    public FirebaseDatabase database;
    public DatabaseReference myRef;
    public String artistName;
    public String songName;

    @Override
    protected Object doInBackground(Object[] objects){
        String pulled = objects[0].toString();
        Integer comma = pulled.indexOf(",");
        artistName = pulled.substring(0,comma);
        songName = pulled.substring(comma + 1);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("lyrics").setValue("Loading, Please Wait...");
        try{
            myRef.child("lyrics").setValue(Lyrics.getLyrics(artistName,songName));
        }catch(IOException ex){
            Log.e("IOException", "Failed to set lyrics value");
        }
        return null;
    }
}
