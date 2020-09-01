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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {


    EditText email;
    EditText password;
    Button registerButton;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    public void goToRegister(View v){
        startActivity(new Intent(getApplicationContext(), Register.class));
        finish();
    }

    public void loginClick(View view){
        String uEmail = email.getText().toString().trim();
        String uPass= password.getText().toString().trim();
        if (uEmail.isEmpty()){
            email.setError("Email is required");
            return;
        }
        if (uPass.length()<6){
            password.setError("Password must be larger than 6 chars");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        fAuth.signInWithEmailAndPassword(uEmail, uPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    FirebaseUser user = fAuth.getCurrentUser();
                    Log.i("Success","Success");
                    Log.i("Successful login of: ", user.getEmail());
                    startActivity(new Intent(Login.this,MainActivity.class));
                    finish();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i( "signInWithEmail:failure", task.getException().getMessage());
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // ...
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth= FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        email=findViewById(R.id.LoginEmail);

        password = findViewById(R.id.LoginPassword);

        progressBar= findViewById(R.id.loginProgressBar);
    }
}
