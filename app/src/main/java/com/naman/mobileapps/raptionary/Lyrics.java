package com.naman.mobileapps.raptionary;

/**
 * Created by Naman on 2/18/2017.
 */

import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.ArrayList;

public class Lyrics
{
    public static String getLyrics(String artist, String song) throws IOException{

        artist = artist.toLowerCase();
        artist = artist.replaceAll("\\s+", "");

        song = song.toLowerCase();
        song = song.replaceAll("\\s+", "");

        String finalLyrics = "";

        String html = Jsoup.connect("http://www.azlyrics.com/lyrics/" + artist + "/" + song + ".html").get().html();
        ArrayList <String> list = new ArrayList<>();
        for(String word : html.split(" "))
            list.add(word);
        int start = 0;
        for(int i = 0; i < list.size(); i++)
            if(list.get(i).equals("about"))
                if(list.get(i+1).equals("that."))
                    start = i+3;
        int end = 0;
        for(int i = start; i < list.size(); i++)
            if(list.get(i).equals("<!--"))
                if(list.get(i+1).equals("MxM"))
                    end = i;
        ArrayList <String> lyrics = new ArrayList<>();
        for(int i = start; i < end; i++)
        {
            if(list.get(i).contains("amp;"))
            {
                lyrics.add(list.get(i).replace("amp;", ""));
                i++;
            }
            if((!list.get(i).equals("<br>")) && (!list.get(i).equals("</div>")))
            {
                if((!list.get(i).contains("<i>")) && (!list.get(i).contains("</i>")))
                    if(! list.get(i).equals(""))
                        lyrics.add(list.get(i));
                if ((list.get(i).contains("<i>")) && (list.get(i).contains("</i>")))
                {

                    String original = list.get(i);
                    String correction = "";
                    correction = original.replace("<i>", "");
                    correction = correction.replace("</i>", "");
                    lyrics.add(correction);
                }
                if((list.get(i).contains("<i>")) && (!list.get(i).contains("</i>")))
                {
                    String correction = (list.get(i).replace("<i>", ""));
                    lyrics.add(correction);
                }
                if((!list.get(i).contains("<i>")) && (list.get(i).contains("</i>")))
                {
                    String correction = (list.get(i).replace("</i>", ""));
                    lyrics.add(correction);
                }
            }
        }
        for(int i = 0; i < lyrics.size() - 3; i++)
        {
            if(i == 0)
            {
                finalLyrics += " " + lyrics.get(0) + " ";
                i++;
            }

            finalLyrics+= lyrics.get(i) + " ";
        }

        return finalLyrics;
    };

}
