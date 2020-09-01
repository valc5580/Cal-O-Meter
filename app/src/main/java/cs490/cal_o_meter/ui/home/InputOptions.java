package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;
import cs490.cal_o_meter.ui.recommendations.Recipes;
import cs490.cal_o_meter.ui.recommendations.Restaurant;

public class InputOptions extends AppCompatActivity {
    int SINGLE_REQUEST_CODE=1;
    int RECIPE_REQUEST_CODE=2;
    int RESTAURANT_REQUEST_CODE=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_options);

        Button cancelButton = findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button singleInput = findViewById(R.id.single_prod);

        singleInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(InputOptions.this, SingleItemActivity.class);
                startActivityForResult(intent, SINGLE_REQUEST_CODE);
            }
        });

        Button recipeBtn = findViewById(R.id.recipes);

        recipeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(InputOptions.this, Recipes.class);
                startActivityForResult(intent, RECIPE_REQUEST_CODE);
            }
        });

        Button restaurantBtn = findViewById(R.id.restaurant);

        restaurantBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(InputOptions.this, Restaurant.class);
                startActivityForResult(intent, RESTAURANT_REQUEST_CODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Request code", Integer.toString(requestCode));

        if (requestCode == SINGLE_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Log.i("Cancel", "Cancelled single activity");
            return;
        }
        if (requestCode == RECIPE_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Log.i("Cancel", "Cancelled recipe activity");
            return;
        }
        if (requestCode == RESTAURANT_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Log.i("Cancel", "Cancelled recipe activity");
            return;
        }

        if (requestCode == SINGLE_REQUEST_CODE || requestCode == RECIPE_REQUEST_CODE
                || requestCode == RESTAURANT_REQUEST_CODE) {
            Intent intent = new Intent();
            intent.putExtra("mealName", data.getStringExtra("mealName"));
            intent.putExtra("calories", data.getStringExtra("calories"));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
