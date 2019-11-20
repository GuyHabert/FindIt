package com.example.findit.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findit.R;
import com.example.findit.SecondScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;
import static com.example.findit.Constants.mAuth;

public class SigninActivity extends AppCompatActivity implements PermissionListener {
    MaterialButton btnSignIn,btnSignUp;
    TextView btnForgotPass;
    TextInputEditText edEmail,edPass;
    Switch switchAvailable;
    Sheriff sheriffPermission;
    int REQUEST_SINGLE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //initialize sherif permission
        initSherifPermission();
        sheriffPermission.requestPermissions();

        init();
        listeners();

        boolean data = Prefs.getBoolean("is_login",false);
        if(data){
            startActivity(new Intent(SigninActivity.this,SecondScreenActivity.class));
            finish();
        }
    }

    private void listeners() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    sheriffPermission.requestPermissions();
                    return;
                }else {
                    if (edEmail.getText().toString().equals("")) {
                        edEmail.setError("Required");
                        return;
                    }
                    if (edPass.getText().toString().equals("")) {
                        edPass.setError("Required");
                        return;
                    }
                    authListener();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this,SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.nothing);
            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.nothing);
            }
        });

    }

    private void authListener() {
        mAuth.signInWithEmailAndPassword(edEmail.getText().toString(), edPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MyApp", "signInWithEmail:success");
                            if(switchAvailable.isChecked()){
                                Prefs.putBoolean("is_login",true);
                            }
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SigninActivity.this, SecondScreenActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MyApp", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.btn_signin);
        btnSignUp = findViewById(R.id.btn_signup);
        btnForgotPass = findViewById(R.id.btn_forgotPass);
        edEmail = findViewById(R.id.txt_email);
        edPass = findViewById(R.id.txt_pass);
        switchAvailable = findViewById(R.id.switch_available);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(false)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void initSherifPermission() {
        sheriffPermission = Sheriff.Builder()
                .with(this)
                .requestCode(REQUEST_SINGLE_PERMISSION)
                .setPermissionResultCallback(this)
                .askFor(SheriffPermission.LOCATION)
                .build();
    }


    @Override
    public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {
        Toast.makeText(this, "You need to allow storage access", Toast.LENGTH_SHORT).show();
        sheriffPermission.requestPermissions();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
