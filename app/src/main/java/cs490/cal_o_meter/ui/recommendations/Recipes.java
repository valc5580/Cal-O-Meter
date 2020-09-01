package cs490.cal_o_meter.ui.recommendations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cs490.cal_o_meter.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipes extends AppCompatActivity {

    void populateRecipe(JSONObject json) throws JSONException {
//        final String restName= json.getJSONArray("locations").getJSONObject(0).getString("name");
//        final String address= json.getJSONArray("locations").getJSONObject(0).getString("address");
//        final String city = json.getJSONArray("locations").getJSONObject(0).getString("city");
//        final String distance = json.getJSONArray("locations").getJSONObject(0).getString("distance_km");

//        final String recipeName= "Ham and Spinach Sandwich";
//        final String calories= "300";
//        final String ingredients = "ham, spinach, mayo, bread";

        for (int i=0;i<3;i++) {
            final String recipeName = json.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("label");
            final float totalCalories= Float.parseFloat(json.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("calories"));
            final float yield = Float.parseFloat(json.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("yield"));
            String url = json.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("url");
            String calories = String.valueOf((int) Math.rint(totalCalories/yield));

            if (i == 0) {
                (findViewById(R.id.recipeTable)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.recipe_name1)).setText(recipeName);
                ((TextView)findViewById(R.id.recipe_calories1)).setText(calories);
                ((TextView)findViewById(R.id.recipe_url1)).setText(url);
            } else if (i == 1) {
                ((TextView)findViewById(R.id.recipe_name2)).setText(recipeName);
                ((TextView)findViewById(R.id.recipe_calories2)).setText(calories);
                ((TextView)findViewById(R.id.recipe_url2)).setText(url);
            } else if (i == 2) {
                ((TextView)findViewById(R.id.recipe_name3)).setText(recipeName);
                ((TextView)findViewById(R.id.recipe_calories3)).setText(calories);
                ((TextView)findViewById(R.id.recipe_url3)).setText(url);
            }

        }

//        ((TextView)findViewById(R.id.recipe_ingredients1)).setText(ingredients);
    }

    String app_id="x";
    String app_key="x";


    void searchAndGetJSON(String foodName, String calories){
        final OkHttpClient client = new OkHttpClient();

        String url = "https://api.edamam.com/search?q="
        + foodName + "&app_id="+app_id+"&app_key="+app_key+"&from=0&to=3&calories=" + calories;

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Recipes.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String error = e.toString();
                        Log.i("failure", error);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("Request", "on response");
                if (response.isSuccessful()) {
                    Log.i("Request", "successful");
                    final String myResponse = response.body().string();

                   Recipes.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Request", myResponse);
                            JSONObject json;
                            try {
                                json = new JSONObject(myResponse);
                                populateRecipe(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_recommendations);

        Button submitButton1 = findViewById(R.id.recipe_selected1);
        submitButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.recipe_name1)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.recipe_calories1)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button submitButton2 = findViewById(R.id.recipe_selected2);
        submitButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.recipe_name2)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.recipe_calories2)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button submitButton3 = findViewById(R.id.recipe_selected3);
        submitButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.recipe_name3)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.recipe_calories3)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.search_name)).getText().toString();
                String calories = ((EditText) findViewById(R.id.search_calories)).getText().toString();
                searchAndGetJSON(name, calories);
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
