package com.example.tryauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText phone, otp, pass;
    Button btngenOTP, btnverify;
    String verificationID;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        pass = findViewById(R.id.pass);
        btnverify = findViewById(R.id.btnverifyOTP);
        btngenOTP = findViewById(R.id.btngenerateOTP);
        mAuth = FirebaseAuth.getInstance();
        bar = findViewById(R.id.bar);

        btngenOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phone.getText().toString()))
                    Toast.makeText(MainActivity.this, "enter valid phone nm.", Toast.LENGTH_SHORT).show();
                else{
                    String number = phone.getText().toString();
                    bar.setVisibility(View.VISIBLE);
                    sendverificationcode(number);
                }
            }
        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(otp.getText().toString()))
                    Toast.makeText(MainActivity.this, "enter valid phone nm.", Toast.LENGTH_SHORT).show();
                else
                    verifycode(otp.getText().toString());
            }
        });

    }

    private void sendverificationcode(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+972"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential)
        {
           final String code = credential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainActivity.this, "verification failed", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificationID = s;
            Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
            btnverify.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);
        }
    };

    private void verifycode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, Code);
        signinbycredentials(credential);
    }

    private void signinbycredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}