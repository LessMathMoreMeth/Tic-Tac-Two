package com.example.tictactwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LocalGameChoose extends AppCompatActivity {

    AppCompatButton btnCpu;
    AppCompatButton btnHuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_game_choose);

        btnCpu = findViewById(R.id.btnCpu);
        btnHuman = findViewById(R.id.btnHuman);

        btnCpu.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CpuGameplayActivity.class)));

        btnHuman.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HumanLocalGameplayActivity.class)));
    }
}