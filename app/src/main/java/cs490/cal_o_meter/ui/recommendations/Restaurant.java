package cs490.cal_o_meter.ui.recommendations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Restaurant extends AppCompatActivity {



    void searchRangeAndFillTable(String range){
        Log.i("test","Got to seachRangeAndFillTable");

        final TableLayout restaurantTable= (TableLayout) findViewById(R.id.restaurantTable);

        final TableRow row1 = (TableRow) findViewById(R.id.firstRow);
        final TextView meal1 = (TextView) findViewById(R.id.rest_meal);
        final TextView calories1= (TextView) findViewById(R.id.rest_calories);
        final TextView rest1= (TextView) findViewById(R.id.restName);

        final TableRow row2 = (TableRow) findViewById(R.id.secondRow);
        final TextView meal2 = (TextView) findViewById(R.id.rest_meal2);
        final TextView calories2= (TextView) findViewById(R.id.rest_calories2);
        final TextView rest2= (TextView) findViewById(R.id.restName2);

        final TableRow row3 = (TableRow) findViewById(R.id.thirdRow);
        final TextView meal3 = (TextView) findViewById(R.id.rest_meal3);
        final TextView calories3= (TextView) findViewById(R.id.rest_calories3);
        final TextView rest3= (TextView) findViewById(R.id.restName3);

        row1.setVisibility(View.INVISIBLE);
        row2.setVisibility(View.INVISIBLE);
        row3.setVisibility(View.INVISIBLE);
        restaurantTable.setVisibility(View.INVISIBLE);

        int low=Integer.parseInt(range.split("-")[0]);
        int high=Integer.parseInt(range.split("-")[1]);
        Log.i("low",Integer.toString(low));
        Log.i("high",Integer.toString(high));

        FirebaseFirestore.getInstance().collection("RestaurantMeals")
                .whereLessThanOrEqualTo("calories",high)
                .whereGreaterThanOrEqualTo("calories",low)
                .orderBy("calories", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int row=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("Meal", document.getId() + " => " + document.getData());

                                String MealName= document.getString("meal");
                                int Calories= document.getLong("calories").intValue();
                                String Restaurant=document.getString("name");

                                if (row==1){
                                    restaurantTable.setVisibility(View.VISIBLE);
                                    meal1.setText(MealName);
                                    calories1.setText(Integer.toString(Calories));
                                    rest1.setText(Restaurant);
                                    row1.setVisibility(View.VISIBLE);
                                    row++;
                                } else if (row==2){
                                    meal2.setText(MealName);
                                    calories2.setText(Integer.toString(Calories));
                                    rest2.setText(Restaurant);
                                    row2.setVisibility(View.VISIBLE);
                                    row++;
                                } else { //row 3

                                    meal3.setText(MealName);
                                    calories3.setText(Integer.toString(Calories));
                                    rest3.setText(Restaurant);
                                    row3.setVisibility(View.VISIBLE);
                                    return;
                                }

                            }
                        } else {
                            Log.i("Error", "Error getting documents.", task.getException());
                        }
                    }
                });

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_recommendations);

        Button submitButton = findViewById(R.id.rest_meal_selected);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.rest_meal)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.rest_calories)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button submitButton2 = findViewById(R.id.rest_meal_selected2);
        submitButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.rest_meal2)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.rest_calories2)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button submitButton3 = findViewById(R.id.rest_meal_selected3);
        submitButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.rest_meal3)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.rest_calories3)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String range = ((EditText) findViewById(R.id.search_range)).getText().toString();
                searchRangeAndFillTable(range);
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

}
