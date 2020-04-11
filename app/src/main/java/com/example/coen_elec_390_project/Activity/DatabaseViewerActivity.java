package com.example.coen_elec_390_project.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseViewerActivity extends AppCompatActivity {
    private ListView usersListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        usersListView = findViewById(R.id.listview);
        usersListView.setAdapter(null);

        loadListView();
    }

    protected void loadListView() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<User> users = databaseHelper.getAllUsers();
        ArrayList<String> dbListText = new ArrayList<>();
        String temp;
        temp = "User table\n";
        for(int i = 0; i < users.size(); i++) {
            temp += "user id: "+ users.get(i).getId() + "\n";
            temp += "user fbuid: "+ users.get(i).getFbuid() + "\n";
            temp += "user name: "+ users.get(i).getFullname() + "\n";
            temp += "user email: "+ users.get(i).getEmail() + "\n";
            temp += "user password: "+ users.get(i).getPassword()+"\n";
            temp += "user gender: "+ users.get(i).getGender()+"\n";
            temp += "user age: "+ users.get(i).getAge()+"\n";
            temp += "user height: "+ users.get(i).getHeight()+"\n";
            temp += "user weight: "+ users.get(i).getWeight()+"\n";
            temp += "user height unit: "+ users.get(i).getHeightUnit()+"\n";
            temp += "user weight unit: "+ users.get(i).getWeightUnit()+"\n";
            temp += "user stat counter: " + users.get(i).getStat_counter()+"\n";
            dbListText.add(temp);
        }

        List<Statistic> stats = databaseHelper.getAllStats();
        temp = "Statistic table\n";
        for(int i =0;i<stats.size();i++){
            temp += "stat id: "+ stats.get(i).getId() + "\n";
            temp += "stat uid: "+ stats.get(i).getUser_id()+"\n";
            temp += "stat date: "+stats.get(i).getDate() +"\n";
            temp += "stat speed: "+stats.get(i).getSpeed() +"\n";
            temp += "stat calory: "+stats.get(i).getCalories() +"\n";
            temp += "stat perf index: "+stats.get(i).getPerformance_index() + "\n";
            dbListText.add(temp);
        }


        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbListText);
        usersListView.setAdapter(arrayAdapter);
    }
}
