package com.example.coen_elec_390_project.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coen_elec_390_project.Activity.StatisticsActivity;
import com.example.coen_elec_390_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DateSelectionFragment extends DialogFragment {
    Button startdate, enddate, back;
    DatePickerDialog.OnDateSetListener startDatePickerDialog, endDatePickerDialog;
    String startDate = "", endDate = "";
    StatisticsActivity statisticsActivity;
    private static final String TAG = "DateSelectionFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_selection_fragment,container, false);

        startdate = view.findViewById(R.id.startdate);
        enddate = view.findViewById(R.id.enddate);
        back = view.findViewById(R.id.back);

        statisticsActivity = (StatisticsActivity) getActivity();

        final String maxDate = getArguments().getString("maxDate");
        final String minDate = getArguments().getString("minDate");

        startdate.setText(minDate);
        enddate.setText(maxDate);

        String[] min_date_component = minDate.split("/");
        final int min_year = Integer.parseInt(min_date_component[0]);
        final int min_month = Integer.parseInt(min_date_component[1]);
        final int min_day = Integer.parseInt(min_date_component[2]);
        final Calendar min_cal = Calendar.getInstance();
        min_cal.set(min_year, min_month - 1, min_day);

        String[] max_date_component = maxDate.split("/");
        final int max_year = Integer.parseInt(max_date_component[0]);
        final int max_month = Integer.parseInt(max_date_component[1]);
        final int max_day = Integer.parseInt(max_date_component[2]);
        final Calendar max_cal = Calendar.getInstance();
        max_cal.set(max_year, max_month - 1, max_day);

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, startDatePickerDialog, min_year, min_month - 1, min_day);
                dialog.getDatePicker().setMinDate(min_cal.getTimeInMillis());
                dialog.getDatePicker().setMaxDate(max_cal.getTimeInMillis());
                dialog.show();
            }
        });

        startDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startdate.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                startDate = startdate.getText().toString();
            }
        };

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, endDatePickerDialog, max_year, max_month, max_day);
                dialog.getDatePicker().setMinDate(min_cal.getTimeInMillis());
                dialog.getDatePicker().setMaxDate(max_cal.getTimeInMillis());
                dialog.show();
            }
        });

        endDatePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                enddate.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                endDate = enddate.getText().toString();

                enddate.setError(null);
                enddate.setFocusable(false);
                enddate.setFocusableInTouchMode(false);
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int validDates = 2;

                try {
                    validDates = compareDate(startDate, endDate);
                }

                catch (ParseException e) {
                    e.printStackTrace();
                }

                if(validDates == -1) {
                    enddate.setError("Error: The end date \n should be after \n the start date");
                    enddate.setFocusable(true);
                    enddate.setFocusableInTouchMode(true);
                }

                else {
                    statisticsActivity.receiveStartEndDate(startDate, endDate);
                    statisticsActivity.loadListView();
                    statisticsActivity.setUpGraph();
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    /**Function that compares two dates*/
    public int compareDate(String d1, String d2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        Date date1 = format.parse(d1);
        Date date2 = format.parse(d2);

        if(date1.after(date2)) {
            return -1;
        }

        else if(date1.before(date2)) {
            return 1;
        }

        else {
            return 0;
        }
    }
}
