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
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText userEmail , userPassword;
    private Button loginButton;
    private int viewPass = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar tool = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        tool.setPadding(0,0,100,5);


        mAuth = FirebaseAuth.getInstance();

        if ( mAuth.getCurrentUser() != null ){
            finish();
            //GOTO profile activity
        }

        userEmail = findViewById(R.id.signInEmail);
        userPassword = findViewById(R.id.signInPassword);
        loginButton = findViewById(R.id.loginButton);
        findViewById(R.id.showPassword).setOnClickListener(this);
        loginButton.setOnClickListener(this);

        System.out.println(viewPass);

    }


    private void userLogin(){
        String email = userEmail.getText().toString().trim();
        String pass = userPassword.getText().toString().trim();

        if ( email.isEmpty() ){
            userEmail.setError("Email is required");
            userEmail.requestFocus();
            return;
        }

        if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
            userEmail.setError("Please enter a valid email address");
            userEmail.requestFocus();
            return;
        }

        if ( pass.isEmpty() ){
            userPassword.setError("Password is required");
            userPassword.requestFocus();
            return;
        }

        if ( pass.length() < 6 ){
            userPassword.setError("Password must be at least 6 characters long");
            userPassword.requestFocus();
            return;
        }
        //now we have verified that we have valid credentials, lets login

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){
                    finish();
                    //launch the profile
                } else {
                    Toast.makeText(getApplicationContext() , "Username or password incorrect. Try again" , Toast.LENGTH_LONG).show();
                }

            }
        });

    }//userLogin

    private void hideKeyboard(){
        View v = getCurrentFocus();
        if ( v != null ){
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){

            case R.id.showPassword:
                System.out.println(viewPass);
                if ( viewPass == 0 ){
                    viewPass = 1;
                    userPassword.setTransformationMethod(null);
                    if ( userPassword.getText().length() > 0 ) {
                        userPassword.setSelection(userPassword.getText().length());
                    }
                } else {
                    viewPass = 0;
                    userPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if ( userPassword.getText().length() > 0 ){
                        userPassword.setSelection(userPassword.getText().length());
                    }
                }

                break;

            case R.id.loginButton:
                userLogin();
                break;

        }
    }
}
