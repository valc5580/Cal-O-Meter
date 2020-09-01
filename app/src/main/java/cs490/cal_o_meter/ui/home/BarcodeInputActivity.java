package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;

public class BarcodeInputActivity extends AppCompatActivity {
    int SCAN_REQUEST_CODE = 1;


    String barcodeName;
    int barcodeCalories;

    FirebaseFirestore fStore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_input);

        fStore = FirebaseFirestore.getInstance();

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button submitButton = (Button) findViewById(R.id.barcode_select_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("mealName", ((TextView) findViewById(R.id.barcodeName)).getText().toString());
                intent.putExtra("calories", ((TextView) findViewById(R.id.barcodeCalories)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent= new Intent(BarcodeInputActivity.this, BarcodeScanActivity.class);
                startActivityForResult(intent,SCAN_REQUEST_CODE);

            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled scan");
                return;
            }
            final Barcode barcode = data.getParcelableExtra("barcode");
            Log.i("SCAN", "Received barcode");
            Log.i("SCAN", barcode.displayValue);
            ((Button)findViewById(R.id.scan_button)).setText("SCAN AGAIN");
            ((TableLayout)findViewById(R.id.barcodeProductTable)).setVisibility(View.VISIBLE);

            fStore.collection("barcodes")
                    .document(barcode.displayValue)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()){
                                    Log.i("Success", "Barcode Found");



                                    String barcodeName = task.getResult().getString("mealName");
                                    int barcodeCalories = task.getResult().getLong("calories").intValue();
                                    ((Button)findViewById(R.id.barcode_select_button)).setVisibility(View.VISIBLE);
                                    ((TextView)findViewById(R.id.barcodeName)).setText(barcodeName);
                                    ((TextView)findViewById(R.id.barcodeCalories)).setText(Integer.toString(barcodeCalories));
                                    ((TextView)findViewById(R.id.barcodeNum)).setText(barcode.displayValue);

                                } else {
                                    Log.i("Error", "No such barcode found");
                                    Toast.makeText(BarcodeInputActivity.this, "Barcode "+barcode.displayValue+ " not found", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Toast.makeText(BarcodeInputActivity.this, "Error searching barcode", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }

    }
}