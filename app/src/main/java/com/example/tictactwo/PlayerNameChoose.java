package com.example.tictactwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNameChoose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name_choose);

        EditText playerNameEt = findViewById(R.id.playerNameEt);
        AppCompatButton startGameBtn = findViewById(R.id.startGameBtn);

        startGameBtn.setOnClickListener(v -> {

            // Mi salvo in una variabile il nome inserito dal giocatore
            String getPlayerName = playerNameEt.getText().toString();

            // Controllo se il giocatore ha inserito un nome valido
            if(getPlayerName.isEmpty()){
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.empty_player_name), Toast.LENGTH_LONG).show();
            } else {

                // Creazione dell'intent per aprire l'activity di gioco
                Intent intent = new Intent(PlayerNameChoose.this, HumanOnlineGameplayActivity.class);

                // Aggiungo il nome del giocatore come extra all'intent in modo da portarmelo dietro nella prossima activity
                intent.putExtra(getApplicationContext().getString(R.string.player_name), getPlayerName);

                // Faccio partire la nuova activity
                startActivity(intent);

                // Distruggo questa activity che non mi serve piu
                finish();
            }
        });
    }
}