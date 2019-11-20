package com.example.findit.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.findit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.findit.Constants.mAuth;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText edEmail,edPass,edPass2;
    MaterialButton btnSignUp,btnBackSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        listerners();
    }

    private void signUpListener() {
        mAuth.createUserWithEmailAndPassword(edEmail.getText().toString(), edPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MyApp", "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "SignUp Successful",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("rss", task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this,task.getException().getMessage() ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void listerners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edEmail.getText().toString().equals("")){
                    edEmail.setError("Required!");
                    return;
                }
                if(edPass.getText().toString().equals("")){
                    edPass.setError("Required!");
                    return;
                }
                if(edPass2.getText().toString().equals("")){
                    edPass2.setError("Required!");
                    return;
                }

                if(edPass.getText().toString().equals(edPass2.getText().toString())){
                    signUpListener();
                }else{
                    Toast.makeText(SignUpActivity.this, "Password must match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();

        edEmail = findViewById(R.id.txt_email);
        edPass = findViewById(R.id.txt_pass);
        edPass2 = findViewById(R.id.txt_pass_2);
        btnSignUp = findViewById(R.id.btn_signup);
        btnBackSignIn = findViewById(R.id.btn_signin_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.slide_down);
    }
}
