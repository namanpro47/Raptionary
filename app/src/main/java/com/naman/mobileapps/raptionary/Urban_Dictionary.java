package com.naman.mobileapps.raptionary;

/**
 * Created by Naman on 2/19/2017.
 */

import android.util.Log;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

public class Urban_Dictionary
{
    public static String urbanDefine(String in) throws IOException{
        String output = "";
        Log.v("tester", "at urban");
        String html = Jsoup.connect("http://www.urbandictionary.com/define.php?term=" + in).get().html();
        Log.v("tester", "at urban 2222");
        ArrayList <String> list = new ArrayList<>();
        for(String word : html.split(" "))
            list.add(word);
        int start = 0;
        int end = 0;
        for(int i = list.size() - 1; i >= 0; i --)
            if(list.get(i).contains("meaning"))
                start = i;
        for(int i = list.size() - 1; i >= start; i--)
            if(list.get(i).contains("example"))
                end = i;
        for(int i = start + 1; i < end; i++)
            if((! list.get(i).contains("<div")) && (! list.get(i).equals("</div>")))
                if((! list.get(i).contains("<br>")) && (! list.get(i).equals("</br>")))
                    if((! list.get(i).equals("")))
                        output+=list.get(i) + " ";
        return output;
    }
}
