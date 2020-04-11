package com.example.coen_elec_390_project.Model;

import android.content.Intent;

import java.text.DecimalFormat;
import java.util.Date;

public class Statistic {
    private Integer id;
    private Integer user_id;
    private String date;
    private Double performance_index;
    private Double speed;
    private Double calories;
    public static Integer counter;

    public Integer getStep_counter() {
        return step_counter;
    }

    public void setStep_counter(Integer step_counter) {
        this.step_counter = step_counter;
    }

    private Integer step_counter;

    public Statistic() {

    }

    public Statistic(Integer user_id, String date, Double performance_index, Double speed,Double calories,Integer step_counter) {
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
        this.calories=calories;
        this.step_counter=step_counter;
    }

    public Statistic(Integer id, Integer user_id, String date, Double performance_index, Double speed,Double calories, Integer step_counter) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
        this.calories=calories;
        this.step_counter=step_counter;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getPerformance_index() {
        return performance_index;
    }

    public void setPerformance_index(Double performance_index) {
        this.performance_index = performance_index;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getCalories(){return calories;}

    public void setCalories(Double calories){this.calories=calories;}

    public static double getperformanceindex(double pre, double post) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (pre != 0 && post != 0) {
            return Double.valueOf(df.format((15.3 * (post / pre))));
        } else
            return 0;
    }

    public static double getCaloriesBurned(double weight, long duration,float speed) {

        if(speed==0.0)
            return 0.0;

        if(speed>4.0)
            return ((weight * 5 * 3.5) / (200)) * (duration);
        else if(speed>8.1)
            return ((weight * 8 * 3.5) / (200)) * (duration);
        else if(speed>9.65)
            return ((weight * 10 * 3.5) / (200)) * (duration);
        else if(speed>11.26)
            return ((weight * 11.5 * 3.5) / (200)) * (duration);
        else if(speed>12.87)
            return ((weight * 13.5 * 3.5) / (200)) * (duration);
        else if(speed>14.48)
            return ((weight * 15 * 3.5) / (200)) * (duration);
        else if(speed>16.09)
            return ((weight * 16 * 3.5) / (200)) * (duration);

        return 0.0;


    }
}
