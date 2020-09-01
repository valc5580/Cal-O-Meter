package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PicInputActivity extends AppCompatActivity {
    int PIC_REQUEST_CODE = 1;

    int CAMERA_REQUEST = 1888;

    void getPicAndPopulateTable(JSONObject json) throws JSONException {
        final String foodName= json.getJSONArray("foods").getJSONObject(0).getString("food_name");
        final int calories= json.getJSONArray("foods").getJSONObject(0).getInt("nf_calories");
        String imageURL= json.getJSONArray("foods").getJSONObject(0).getJSONObject("photo").getString("thumb");
        final String unit = json.getJSONArray("foods").getJSONObject(0).getString("serving_unit");
        final String grams = json.getJSONArray("foods").getJSONObject(0).getString("serving_weight_grams") + "g";

        final ImageView image =(ImageView)findViewById(R.id.foodPic);

        final OkHttpClient client2 = new OkHttpClient();
        //String url2 = "https://d1r9wva3zcpswd.cloudfront.net/5c8b5306c397fd0679775147.jpeg";

        Request request2 = new Request.Builder()
                .url(imageURL)
                .build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PicInputActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i("failure", "failure");
                        //mTextViewResult.setText("shit failed");
                        //e.printStackTrace();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("Request2", "on response");
                if (response.isSuccessful()) {
                    Log.i("Request", "succesful");
                    //final String myResponse = response.body().string();
                    final InputStream inputStream = response.body().byteStream();
                    final Bitmap map = BitmapFactory.decodeStream(inputStream);
                    PicInputActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            image.setImageBitmap(map);
                            ((TableLayout)findViewById(R.id.productTable)).setVisibility(View.VISIBLE);
                            ((Button)findViewById(R.id.select_button)).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.foodName)).setText(foodName);
                            ((TextView)findViewById(R.id.calories)).setText(Integer.toString(calories));
                            ((TextView)findViewById(R.id.servingUnit)).setText(unit);
                            ((TextView)findViewById(R.id.grams)).setText(grams);


                        }
                    });
                }
            }
        });
    }


    void searchAndGetJSON(String name){
        Map<String, Object> data = new HashMap<>();
        data.put( "mealName", name );

        FirebaseFunctions.getInstance() // Optional region: .getInstance("europe-west1")
                .getHttpsCallable("foodImageRecongitionAPI2")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return new Gson().toJson(task.getResult().getData());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull final Task<String> task) {
                        Log.i("onComplete", "got to onComplete");
                        if (!task.isSuccessful()) {
                            Log.i("ERROR", "Exception");
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.i("Error", details.toString());
                            }
                        } else {
                            Log.i("RETURNED", "Returned "+ task.getResult());
                            try {
                                String json = task.getResult();
                                final JSONObject obj=new JSONObject(json);
                                if (obj.get("message").equals("SUCCESS"))
                                    getPicAndPopulateTable(obj);
                                else
                                    Log.i("ERROR", "Call returned ERROR as message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    void getImageLabelThenGetNutritionThenFillTable(String base64Photo){
        Map<String, Object> data = new HashMap<>();
        data.put( "image", base64Photo );

        FirebaseFunctions.getInstance() // Optional region: .getInstance("europe-west1")
                .getHttpsCallable("foodImageRecongitionAPI")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return new Gson().toJson(task.getResult().getData());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull final Task<String> task) {
                        Log.i("onComplete", "got to onComplete");
                        if (!task.isSuccessful()) {
                            Log.i("ERROR", "Exception");
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.i("Error", details.toString());
                            }
                        } else {
                            Log.i("RETURNED", "Returned "+ task.getResult());

                            try {
                                String json = task.getResult();
                                final JSONObject obj=new JSONObject(json);
                                if (obj.get("message").equals("SUCCESS"))
                                    searchAndGetJSON(obj.getString("item"));
                                else
                                    Log.i("ERROR", "Call returned ERROR as message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_input);



        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button submitButton = (Button) findViewById(R.id.select_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.foodName)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.calories)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button picButton = (Button) findViewById(R.id.takePicButton);
        picButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {

            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled taking pic");
                return;
            }
            //searchAndGetJSON("pizza");

            Log.i("Done", "Took pic!");
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //convert to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            getImageLabelThenGetNutritionThenFillTable(base64Photo);
            /*
            ImageView pic= (ImageView)findViewById(R.id.foodPic);
            pic.setImageBitmap(photo);
            ((TableLayout)findViewById(R.id.productTable)).setVisibility(View.VISIBLE);
             */
            ((Button)findViewById(R.id.takePicButton)).setText("Take Pic Again");


        }

    }
}