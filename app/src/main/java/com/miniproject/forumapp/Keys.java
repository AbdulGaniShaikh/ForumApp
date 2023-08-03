package com.miniproject.forumapp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Keys {

    public static String TOPIC_COLLECTION = "topics";
    public static String TOPICNAME = "topicname";
    public static String TOPICDESP = "description";
    public static String TOPICCATEGORY = "category";
    public static String TOPICCREATEDBY = "createdby";
    public static String TOPICCREATEDBYID = "createdbyid";
    public static String REQUESTEDUSERS = "requestedusers";
    public static String JOINEDUSERS = "joinedusers";
    public static String TOPIC_CREATEDON = "createdon";

    public static String USER_COLLECTION = "users";
    public static String USERNAME = "name";
    public static String EMAIL = "email";
    public static String AVATAR = "avatar";
    public static String REQUESTEDFORUMS = "requestedtopics";
    public static String JOINEDFORUMS = "joinedtopics";
    public static String CREATEDFORUMS = "createdtopics";
    public static String FRIENDS = "friends";
    public static String FRIENDREQUESTS = "friendrequests";
    public static String MYREQUESTS = "myrequests";

    public static String QUESTION_COLLECTION = "questions";
    public static String QUESTION = "question";
    public static String ASKEDBYNAME = "name";
    public static String ASKEDBYDP = "dp";
    public static String ASKEDBYMAIL = "mail";
    public static String ASKEDBY = "askedby";
    public static String ASKEDON = "askedon";
    public static String QUESTIONTOPIC = "topic";
    public static String QUESTIONSANSWER = "answers";


    public static String ANSWER_COLLECTION = "answers";
    public static String ANSWER = "answer";
    public static String ANSWEREDBYNAME = "name";
    public static String ANSWEREDBYDP = "dp";
    public static String ANSWEREDBYMAIL = "mail";
    public static String ANSWEREDBY = "answeredby";
    public static String ANSWEREDON = "answeredon";

    public static int NO_STATUS = 0;
    public static int STATUS_REQUESTED = 1;
    public static int STATUS_FRIEND = 2;

    public static int USER_INTENT= 0;
    public static int TOPIC_INTENT= 1;

    public static String PUBLIC = "public";
    public static String PRIVATE = "private";


    public static int getAvatar(int i){
        switch (i){
            case 1:
                return R.drawable.ic_avat1;
            case 2:
                return R.drawable.ic_avat2;
            case 3:
                return R.drawable.ic_avat3;
            case 4:
                return R.drawable.ic_avat4;
            case 5:
                return R.drawable.ic_avat5;
            case 6:
                return R.drawable.ic_avat6;
            case 7:
                return R.drawable.ic_avat7;
            case 8:
                return R.drawable.ic_avat8;
            case 9:
                return R.drawable.ic_avat9;
            case 10:
                return R.drawable.ic_avat10;
            case 11:
                return R.drawable.ic_avat11;
            case 12:
                return R.drawable.ic_avat12;
            case 13:
                return R.drawable.ic_avat13;
            case 14:
                return R.drawable.ic_avat14;
            case 15:
                return R.drawable.ic_avat15;
            default:
                return R.drawable.ic_avat0;
        }
    }

    public static int getImage(String s){
        switch (s){
            case "Arts":
                return R.drawable.illustration_art;
            case "Business":
                return R.drawable.illustration_business1;
            case "Cartoon":
                return R.drawable.illustration_cartoon;
            case "Computer":
                return R.drawable.illustration_computer;
            case "Daily Life":
                return R.drawable.illustration_dailylife;
            case "Farming":
                return R.drawable.illustration_farming1;
            case "Gaming":
                return R.drawable.illustration_gaming1;
            case "Geography":
                return R.drawable.illustration_geography;
            case "Health":
                return R.drawable.illustration_health;
            case "History":
                return R.drawable.illustration_history;
            case "Meme":
                return R.drawable.illustration_meme;
            case "Movie":
                return R.drawable.illustration_movie;
            case "Music":
                return R.drawable.illustration_music;
            case "Politics":
                return R.drawable.illustration_politics;
            case "Psychology":
                return R.drawable.illustration_psychology;
            case "Science":
                return R.drawable.illustration_science;
            case "Scientific Research":
                return R.drawable.illustration_scientificreasearch;
            case "Sports":
                return R.drawable.illustration_sports;
        }
        return R.drawable.illustration_art;
    }

    private static String[] suffix = new String[]{"","k", "m", "b", "t"};
    private static int MAX_LENGTH = 4;

    public static String format(int number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public static String formatDate(Date d){
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa, dd MMM yyyy");
        String format = formatter.format(d);
        return format;
    }

}
