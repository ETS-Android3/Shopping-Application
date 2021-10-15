package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    EditText DOB;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final DataBaseHelper db = new DataBaseHelper(this);

        final EditText name = (EditText)findViewById(R.id.nametxt);
        final EditText username = (EditText)findViewById(R.id.usernametxt);
        final EditText password = (EditText)findViewById(R.id.passwordtxt);
        final EditText job = (EditText)findViewById(R.id.jobtxt);

        DOB = findViewById(R.id.birthtxt);
        Calendar calender = Calendar.getInstance();
        final int year = calender.get(Calendar.YEAR);
        final int month = calender.get(Calendar.MONTH);
        final int day = calender.get(Calendar.DAY_OF_MONTH);

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month=month+1;
                        String date = day + "/" + month + "/" + year;
                        DOB.setText(date);
                    }
                }
                , year, month, day);
                datePickerDialog.show();
            }
        });

        Button SignUpbtn = (Button)findViewById(R.id.signUpBtn);
        SignUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String gender;
                RadioButton female = (RadioButton)findViewById(R.id.female);
                RadioButton male = (RadioButton)findViewById(R.id.male);
                if(female.isChecked())
                    gender = "female";
                else
                    gender = "male";

                db.addCustomer(name.getText().toString(), username.getText().toString(), password.getText().toString(), gender, DOB.getText().toString(), job.getText().toString());
                Toast.makeText(getApplicationContext(), "SignUp Successfully", Toast.LENGTH_LONG).show();

                finish();
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
