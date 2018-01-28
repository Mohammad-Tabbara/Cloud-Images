package com.example.mohammad.cloudimages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AutoCompleteTextView email;
    private Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        resetPassword = (Button) findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString());
                Toast.makeText(ForgotPasswordActivity.this,"Email Send to Reset Password",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
