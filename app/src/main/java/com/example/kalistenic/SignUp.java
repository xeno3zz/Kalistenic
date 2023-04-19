package com.example.kalistenic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.mindrot.jbcrypt.BCrypt;

public class SignUp extends AppCompatActivity {

    private EditText email, password, firstName, lastName;
    private Button registerBtn;
    private TextView login;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initWidgets();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(SignUp.this);
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String LastName = lastName.getText().toString();
                String FirstName = firstName.getText().toString();
                String Salt = BCrypt.gensalt();
                String PasswordHash = BCrypt.hashpw(Password, Salt);
                db.addUser(Email, PasswordHash, FirstName, LastName, Salt);
                int userId = db.getUserId(Email, Password);

                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("userId", userId);
                editor.apply();

                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);


            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
            }
        });
    }

    private void initWidgets() {
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        firstName = findViewById(R.id.firstnameET);
        lastName = findViewById(R.id.lastnameET);
        registerBtn = findViewById(R.id.saveBtn);
        login = findViewById(R.id.logInTV);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // This regex matches any character that is not a symbol, letter of the English alphabet, or number
                String regex = "[^a-zA-Z0-9\\p{Punct}]";

                // Loop through each character in the input and check if it matches the regex
                for (int i = start; i < end; i++) {
                    if (Character.toString(source.charAt(i)).matches(regex)) {
                        // If the character does not match the regex, return an empty string to block the input
                        return "";
                    }
                }

                // If all characters match the regex, return null to allow the input
                return null;
            }

        };
        password.setFilters(new InputFilter[]{ filter });
    }
}