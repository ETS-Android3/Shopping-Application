package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PrefsFile";

    EditText username;
    EditText password;
    CheckBox remCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final DataBaseHelper db = new DataBaseHelper(getApplicationContext());

        //SignUp
        Button signUpBtn = (Button)findViewById(R.id.SignUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        username = (EditText)findViewById(R.id.usernametxt);
        password = (EditText)findViewById(R.id.passwordtxt);
        remCheckBox = (CheckBox)findViewById(R.id.checkBox);

        getPrefData();

        Button Login = (Button)findViewById(R.id.loginBtn);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean res = db.CheckExsist(username.getText().toString(), password.getText().toString());
                if(res)
                {
                    Cursor cursor = db.fetchCustomerInfo(username.getText().toString(), password.getText().toString());
                    CustomerInfo c = new CustomerInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));

                    SharedPreferences savePref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    if(remCheckBox.isChecked()) {
                        Boolean isChecked = remCheckBox.isChecked();
                        SharedPreferences.Editor editor = savePref.edit();
                        editor.putString("Login_username", username.getText().toString());
                        editor.putString("Login_password", password.getText().toString());
                        editor.putBoolean("Login_remember", isChecked);
                        editor.apply();
                    }
                    else
                    {
                        savePref.edit().clear().apply();
                    }

                    Toast.makeText(getApplicationContext(), "Login Successfully, Welcome", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("cust_object", c);
                    startActivity(i);

                    username.getText().clear();
                    password.getText().clear();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
                    ((EditText)findViewById(R.id.usernametxt)).setText(null);
                    ((EditText)findViewById(R.id.passwordtxt)).setText(null);
                }
            }
        });

        TextView forgetPass = (TextView)findViewById(R.id.forgettxt);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);
            }
        });
    }
    private void getPrefData()
    {
        SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(sp.contains("Login_username"))
        {
            String un = sp.getString("Login_username", "not found");
            username.setText(un.toString());
        }
        if(sp.contains("Login_password"))
        {
            String pass = sp.getString("Login_password", "not found");
            password.setText(pass.toString());
        }
        if(sp.contains("Login_remember"))
        {
            Boolean me = sp.getBoolean("Login_remember", false);
            remCheckBox.setChecked(me);
        }
    }
}
