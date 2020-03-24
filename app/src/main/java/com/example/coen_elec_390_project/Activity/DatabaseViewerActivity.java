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

        loadListView();
    }

    protected void loadListView() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<User> users = databaseHelper.getAllUsers();
        ArrayList<String> dbListText = new ArrayList<>();
        String temp;
        for(int i = 0; i < users.size(); i++) {
            temp = "User table\n";
            temp += users.get(i).getId() + "\n";
            temp += users.get(i).getFullname() + "\n";
            temp += users.get(i).getEmail() + "\n";
            temp += users.get(i).getPassword()+"\n";
            temp += users.get(i).getGender()+"\n";
            temp += users.get(i).getAge()+"\n";
            temp += users.get(i).getHeight()+"\n";
            temp += users.get(i).getWeight()+"\n";
            temp += users.get(i).getHeightUnit()+"\n";
            temp += users.get(i).getWeightUnit()+"\n";
            dbListText.add(temp);
        }

        List<Statistic> stats = databaseHelper.getAllStats();

        for(int i =0;i<stats.size();i++){
            temp = "Statistic table\n";
            temp += stats.get(i).getId() + "\n";
            temp += stats.get(i).getDate() +"\n";
            temp += stats.get(i).getSpeed() +"\n";
            temp += stats.get(i).getCalories() +"\n";
            temp += stats.get(i).getPerformance_index() + "\n";
            dbListText.add(temp);
        }


        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbListText);
        usersListView.setAdapter(arrayAdapter);
    }
}
