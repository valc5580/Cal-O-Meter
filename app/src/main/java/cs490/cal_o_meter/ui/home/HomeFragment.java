package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cs490.cal_o_meter.Login;
import cs490.cal_o_meter.R;
import cs490.cal_o_meter.Register;

import static android.app.Activity.RESULT_CANCELED;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    Date pageDate;
    int Goal=2500;
    int Consumed=0;
    int CALENDAR_ACTIVITY_REQUEST_CODE = 1;

    int BREAKFAST_REQUEST_CODE=2;
    int LUNCH_REQUEST_CODE=3;
    int DINNER_REQUEST_CODE=4;

    FirebaseFirestore fStore;

    View root;


    void setPageDate(Date d, View v){
        ((TextView)v.findViewById(R.id.pageDate)).setText(new SimpleDateFormat("dd MMMM yyyy").format(d));
    }

    void addActivity (int requestCode, View v){
        Intent intent = new Intent(v.getContext(), InputOptions.class);
        startActivityForResult(intent, requestCode);
    }




    void updateGoalInDB(int goal){
        //FirebaseAuth.getInstance().signOut();//logout
        Map<String,Integer> dateObj = new HashMap<>();
        dateObj.put("goal", goal);

        fStore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("userConsumptionGoals")
                .document(new SimpleDateFormat("dd-MM-yyyy").format(pageDate))
                .set(dateObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Goal Updated", "Goal Updated");
                        updateConsumedAndGoalUI(root);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Goal Update Failed", "Goal Update Failed");
                    }
                });

    }

    void updateConsumedAndGoalUI(final View v){

        //update Goal (if loading page for this date for first time then just put 0 in db for this date
        fStore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("userConsumptionGoals")
                .document(new SimpleDateFormat("dd-MM-yyyy").format(pageDate))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()){
                                Log.i("Goal Exists", "Goal Exists");
                                Goal = task.getResult().getLong("goal").intValue();
                                Log.i("Fetched Goal", Integer.toString(Goal));
                            } else {
                                Log.i("Error", "No goal, setting to 2400");
                                updateGoalInDB(2400);
                                Goal=2400;
                            }

                            ((TextView)v.findViewById(R.id.goalOutput)).setText(Integer.toString(Goal));

                            fStore.collection("users")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .collection("userConsumptionRecords")
                                    .document(new SimpleDateFormat("dd-MM-yyyy").format(pageDate))
                                    .collection("meals")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Consumed=0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Consumed+= document.getLong("calories").intValue();
                                                }
                                                Log.i("Consumed", Integer.toString(Consumed));
                                                ((TextView)v.findViewById(R.id.consumedOutput)).setText(Integer.toString(Consumed));

                                                CircularProgressBar circularProgressBar = (CircularProgressBar)root.findViewById(R.id.yourCircularProgressbar);
                                                float percent;
                                                if (Goal==Consumed)
                                                    percent=100;
                                                else if (Goal<Consumed){
                                                    percent = 100;

                                                } else {
                                                    percent = (Consumed *100) / Goal ;
                                                }
                                                circularProgressBar.setProgressWithAnimation(percent, (long) 1800);
                                            } else {
                                                Log.i("Error", "Error getting documents.", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.i("Error", "get  meals failed with ", task.getException());
                        }
                    }
                });



    }


    void addMealToDB(String Meal, String mealName, int Calories, String date){
        Map<String, Object> mealObj = new HashMap<>();
        mealObj.put("mealType", Meal);
        mealObj.put("mealName", mealName);
        mealObj.put("calories", Calories);

        fStore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("userConsumptionRecords")
                .document(date)
                .collection("meals")
                .add(mealObj)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference doc) {
                        Log.i("Added Meal To DB", "Data is "+doc.toString());
                        clearTables(root);
                        populateTables(root);
                        updateConsumedAndGoalUI(root);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Error", "Failed to add meal to DB");
                    }
                });


       // Log.i("Inserted","Inserted for "+Meal+" "+mealName+" which has "+Integer.toString(Calories)+" calories on date "+date);
    }


    void removeFromDB(String ID){
        //db.execSQL("DELETE FROM Meals WHERE ID = "+Integer.toString(id));
        fStore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("userConsumptionRecords")
                .document(new SimpleDateFormat("dd-MM-yyyy").format(pageDate))
                .collection("meals")
                .document(ID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Success", "Meal Successfully Removed");
                        updateConsumedAndGoalUI(root);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Error", "Failed To Remove Meal");
                    }
                });
    }

    void addRowToTable(final String ID, final String Meal, final String MealName, final int Calories, View v){
        TableLayout table;

        if (Meal.equals("Breakfast")){
            table = (TableLayout) v.findViewById(R.id.breakfast_table);
        } else if (Meal.equals("Lunch")) {
            table = (TableLayout) v.findViewById(R.id.lunch_table);
        } else {
            table = (TableLayout) v.findViewById(R.id.dinner_table);
        }


        final TableRow row = new TableRow(v.getContext());

        //row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        row.setWeightSum(4);
        row.setGravity(Gravity.CENTER);

        TextView blank= new TextView(v.getContext());

        blank.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,1));
        row.addView(blank);

        TextView meal= new TextView(v.getContext());
        meal.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,1));
        meal.setGravity(Gravity.CENTER);
        meal.setText(MealName);
        meal.setTextColor(Color.BLACK);
        meal.setTextSize(18);
        //meal.setTypeface(Typeface.DEFAULT_BOLD);

        row.addView(meal);

        TextView calories= new TextView(v.getContext());
        calories.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,1));
        calories.setGravity(Gravity.CENTER);
        calories.setText(Integer.toString(Calories));
        calories.setTextColor(Color.BLACK);
        calories.setTextSize(18);
        //calories.setTypeface(Typeface.DEFAULT_BOLD);

        row.addView(calories);

        /*
        <RelativeLayout
        android:layout_weight="1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
                >
                            <Button

        android:text="drop"
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/remove_button"/>
                        </RelativeLayout> */
        RelativeLayout buttonHolder = new RelativeLayout(v.getContext());
        buttonHolder.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,1));

        final Button remove_button = new Button(v.getContext());
        remove_button.setText("Drop");
        remove_button.setBackground(this.getResources().getDrawable(R.drawable.remove_button));

        remove_button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
        final TableLayout tablePointer=table;
        final TableRow rowPointer=row;
        final String date = new SimpleDateFormat("dd/MM/yyyy").format(pageDate);
        remove_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Product added: "+productList.get(index).name, Toast.LENGTH_SHORT).show();
                //removeFromTable(Meal,MealName,Calories);
                tablePointer.removeView(rowPointer);
                removeFromDB(ID);
            }
        });

        buttonHolder.addView(remove_button);
        row.addView(buttonHolder);
        table.addView(row);
    }

    void clearTables(View v){
        //clearing breakfast
        TableLayout table = (TableLayout) v.findViewById(R.id.breakfast_table);
        while(table.getChildCount()>1) {
            TableRow child = (TableRow) table.getChildAt(1);
            table.removeView(child);
        }

        //clearing lunch
        table = (TableLayout) v.findViewById(R.id.lunch_table);
        while(table.getChildCount()>1) {
            TableRow child = (TableRow) table.getChildAt(1);
            table.removeView(child);
        }

        //clearing lunch
        table = (TableLayout) v.findViewById(R.id.dinner_table);
        while(table.getChildCount()>1) {
            TableRow child = (TableRow) table.getChildAt(1);
            table.removeView(child);
        }
    }

    void populateTables(final View v){
        //populating breakfast table

        fStore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("userConsumptionRecords")
                .document(new SimpleDateFormat("dd-MM-yyyy").format(pageDate))
                .collection("meals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("Meal", document.getId() + " => " + document.getData());
                                String ID= document.getId();
                                String MealName= document.getString("mealName");
                                int Calories= document.getLong("calories").intValue();
                                String mealType=document.getString("mealType");
                                addRowToTable(ID,mealType,MealName,Calories,v);
                            }
                        } else {
                            Log.i("Error", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
         root = inflater.inflate(R.layout.fragment_home, container, false);



        fStore = FirebaseFirestore.getInstance();

        pageDate=new Date();
        setPageDate(pageDate,root);

        populateTables(root);

        updateConsumedAndGoalUI(root);


        Button calendar = (Button) root.findViewById(R.id.calendar_button);

        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String currentDate= new SimpleDateFormat("dd/MM/yyyy").format(pageDate);
                Intent intent = new Intent(view.getContext(), Calendar.class);
                intent.putExtra("date",currentDate);
                startActivityForResult(intent, CALENDAR_ACTIVITY_REQUEST_CODE);



            }
        });


        Button logout= (Button) root.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //populateTables();
                Log.i("Logout", "Clicked logout");


                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(view.getContext(), Login.class));
                getActivity().finish();


            }
        });



        Button updateGoal= (Button) root.findViewById(R.id.updateButton);
        updateGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText goalInput = (EditText) root.findViewById(R.id.goalInput);
                updateGoalInDB(Integer.parseInt(goalInput.getText().toString()));
                goalInput.setText("");
                goalInput.clearFocus();


            }
        });

        Button breakfastButton= (Button) root.findViewById(R.id.breakfastButton);
        breakfastButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addActivity(BREAKFAST_REQUEST_CODE, view);
            }
        });
        Button lunchButton= (Button) root.findViewById(R.id.lunchButton);
        lunchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addActivity(LUNCH_REQUEST_CODE, view);
            }
        });
        Button dinnerButton= (Button) root.findViewById(R.id.dinnerButton);
        dinnerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addActivity(DINNER_REQUEST_CODE, view);
            }
        });

        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Request code", Integer.toString(requestCode));

        if (requestCode == CALENDAR_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled Calendar");
                return;
            }
            String date=data.getStringExtra("date");
            try {
                pageDate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setPageDate(pageDate,root);

            clearTables(root);
            populateTables(root);
            updateConsumedAndGoalUI(root);
        }

        if (requestCode == BREAKFAST_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled Breakfast");
                return;
            }

            Log.i("Main", "In main, got breakfast");
            Log.i("mealName", data.getStringExtra("mealName"));
            Log.i("calories", data.getStringExtra("calories"));

            addMealToDB("Breakfast", data.getStringExtra("mealName"), Integer.parseInt(data.getStringExtra("calories")), new SimpleDateFormat("dd-MM-yyyy").format(pageDate));

        }

        if (requestCode == LUNCH_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled Lunch");
                return;
            }
            Log.i("Main", "In main, got Lunch");
            Log.i("mealName", data.getStringExtra("mealName"));
            Log.i("calories", data.getStringExtra("calories"));
            addMealToDB("Lunch", data.getStringExtra("mealName"), Integer.parseInt(data.getStringExtra("calories")),new SimpleDateFormat("dd-MM-yyyy").format(pageDate));

        }

        if (requestCode == DINNER_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled Dinner");
                return;
            }
            Log.i("Main", "In main, got Dinner");
            Log.i("mealName", data.getStringExtra("mealName"));
            Log.i("calories", data.getStringExtra("calories"));
            addMealToDB("Dinner", data.getStringExtra("mealName"), Integer.parseInt(data.getStringExtra("calories")),new SimpleDateFormat("dd-MM-yyyy").format(pageDate));

        }

    }
}
