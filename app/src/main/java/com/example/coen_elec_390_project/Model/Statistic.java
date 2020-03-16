package com.example.coen_elec_390_project.Model;

import android.content.Intent;

import java.util.Date;

public class Statistic {
    private Integer id;
    private Integer user_id;
    private String date;
    private Integer performance_index;
    private Integer bpm;

    public Statistic() {

    }

    public Statistic(Integer user_id, String date, Integer performance_index, Integer bpm) {
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.bpm = bpm;
    }

    public Statistic(Integer id, Integer user_id, String date, Integer performance_index, Integer bpm) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.performance_index = performance_index;
        this.bpm = bpm;
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

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }
}
