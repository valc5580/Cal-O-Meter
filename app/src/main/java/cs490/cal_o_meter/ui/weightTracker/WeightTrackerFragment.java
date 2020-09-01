package cs490.cal_o_meter.ui.weightTracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs490.cal_o_meter.R;
import cs490.cal_o_meter.ui.home.InputOptions;

public class WeightTrackerFragment extends Fragment {

    private WeightTrackerViewModel weightTrackerViewModel;
    SQLiteDatabase db;
    int v = 0;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        weightTrackerViewModel =
                ViewModelProviders.of(this).get(WeightTrackerViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_weight_tracker, container, false);

        // setup db

        setUpDB();
        //deleteWeightTable();
        try {
            setUpWeightsTable();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final LineChart mChart = root.findViewById(R.id.weightChart);

        // add weight button
        final DatePicker picker=(DatePicker) root.findViewById(R.id.datePicker);
        final EditText userWeight = root.findViewById((R.id.userWeight));
        Button addWeightButton = (Button) root.findViewById(R.id.addWeightButton);
        addWeightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Log.d(getClass().getName(), "date: "+ dateFormat.format(getDateFromDatePicker(picker)));
                InsertWeight(Float.valueOf(userWeight.getText().toString()), getDateFromDatePicker(picker));
                RefreshPage();
            }
        });

        DrawWeightChart(mChart);

        return root;
    }

    public void RefreshPage(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    void DrawWeightChart(LineChart mChart)
    {
        //Log.d(getClass().getName(), "DrawWeightChart: called");
        // Get the list of weights
        ArrayList<Pair<Float, Date>> weightRecords = null;
        try {
            weightRecords = GetWeights();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final ArrayList<Entry> weightLine = new ArrayList<>();
        float firstX = 0;
        int i = 0;
        for (Pair<Float, Date> record : weightRecords){
            //Log.d(getClass().getName(), "value = " + i);
            float datelong = record.second.getTime() / 100000;
            if (firstX != 0){
                datelong -= firstX;
            } else {
                firstX = datelong;
                datelong = 0;
            }
            weightLine.add(
                    new Entry(
                            datelong,
                            record.first
                    )
            );
            //Log.d(getClass().getName(), "value = " + i +", date " + datelong +", weight = "+weightRecords.get(i).first);
            //i++;
        }

        // draw the chart
        //LineChart mChart = root.findViewById(R.id.weightChart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setLabelCount(5, true);
        xAxis.setDrawLabels(true);

        final float finalFirstX = firstX;
        xAxis.setValueFormatter(new IAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date((long) ((value + finalFirstX) * 100000));
                return displayDateFormat.format(date);
            }
        });

        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(weightLine);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(weightLine, "Recorded Weights in LB");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);


            if (Utils.getSDKInt() >= 18) {
                //Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                //set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }

            //mChart.getXAxis().setDrawLabels(false);
            mChart.getDescription().setEnabled(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
    }

    public String IncrementDate(Date oldDate, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(oldDate);
        //Incrementing the date
        c.add(Calendar.DAY_OF_MONTH, days);
        return dateFormat.format(c.getTime());
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }


    void setUpDB(){
        db =  getActivity().openOrCreateDatabase("appDB",android.content.Context.MODE_PRIVATE,null);
    }

    void setUpWeightsTable() throws ParseException {
        db.execSQL("CREATE TABLE IF NOT EXISTS Weights(Weight DECIMAL(10, 2), Date DATE);");

        InsertWeight(140, dateFormat.parse("01/02/2020"));
        InsertWeight(120, dateFormat.parse("01/01/2020"));
        InsertWeight(130, dateFormat.parse("01/03/2020"));
    }

    void deleteWeightTable(){
        //db = getActivity().openOrCreateDatabase("appDB",android.content.Context.MODE_PRIVATE,null);
        db.execSQL("DROP TABLE IF EXISTS Weights;");
    }

    void InsertWeight(double weight, Date date){
        db.execSQL("UPDATE Weights SET Weight = " + weight
                + " WHERE Date =\"" + dateFormat.format(date)+"\"");
        db.execSQL("INSERT INTO Weights(Weight, Date) " +
                "SELECT " + weight + ", \"" + dateFormat.format(date) + "\" " +
                "WHERE NOT EXISTS (SELECT 1 FROM Weights " +
                "WHERE Date =\"" + dateFormat.format(date)+"\")"
        );
    }

    void printWeights(){
        Cursor resultSet= db.rawQuery("Select * from Weights",null);
        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false) {
            Log.i("Weight", resultSet.getString(resultSet.getColumnIndex("Weight")));
            Log.i("Date", resultSet.getString(resultSet.getColumnIndex("Date")));
            resultSet.moveToNext();
        }
    }

    ArrayList<Pair<Float, Date>> GetWeights() throws ParseException {
        Cursor resultSet= db.rawQuery("Select * from Weights",null);
        resultSet.moveToFirst();
        //Log.v("desc", DatabaseUtils.dumpCursorToString(resultSet));

        ArrayList<Pair<Float, Date>> weightRecords = new ArrayList<>();

        while(resultSet.isAfterLast() == false) {
            Pair<Float, Date> curPair = new Pair<Float, Date>(
                    resultSet.getFloat(resultSet.getColumnIndex("Weight")),
                    dateFormat.parse(resultSet.getString(resultSet.getColumnIndex("Date")))
            );
            weightRecords.add(curPair);

            resultSet.moveToNext();
        }

        resultSet.close();

        Collections.sort(weightRecords, new Comparator<Pair<Float, Date>>() {
            public int compare(Pair<Float, Date> o1, Pair<Float, Date> o2) {
                return o1.second.compareTo(o2.second);
            }
        });

        return weightRecords;
    }
}
