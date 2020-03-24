package com.example.coen_elec_390_project.Model;

import com.example.coen_elec_390_project.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Temp {
    public static ArrayList<Double> PreBPMs = new ArrayList<>();
    public static ArrayList<Double> PostBPMs= new ArrayList<>();
    public static ArrayList<Long> Durations= new ArrayList<>();
    public static ArrayList<Float> Speeds= new ArrayList<>();
    public static ArrayList<String> Dates= new ArrayList<>();
    public static int session_counter = 0;

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean insertTemp(double prebpm, double postbpm,String str_date,float speed,long duration){
        if(session_counter<10){
            PreBPMs.add(prebpm);
            PostBPMs.add(postbpm);
            Dates.add(str_date);
            Speeds.add(speed);
            Durations.add(duration);
            return true;
        }
        return false;
    }

    public static void clear(){
        session_counter=0;
        PreBPMs.clear();
        PostBPMs.clear();
        Durations.clear();
        Speeds.clear();
        Dates.clear();
    }

}
