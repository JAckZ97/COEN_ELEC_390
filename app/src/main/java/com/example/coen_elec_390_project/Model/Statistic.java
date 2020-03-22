package com.example.coen_elec_390_project.Model;

import android.content.Intent;

import java.util.Date;

public class Statistic {
    private Integer id;
    private Integer user_id;
    private String date;
    private Integer performance_index;
    private Double speed;

    public Statistic() {

    }

    public Statistic(Integer user_id, String date, Integer performance_index, Double speed) {
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
    }

    public Statistic(Integer id, Integer user_id, String date, Integer performance_index, Double speed) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.speed = speed;
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

    public Integer getPerformance_index() {
        return performance_index;
    }

    public void setPerformance_index(Integer performance_index) {
        this.performance_index = performance_index;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
