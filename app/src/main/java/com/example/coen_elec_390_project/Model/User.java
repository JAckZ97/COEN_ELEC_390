package com.example.coen_elec_390_project.Model;

public class User {

    public static String Global_email;
    private Integer id;
    private String fullname;
    private String email;
    private String password;
    private String age;
    private String weight;
    private String height;
    private String gender;
    private int heightUnit;
    private int weightUnit;

    public String getFbuid() {
        return fbuid;
    }

    public void setFbuid(String fbuid) {
        this.fbuid = fbuid;
    }

    private String fbuid;

    public int getStat_counter() {
        return stat_counter;
    }

    public void setStat_counter(int stat_counter) {
        this.stat_counter = stat_counter;
    }

    private int stat_counter;

    public User() {

    }


    public User(int id, String fullname, String email, String password, String age, String weight, String height, String gender, int heightUnit, int weightUnit,int stat_counter,String fbuid) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
        this.stat_counter=stat_counter;
        this.fbuid=fbuid;
    }

    public User(String fullname, String email, String password, String age, String weight, String height, String gender, int heightUnit, int weightUnit,int stat_counter,String fbuid) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
        this.stat_counter=stat_counter;
        this.fbuid=fbuid;
    }

    public int getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(int heightUnit) {
        this.heightUnit = heightUnit;
    }

    public int getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
