package com.example.tictactwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    AppCompatButton btnLocal;
    AppCompatButton btnRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocal = findViewById(R.id.btnLocal);
        btnRemote = findViewById(R.id.btnMulti);

        btnLocal.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),LocalGameChoose.class)));

        btnRemote.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),PlayerNameChoose.class)));
    }
}