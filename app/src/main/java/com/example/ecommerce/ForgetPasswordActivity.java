package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        final EditText username = (EditText)findViewById(R.id.usernameText2);
        final EditText password = (EditText)findViewById(R.id.newpassText);

        Button btn = (Button)findViewById(R.id.resetBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper db = new DataBaseHelper(getApplicationContext());
                db.updatePassword(username.getText().toString(), password.getText().toString());
                Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
