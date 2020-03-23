package com.example.coen_elec_390_project;

public class Performance {

        private int bpmafter;
        private int bpmrest;
        private int bpmmax;
        public double vo2maxRock;
        public double vo2maxUth;

        //VO2 calculations 2 ways:

        //ROCKPORT Formula
        //Uth-Sorenen-Overgaard-Pedersen estimation


    public double getVo2maxRock(int weight, int age, int height, int genderIndex, int timeofwalk, int bpmafter) {
        vo2maxRock = 132.853 - ((0.0769*weight) - (0.3877*age)) + (6.315 * genderIndex) - (3.2649 * timeofwalk) - (0.1565 * bpmafter);
        return vo2maxRock;
    }

    public double getVo2maxUth (int bpmrest, int  bpmmax){
        vo2maxUth = 15.3 * (bpmmax / bpmrest);
        return vo2maxUth;
    }


}
