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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.timemanagement.R;
import com.example.android.timemanagement.data.Contract;
import com.example.android.timemanagement.data.DBHelper;
import com.example.android.timemanagement.data.DatabaseUtils;
import com.example.android.timemanagement.models.ActivitySwitcherToolbar;
import com.example.android.timemanagement.models.StartEndTime;
import com.example.android.timemanagement.utilities.PreferenceUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GraphActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "GraphActivity";

    private SQLiteDatabase database;
    private BarChart chart;

    private SegmentedRadioGroup DayRadioButton;

    private int currentDayButton;
    private int moveDayBackTask = 0;
    private int moveWeekBackTask = 0;
    private int moveMonthBackTask = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //new ActivitySwitcherToolbar(this.getClass(), this);

        // top of the layout
        DayRadioButton = (SegmentedRadioGroup) findViewById(R.id.day_button);
        DayRadioButton.setOnCheckedChangeListener(this);
        currentDayButton = DayRadioButton.getCheckedRadioButtonId();

        // middle of the layout
        chart = (BarChart) findViewById(R.id.bar_chart);

        Log.d(TAG, ": onCreate");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == DayRadioButton) {
            if (checkedId == R.id.today_button)
            {
                currentDayButton = R.id.today_button;
                daysBarChart(moveDayBackTask);
            } else if (checkedId == R.id.week_button)
            {
                currentDayButton = R.id.week_button;
                weekBarChart(moveWeekBackTask);
            } else if (checkedId == R.id.month_button)
            {
                currentDayButton = R.id.month_button;
                monthBarChart(moveMonthBackTask);
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        daysBarChart(moveDayBackTask);

        Log.d("GraphActivity: ", "onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("GraphActivity: ", "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("GraphActivity: ", "onPause");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("GraphActivity: ", "onStop");
        this.finish();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d("GraphActivity: ", "onRestart");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("GraphActivity: ", "onDestroy");
    }

    public void onPreviousButton(View view)
    {
        if(currentDayButton == R.id.today_button)
        {
            moveDayBackTask -= 1;
            daysBarChart(moveDayBackTask);
        }
        else if(currentDayButton == R.id.week_button)
        {
            moveWeekBackTask -= 1;
            weekBarChart(moveWeekBackTask);
        }
        else if(currentDayButton == R.id.month_button)
        {
            moveMonthBackTask -= 1;
            monthBarChart(moveMonthBackTask);
        }
        Log.d(TAG, "- moveDayBackTask: " + moveDayBackTask);
        Log.d(TAG, "- moveWeekBackTask: " + moveWeekBackTask);
        Log.d(TAG, "- moveMonthBackTask: " + moveMonthBackTask);
        Toast.makeText(this, "Clicked on previous Button", Toast.LENGTH_LONG).show();
    }

    public void onNextButton(View view)
    {
        if(currentDayButton == R.id.today_button)
        {
            if(moveDayBackTask >= 0)
            {
                moveDayBackTask = 0;
            }
            else
            {
                moveDayBackTask += 1;
                daysBarChart(moveDayBackTask);
            }

        }
        else if(currentDayButton == R.id.week_button)
        {
            if(moveWeekBackTask >= 0)
            {
                moveWeekBackTask = 0;
            }
            else
            {
                moveWeekBackTask += 1;
                weekBarChart(moveWeekBackTask);
            }
        }
        else if(currentDayButton == R.id.month_button)
        {
            if(moveMonthBackTask >= 0)
            {
                moveMonthBackTask = 0;
            }
            else
            {
                moveMonthBackTask += 1;
                monthBarChart(moveMonthBackTask);
            }
        }
        Log.d(TAG, "- moveDayBackTask: " + moveDayBackTask);
        Log.d(TAG, "- moveWeekBackTask: " + moveWeekBackTask);
        Log.d(TAG, "- moveMonthBackTask: " + moveMonthBackTask);
        Toast.makeText(this, "Clicked on NEXT Button", Toast.LENGTH_LONG).show();
    }

    public void daysBarChart(int dayOffset)
    {
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = DatabaseUtils.getDaysTask(database, dayOffset);

        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");

        List<StartEndTime> startEndTimeUTC = new ArrayList<>();

        ArrayList<String> dates = new ArrayList<>();
        if(cursor.moveToFirst())
        {
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
        }

        Collections.sort(startEndTimeUTC, new Comparator<StartEndTime>() {
            @Override
            public int compare(StartEndTime o1, StartEndTime o2) {
                if(o1.getStartingTime() == o2.getStartingTime()) {return 0;}
                else if(o1.getStartingTime() < o2.getStartingTime()) {return -1;}
                else {return 1;}
            }
        });

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

        makeBarChart(xTimeVals, yMinVals, "Today");

        Log.d("testing ", "stuff");
    }

    public void weekBarChart(int weekOffset)
    {
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = DatabaseUtils.getWeeksTask(database, weekOffset);

        HashMap<String, Integer> weekTask = new HashMap<>();

        if(cursor.moveToFirst())
        {
            do
            {
                String date = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_DATE));
                int totalMintues = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_TASK_TOTAL_MINUTES));

                if(weekTask.containsKey(date))
                {
                    int daysTotalMintues = weekTask.get(date);
                    daysTotalMintues += totalMintues;
                    weekTask.put(date, daysTotalMintues);
                }
                else
                {
                    weekTask.put(date, totalMintues);
                }
            }while(cursor.moveToNext());
        }

        List<String> dayKey = new ArrayList(weekTask.keySet());
        Collections.sort(dayKey, new Comparator<String>() {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            @Override
            public int compare(String o1, String o2) {
                try {
                    return dateFormat.parse(o1).compareTo(dateFormat.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        ArrayList<BarEntry> yMinVals = new ArrayList();
        ArrayList<String> xdayVals = new ArrayList();
        for(int i = 0; i < dayKey.size(); i++)
        {
            String date = dayKey.get(i);
            int dayTotalMinute = weekTask.get(date);

            xdayVals.add(date);
            yMinVals.add(new BarEntry(i, dayTotalMinute));
        }

        makeBarChart(xdayVals, yMinVals, "Week");
    }

    public void monthBarChart(int monthOffset)
    {
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        ArrayList<Cursor> thisMonthWeekCursor = DatabaseUtils.getMonthTask(database, monthOffset);
        //ArrayList<String> dates = new ArrayList<>();

        ArrayList<BarEntry> yMinVals = new ArrayList();
        ArrayList<String> xWeekVals = new ArrayList();
        for(int i = 0; i < thisMonthWeekCursor.size(); i++)
        {
            Cursor cursor = thisMonthWeekCursor.get(i);

            int totalMintues = 0;
            if(cursor.moveToFirst())
            {
                do
                {
                    //String date = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_DATE));
                    totalMintues += cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TASK.COLUMN_NAME_TASK_TOTAL_MINUTES));

                    //dates.add(date);
                }while(cursor.moveToNext());
            }

            xWeekVals.add("Week " + String.valueOf(i + 1));
            yMinVals.add(new BarEntry(i, totalMintues));
        }
        makeBarChart(xWeekVals, yMinVals, "Month");
        Log.d("test: ", "stuff");
    }

    private void makeBarChart(ArrayList<String> xTimeVals, ArrayList<BarEntry> yMintueVals, String name)
    {
        float barWidth = 0.7f;

        Description hello = new Description();
        hello.setText(name);
        hello.setTextSize(20);
        hello.setXOffset(170);
        hello.setYOffset(349);
        chart.setDescription(hello);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(true);
        chart.enableScroll();

        BarDataSet todaysTaskSet;
        todaysTaskSet = new BarDataSet(yMintueVals, "Total Time Spent");
        todaysTaskSet.setColor(Color.BLUE);
        todaysTaskSet.setValueTextSize(15f);

        BarData data = new BarData(todaysTaskSet);
        data.setValueFormatter(new LargeValueFormatter(" min"));

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
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(xTimeVals.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xTimeVals));
        // text size and offset to compensate the text size
        xAxis.setTextSize(15);
        chart.setExtraBottomOffset(20);

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
}
