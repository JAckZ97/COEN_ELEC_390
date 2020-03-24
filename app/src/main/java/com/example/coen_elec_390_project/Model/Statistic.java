package com.example.coen_elec_390_project.Model;

import android.content.Intent;

import java.util.Date;

public class Statistic {
    private Integer id;
    private Integer user_id;
    private String date;
    private Double performance_index;
    private Double speed;
    private Double calories;
    private Integer counter_id;
    public static Integer counter;

    public Statistic() {

    }

    public Statistic(Integer user_id, String date, Double performance_index, Double speed,Double calories) {
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
        this.calories=calories;
    }

    public Statistic(Integer id, Integer user_id, String date, Double performance_index, Double speed,Double calories,Integer counter) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
        this.calories=calories;
        this.counter_id=counter;
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

    public Integer getCounter_id(){return this.counter_id;}
}
