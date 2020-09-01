package cs490.cal_o_meter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;
    Button registerButton;
    ProgressBar progressBar;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    public void registerClick(View view){
        final String uEmail = email.getText().toString().trim();
        final String uName= username.getText().toString().trim();
        final String uPass= password.getText().toString().trim();

        fStore = FirebaseFirestore.getInstance();

        if (uEmail.isEmpty()){
            email.setError("Email is required");
            return;
        }
        if (uName.isEmpty()){
            username.setError("Username is required");
            return;
        }
        if (uPass.length()<6){
            password.setError("Password must be larger than 6 chars");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(uEmail, uPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();

                    final String userID = fAuth.getCurrentUser().getUid();

                    Map<String,String> user = new HashMap<>();
                    user.put("username",uName);
                    user.put("email",uEmail);

                    fStore.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("FIREBASE", "onSuccess: user Profile is created for "+ userID);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("FIREBASE", "onFailure: " + e.toString());
                        }
                    });


                } else {
                    Toast.makeText(Register.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void goToLogin(View v){
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth= FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        username = findViewById(R.id.RegisterUsername);
        email = findViewById(R.id.RegisterEmail);
        password = findViewById(R.id.RegisterPassword);
        username = findViewById(R.id.RegisterUsername);
        registerButton= findViewById(R.id.RegisterButton);
        progressBar= findViewById(R.id.registerProgressBar);







    }
}
