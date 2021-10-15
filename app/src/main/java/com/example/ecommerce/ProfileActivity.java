package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    CustomerInfo c = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditText name = (EditText)findViewById(R.id.nametxt);
        EditText username = (EditText)findViewById(R.id.usernametxt);
        EditText password = (EditText)findViewById(R.id.passwordtxt);
        EditText gender = (EditText)findViewById(R.id.gendertxt);
        EditText birthdate = (EditText)findViewById(R.id.birthtxt);
        EditText job = (EditText)findViewById(R.id.jobtxt);

        c =  (CustomerInfo)getIntent().getSerializableExtra("cust_object");
        String[] custInfo = c.getInfo();
        name.setText(custInfo[1]);
        username.setText(custInfo[2]);
        password.setText(custInfo[3]);
        gender.setText(custInfo[4]);
        birthdate.setText(custInfo[5]);
        job.setText(custInfo[6]);

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNav);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch (item.getItemId())
                {
                    case R.id.homepage:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cart:
                        i = new Intent(getApplicationContext(), CartActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cat:
                        i = new Intent(getApplicationContext(), CategoriesActivity.class);
                        i.putExtra("cust_object", c);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

    }
}
