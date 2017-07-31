package com.example.android.timemanagement.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.timemanagement.R;
import com.example.android.timemanagement.data.Contract;
import com.example.android.timemanagement.data.DBHelper;
import com.example.android.timemanagement.data.DatabaseUtils;
import com.example.android.timemanagement.models.ActivitySwitcherToolbar;
import com.example.android.timemanagement.models.StartEndTime;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GraphActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private ActivitySwitcherToolbar activitySwitcherToolbar;

    private SQLiteDatabase database;
    private BarChart chart;

    private Button todayButton;
    private Button weekButton;
    private Button monthButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // init
        //dummyTask();

        // top of the layout
        todayButton =  (Button) findViewById(R.id.today_button);
        todayButton.setBackgroundResource(R.color.colorAccent);

        weekButton =  (Button) findViewById(R.id.week_button);
        weekButton.setBackgroundResource(R.color.colorPrimary);

        monthButton =  (Button) findViewById(R.id.month_button);
        monthButton.setBackgroundResource(R.color.colorPrimary);

        // middle of the layout
        chart = (BarChart) findViewById(R.id.bar_chart);
        todaysBarChart();

        // bottom of the layout
        toolbar = (Toolbar) findViewById(R.id.graph_toolbar);
        activitySwitcherToolbar = new ActivitySwitcherToolbar(this.getClass(), this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.d("GraphActivity: ", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("GraphActivity: ", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GraphActivity: ", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("GraphActivity: ", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("GraphActivity: ", "onStop");
        this.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("GraphActivity: ", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GraphActivity: ", "onDestroy");
    }

    public void onButtonToday(View view)
    {
        chart.clear();
        todayButton.setBackgroundResource(R.color.colorAccent);
        weekButton.setBackgroundResource(R.color.colorPrimary);
        monthButton.setBackgroundResource(R.color.colorPrimary);

        todaysBarChart();
        Toast.makeText(this, "Clicked on Today Button", Toast.LENGTH_LONG).show();
    }

    public void onButtonWeek(View view)
    {
        chart.clear();
        todayButton.setBackgroundResource(R.color.colorPrimary);
        weekButton.setBackgroundResource(R.color.colorAccent);
        monthButton.setBackgroundResource(R.color.colorPrimary);

        //weekButton.setBackgroundResource();
        Toast.makeText(this, "Clicked on Week Button", Toast.LENGTH_LONG).show();
    }

    public void onButtonMonth(View view)
    {
        chart.clear();
        todayButton.setBackgroundResource(R.color.colorPrimary);
        weekButton.setBackgroundResource(R.color.colorPrimary);
        monthButton.setBackgroundResource(R.color.colorAccent);

        Toast.makeText(this, "Clicked on Month Button", Toast.LENGTH_LONG).show();
    }

    public void makeBarChart()
    {
        float barWidth;
        float barSpace;
        float groupSpace;

        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;

        chart = (BarChart) findViewById(R.id.bar_chart);
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(true);
        chart.enableScroll();

        int groupCount = 6;

        ArrayList xVals = new ArrayList();

        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");

        ArrayList yVals1 = new ArrayList();
        ArrayList yVals2 = new ArrayList();

        yVals1.add(new BarEntry(1, (float) 1));
        yVals2.add(new BarEntry(1, (float) 2));
        yVals1.add(new BarEntry(2, (float) 3));
        yVals2.add(new BarEntry(2, (float) 4));
        yVals1.add(new BarEntry(3, (float) 5));
        yVals2.add(new BarEntry(3, (float) 6));
        yVals1.add(new BarEntry(4, (float) 7));
        yVals2.add(new BarEntry(4, (float) 8));
        yVals1.add(new BarEntry(5, (float) 9));
        yVals2.add(new BarEntry(5, (float) 10));
        yVals1.add(new BarEntry(6, (float) 11));
        yVals2.add(new BarEntry(6, (float) 12));

        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "A");
        set1.setColor(Color.RED);
        set1.setValueTextSize(15);
        set2 = new BarDataSet(yVals2, "B");
        set2.setColor(Color.BLUE);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(0, groupSpace, barSpace);
        chart.getData().setHighlightEnabled(false);
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(4);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.setTextSize(12);
//Y-axis
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(15);
    }

    public void todaysBarChart()
    {
        setupBarChart();

        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = DatabaseUtils.getTodaysTask(database);
        //Cursor cursor = DatabaseUtils.getThisWeeksTask(database);

        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");

        List<StartEndTime> startEndTimeUTC = new ArrayList<>();

        ArrayList<String> dates = new ArrayList<>();
        cursor.moveToFirst();
        do
        {
            int StartingTimeHr = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_START_HOUR));
            int StartingTimeMin = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_START_MINUTE));
            String StartingMidDay = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_START_MID_DAY));

            int EndingTimeHr = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_END_HOUR));
            int EndingTimeMin = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_END_MINUTE));
            String EndingMidDay = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_END_MID_DAY));

            int totalMintues = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_TASK_TOTAL_MINUTES));

            String date = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_DATE));
            dates.add(date);

            Date startTime = null;
            Date endTime = null;
            try
            {
                startTime = parseFormat.parse(String.valueOf(StartingTimeHr) + ":" + String.valueOf(StartingTimeMin)
                        + StartingMidDay);
                endTime = parseFormat.parse(String.valueOf(EndingTimeHr) + ":" + String.valueOf(EndingTimeMin)
                        + EndingMidDay);
                startEndTimeUTC.add(new StartEndTime(startTime.getTime(), endTime.getTime(), totalMintues));
            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }

        }while(cursor.moveToNext());

        Collections.sort(startEndTimeUTC, new Comparator<StartEndTime>() {
            @Override
            public int compare(StartEndTime o1, StartEndTime o2) {
                if(o1.getStartingTime() == o2.getStartingTime()) {return 0;}
                else if(o1.getStartingTime() < o2.getStartingTime()) {return -1;}
                else {return 1;}
            }
        });


        makeBarChart(startEndTimeUTC);

        Log.d("testing ", "stuff");
    }

    public void setupBarChart()
    {
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(true);
        chart.enableScroll();
    }

    private void makeBarChart(List<StartEndTime> startEndTimeUTC)
    {
        float barWidth = 0.7f;

//        chart.setDescription(null);
//        chart.setPinchZoom(false);
//        chart.setScaleEnabled(true);
//        chart.setDrawBarShadow(false);
//        chart.setDrawGridBackground(false);
//        chart.setDrawBorders(true);
//        chart.enableScroll();

        int groupCount = 10;

        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");

        ArrayList<BarEntry> yMinVals = new ArrayList();
        ArrayList<String> xTimeVals = new ArrayList();
        for(int i = 0; i < startEndTimeUTC.size(); i++)
        {
            StartEndTime time = startEndTimeUTC.get(i);

            Date startTime = new Date(time.getStartingTime());
            Date endTime = new Date(time.getEndingTime());

            String taskDuration = parseFormat.format(startTime) + " - " + parseFormat.format(endTime);
            xTimeVals.add(taskDuration);

            yMinVals.add(new BarEntry(i, time.getTotalMinutes()));
        }

        BarDataSet todaysTaskSet;
        todaysTaskSet = new BarDataSet(yMinVals, "Total Time Spent");
        todaysTaskSet.setColor(Color.BLUE);
        todaysTaskSet.setValueTextSize(15f);

        BarData data = new BarData(todaysTaskSet);
        data.setValueFormatter(new LargeValueFormatter());

        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getData().setHighlightEnabled(false);
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(5f);
        l.setYEntrySpace(0f);
        l.setTextSize(15f);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(xTimeVals.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xTimeVals));
        xAxis.setTextSize(12);

        //Y-axis
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter(" min"));
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(15);

        Log.d("testing ", "stuff");
    }

    public void dummyTask()
    {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase dbAdd = dbHelper.getWritableDatabase();

        dbAdd.delete(Contract.TABLE_TASK.TABLE_NAME, null, null);
        dbAdd.delete(Contract.TABLE_SUBJECT.TABLE_NAME, null, null);
        dbAdd.delete(Contract.TABLE_PROJECT.TABLE_NAME, null, null);

        DatabaseUtils.addTask(dbAdd, "07/23/2017", "walk for 30 min", "diet",
                8, 30, "PM", 9, 00, "PM",
                ((9 - 8) * 60) + (00 - 30));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "eat a salad", "diet",
                12, 00, "AM", 1, 00, "AM",
                ((1) * 60) + (00 - 00));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "eat a salad", "diet",
                8, 30, "AM", 9, 00, "AM",
                ((9 - 8) * 60) + (00 - 30));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "get money from aim for project", "android project",
                10, 00, "AM", 10, 10, "AM",
                ((10 - 10) * 60) + (10 - 00));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "Hello", "meeting",
                2, 30, "PM", 6, 30, "PM",
                ((6 - 2) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "bye", "meeting",
                6, 30, "PM", 7, 30, "PM",
                ((7 - 6) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "commit graph layout", "school",
                8, 30, "PM", 11, 30, "PM",
                ((11 - 8) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/30/2017", "cooking snack", "diet",
                11, 31, "PM", 11, 59, "PM",
                ((11 - 11) * 60) + (59 - 31));

        DatabaseUtils.addTask(dbAdd, "07/29/2017", "english essay", "school",
                2, 30, "PM", 6, 30, "PM",
                ((6 - 2) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/29/2017", "math hw", "school",
                6, 30, "PM", 7, 30, "PM",
                ((7 - 6) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/29/2017", "reading", "school",
                7, 30, "PM", 8, 30, "PM",
                ((8 - 7) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/28/2017", "added add task button", "android project",
                7, 30, "PM", 8, 30, "PM",
                ((8 - 7) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/28/2017", "finish graph layout", "school",
                8, 30, "PM", 11, 30, "PM",
                ((11 - 8) * 60) + (30 - 30));

        DatabaseUtils.addTask(dbAdd, "07/29/2017", "finish updating task", "school",
                12, 30, "AM", 3, 30, "AM",
                ((3) * 60) + (30 - 30));

        dbHelper.close();
        dbAdd.close();
    }
}
