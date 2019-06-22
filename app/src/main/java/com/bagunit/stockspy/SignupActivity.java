package com.bagunit.stockspy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText usersName;
    private EditText pass;
    private EditText confirmPass;
    private Button signUpButton;
    private boolean showPass , showConfirmPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        usersName = findViewById(R.id.userName);
        email = findViewById(R.id.signUpEmail);
        pass = findViewById(R.id.signUpPassword);
        confirmPass = findViewById(R.id.confirmPass);
        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this);
        findViewById(R.id.showPassSU).setOnClickListener(this);
        findViewById(R.id.showConfirmPassSU).setOnClickListener(this);
        findViewById(R.id.loginOption).setOnClickListener(this);

    }

    private void registerUser(){

        String userEmail = email.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String passConf = confirmPass.getText().toString().trim();

        if ( userEmail.isEmpty() ){
            email.setError("Email address is required");
            email.requestFocus();
            return;
        }

        if ( !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() ){
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }

        if ( password.isEmpty() ){
            pass.setError("Password is required");
            pass.requestFocus();
            return;
        }

        if ( password.length() < 6 ){
            pass.setError("Password must be at least 6 characters long");
            pass.requestFocus();
            return;
        }

        if ( password.compareTo(passConf) != 0 ){
            confirmPass.setError("Passwords do not match");
            confirmPass.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(userEmail , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){
                    Toast.makeText(getApplicationContext() , usersName.getText().toString()+ " registered successfully" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext() , "An error occured, please try again" , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//register user

    private void hideKeyboard(){
        View v = getCurrentFocus();
        if ( v != null ){
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.signUpButton:
                hideKeyboard();
                registerUser();
                startActivity(new Intent(SignupActivity.this , LoginActivity.class));
                break;

            case R.id.showPassSU:

                if ( !showPass ){
                    showPass = !showPass;
                    pass.setTransformationMethod(null);
                    if ( pass.getText().length() > 0 ) {
                        pass.setSelection(pass.getText().length());
                    }
                } else {
                    showPass = !showPass;
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                    if ( pass.getText().length() > 0 ){
                        pass.setSelection(pass.getText().length());
                    }
                }

                break;

            case R.id.showConfirmPassSU:

                if ( !showConfirmPass ){
                    showConfirmPass = !showConfirmPass;
                    confirmPass.setTransformationMethod(null);
                    if ( confirmPass.getText().length() > 0 ) {
                        confirmPass.setSelection(confirmPass.getText().length());
                    }
                } else {
                    showConfirmPass = !showConfirmPass;
                    confirmPass.setTransformationMethod(new PasswordTransformationMethod());
                    if ( confirmPass.getText().length() > 0 ){
                        confirmPass.setSelection(confirmPass.getText().length());
                    }
                }

                break;

            case R.id.loginOption:

                startActivity(new Intent(SignupActivity.this , LoginActivity.class ));
                finish();
                break;
        }
    }
}
