package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;

public class Calendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        CalendarView cv = (CalendarView) findViewById(R.id.calendarView);
        //long a = cv.getDate();


        String selectedDate = getIntent().getStringExtra("date");
        try {
            cv.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(selectedDate).getTime(), true, true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Log.i("calendar",Integer.toString(i2)+"/"+Integer.toString(i1+1)+"/"+Integer.toString(i));
                Intent intent = new Intent();
                intent.putExtra("date", Integer.toString(i2)+"/"+Integer.toString(i1+1)+"/"+Integer.toString(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
