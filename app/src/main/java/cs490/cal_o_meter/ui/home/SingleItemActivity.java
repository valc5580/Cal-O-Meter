package cs490.cal_o_meter.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import cs490.cal_o_meter.R;

public class SingleItemActivity extends AppCompatActivity {

    int MANUAL_REQUEST_CODE=1;
    int SEARCH_BY_NAME_REQUEST_CODE=2;
    int BARCODE_REQUEST_CODE=3;
    int PIC_REQUEST_CODE=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button manualButton = (Button) findViewById(R.id.manualButton);
        manualButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SingleItemActivity.this, ManualInputActivity.class);
                startActivityForResult(intent, MANUAL_REQUEST_CODE);
            }
        });


        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SingleItemActivity.this, SearchByNameActivity.class);
                startActivityForResult(intent, SEARCH_BY_NAME_REQUEST_CODE);
            }
        });

        Button scanBarcodeButton = (Button) findViewById(R.id.barcodeButton);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SingleItemActivity.this, BarcodeInputActivity.class);
                startActivityForResult(intent, BARCODE_REQUEST_CODE);
            }
        });

        Button takePicButton = (Button) findViewById(R.id.takePicButton);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SingleItemActivity.this, PicInputActivity.class);
                startActivityForResult(intent, PIC_REQUEST_CODE);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Request code", Integer.toString(requestCode));

        if (requestCode == MANUAL_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled manual input");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("mealName", data.getStringExtra("mealName"));
            intent.putExtra("calories", data.getStringExtra("calories"));
            setResult(RESULT_OK, intent);
            finish();

        }

        if (requestCode == SEARCH_BY_NAME_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled search by name input");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("mealName", data.getStringExtra("mealName"));
            intent.putExtra("calories", data.getStringExtra("calories"));
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == BARCODE_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled input by barcode");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("mealName", data.getStringExtra("mealName"));
            intent.putExtra("calories", data.getStringExtra("calories"));
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == PIC_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Log.i("Cancel", "Cancelled input by pic");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("mealName", data.getStringExtra("mealName"));
            intent.putExtra("calories", data.getStringExtra("calories"));
            setResult(RESULT_OK, intent);
            finish();
        }





    }
}
