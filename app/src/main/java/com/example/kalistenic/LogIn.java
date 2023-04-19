package com.example.kalistenic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

public class LogIn extends AppCompatActivity {
    private EditText email, password;
    private Button loginBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initWidgets();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DatabaseHelper(LogIn.this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String[] projection = {"passwordHash", "salt"};
                String selection = "email" + " = ?";
                String[] selectionArgs = { Email };
                Cursor cursor = db.query(
                        "users",
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                if (cursor.moveToFirst()) {
                    String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("passwordHash"));
                    String storedSalt = cursor.getString(cursor.getColumnIndexOrThrow("salt"));
                    String UsersPass = BCrypt.hashpw(Password, storedSalt);
                    if (UsersPass.equals(storedPassword)){
                        Toast.makeText(LogIn.this, "Login successfull!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, MainActivity.class);
                        int userId = dbHelper.getUserId(Email, Password);
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId);
                        editor.apply();
                        startActivity(intent);
                    } else {
                        Toast.makeText(LogIn.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LogIn.this, "No User found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initWidgets() {
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.logInBtn);
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