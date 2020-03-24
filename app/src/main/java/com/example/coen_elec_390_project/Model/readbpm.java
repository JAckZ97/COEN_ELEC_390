package com.example.coen_elec_390_project.Model;

import java.util.ArrayList;
import java.util.List;

public class readbpm{

    public static double preBPM;
    public static double postBPM;
    public static ArrayList<Integer> recordings = new ArrayList<>();
    public static int recording;
    public static Integer bpmrecording;
    public static int sumbpm;
    public static Boolean getprebpm=false;
    public static Boolean getpostbpm=false;

    public static void getPreBPM(){
        preBPM=0;

        int div=0;
        //int[] array1 = new int[]{85, 90, 78, 82, 84, 87, 88, 91, 92, 90, 87, 84, 85, 86, 89, 76, 89, 79, 87, 84};

        for (int i = 0; i <= recordings.size() - 1; i++) {
            bpmrecording = recordings.get(i);
            if (bpmrecording != 0 && bpmrecording < 190 && bpmrecording > 30) ;
            sumbpm += bpmrecording;
            div++;
        }

        //preBPM = sumbpm / recordings.size();
        preBPM = sumbpm / div;

        recordings.clear();
        sumbpm=0;
    }

    public static void getPostBPM(){
        postBPM=0;

        int div =0;
        //int[] array2 = new int[]{111, 119, 128, 132, 111, 112, 118, 1117, 115, 111, 114, 118, 110, 109, 132, 122, 121, 125, 113, 109};

        for (int i = 0; i <= recordings.size() - 1; i++) {
            bpmrecording = recordings.get(i);
            if (bpmrecording != 0 && bpmrecording < 190 && bpmrecording > 55) ;
            sumbpm += bpmrecording;
            div++;
        }

        //postBPM = sumbpm / recordings.size();
        postBPM = sumbpm / div;

        recordings.clear();
        sumbpm=0;
    }

}
