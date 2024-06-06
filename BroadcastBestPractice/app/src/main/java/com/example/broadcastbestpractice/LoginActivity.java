package com.example.broadcastbestpractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends BaseActivity {

    private EditText accountEdit, passwordEdit;

    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String account = accountEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (account.equals("admin") && password.equals("12345")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "account or password not correct",Toast.LENGTH_SHORT).show();
            }
        });
    }
}