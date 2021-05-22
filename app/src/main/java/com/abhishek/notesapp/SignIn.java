package com.abhishek.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    TextView tv1,tvpwd;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    EditText ed1,ed2;
    Button SignInlog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        tv1=findViewById(R.id.signuptv);
        ed1=findViewById(R.id.usernamelogn);
        Intent data=getIntent();
        ed2=findViewById(R.id.passwordsignin1);
        Intent signup=getIntent();
        tvpwd=findViewById(R.id.fgtpwd);
        SignInlog=findViewById(R.id.signinlog);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        String update=data.getStringExtra("dataname");
        ed1.setText(update);

        String fetchmail=signup.getStringExtra("emailextra");
        String fetchpwd=signup.getStringExtra("pwdextra");
        ed1.setText(fetchmail);
        ed2.setText(fetchpwd);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        tvpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });


        SignInlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }
    private void Login()
    {
        String str1=ed1.getText().toString();
        String str2=ed2.getText().toString();

        if (TextUtils.isEmpty(str1)) {
            Toast.makeText(SignIn.this, "mail not entered", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(str2)) {
            Toast.makeText(SignIn.this, "password not entered", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(str1, str2).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    emailVerified();
                }
                else
                {
                    Toast.makeText(SignIn.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void emailVerified()
    {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true)
        {
            Toast.makeText(SignIn.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(SignIn.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(SignIn.this, "Mail Not Verified", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(SignIn.this, "Mail sent,Verify first!", Toast.LENGTH_LONG).show();
                    }
            });
            }
        }

    }