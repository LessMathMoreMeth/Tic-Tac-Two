package com.example.tictactwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class HumanLocalGameplayActivity extends AppCompatActivity implements View.OnClickListener {

    // Variabile che tiene traccia di quale sia il giocatore con il turno attivo
    // true -> Player 1 turn
    // false -> Player 2 turn
    boolean playerTurn = true;
    int player1Score = 0;
    int player2Score = 0;
    ArrayList<Integer> player1Cells = new ArrayList<>();
    ArrayList<Integer> player2Cells = new ArrayList<>();
    ArrayList<Integer> alreadyHittedCells = new ArrayList<>();
    TextView tvPlayer1;
    TextView tvPlayer2;
    AppCompatButton btnCell1;
    AppCompatButton btnCell2;
    AppCompatButton btnCell3;
    AppCompatButton btnCell4;
    AppCompatButton btnCell5;
    AppCompatButton btnCell6;
    AppCompatButton btnCell7;
    AppCompatButton btnCell8;
    AppCompatButton btnCell9;
    AppCompatButton btnHome;
    AppCompatButton btnReset;
    Drawable enemy_marker;
    Drawable player_marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_local_gameplay);

        // Ottengo il riferimento alle TextView del giocatore e della cpu
        tvPlayer1 = findViewById(R.id.tvLocalHumanPlayer1);
        tvPlayer2 = findViewById(R.id.tvLocalHumanPlayer2);

        // Ottengo i riferimenti delle celle di gioco
        btnCell1 = findViewById(R.id.btnLocalHumanCell1);
        btnCell2 = findViewById(R.id.btnLocalHumanCell2);
        btnCell3 = findViewById(R.id.btnLocalHumanCell3);
        btnCell4 = findViewById(R.id.btnLocalHumanCell4);
        btnCell5 = findViewById(R.id.btnLocalHumanCell5);
        btnCell6 = findViewById(R.id.btnLocalHumanCell6);
        btnCell7 = findViewById(R.id.btnLocalHumanCell7);
        btnCell8 = findViewById(R.id.btnLocalHumanCell8);
        btnCell9 = findViewById(R.id.btnLocalHumanCell9);

        // Ottengo riferimento dei bottoni in basso nella schermata
        btnHome = findViewById(R.id.btnLocalHumanHome);
        btnReset = findViewById(R.id.btnLocalHumanReset);

        // Inizializzione di un Listener nelle celle di gioco
        btnCell1.setOnClickListener(this);
        btnCell2.setOnClickListener(this);
        btnCell3.setOnClickListener(this);
        btnCell4.setOnClickListener(this);
        btnCell5.setOnClickListener(this);
        btnCell6.setOnClickListener(this);
        btnCell7.setOnClickListener(this);
        btnCell8.setOnClickListener(this);
        btnCell9.setOnClickListener(this);

        // Inizializzione di un Listener dei bottoni in basso
        btnReset.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        // Setup iniziale delle TextView del giocatore e della cpu
        updateTvScore();

        // Prendo il riferimento ai due Drawable che rappresentano la "X" e lo "0" del gioco
        enemy_marker = ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.circle_rounded_85, null);
        player_marker = ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.cross_rounded_85, null);
    }

    // Funzione che aggiorna le TextView del giocatore e della cpu
    // Aggiorna dopo ogni partita il counter di vittorie per questa serie di partite
    private void updateTvScore() {
        String myScoreStringPlayer = getResources().getString(R.string.player1) + " " + player1Score;
        tvPlayer1.setText(myScoreStringPlayer);
        String myScoreStringCpu = getResources().getString(R.string.player2) + " " + player2Score;
        tvPlayer2.setText(myScoreStringCpu);
    }

    // Funzione eseguita in seguito della pressione di uno dei tasti
    @Override
    public void onClick(@NonNull View v) {

        // Variabile che conterrà la mossa a seconda del tasto premuto dal giocatore
        int cellId = 0;

        // Variabile che contiene l'id del tasto premuto dall'utente
        int id = v.getId();

        // Controllo quale tasto è stato premuto
        if(id == R.id.btnLocalHumanReset){
            reset();
        } else if(id == R.id.btnLocalHumanHome){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if(id == R.id.btnLocalHumanCell1) {
            cellId = 1;
        } else if(id == R.id.btnLocalHumanCell2) {
            cellId = 2;
        } else if(id == R.id.btnLocalHumanCell3) {
            cellId = 3;
        } else if(id == R.id.btnLocalHumanCell4) {
            cellId = 4;
        } else if(id == R.id.btnLocalHumanCell5) {
            cellId = 5;
        } else if(id == R.id.btnLocalHumanCell6) {
            cellId = 6;
        } else if(id == R.id.btnLocalHumanCell7) {
            cellId = 7;
        } else if(id == R.id.btnLocalHumanCell8) {
            cellId = 8;
        } else if(id == R.id.btnLocalHumanCell9) {
            cellId = 9;
        }

        // Dopo aver determinato quale tasto viene premuto dal giocatore invoco la funzione che effettivamente esegue la mossa
        performMove(v, cellId);
    }

    // Funzione che resetta il gioco per iniziare una nuova partita
    private void reset() {

        // Svuoto la lista delle caselle premute dal giocatore 1
        player1Cells.clear();

        // Svuoto la lista delle caselle premute dal giocatore 2
        player2Cells.clear();

        // Svuoto la lista delle caselle premute da entrambi
        alreadyHittedCells.clear();

        // Setto il primo turno al giocatore 1
        playerTurn = true;

        // Reset dei bottoni allo stato inziale per una nuova partita
        resetButtons();

        // Aggiornamento dello score di questa serie di partite
        updateTvScore();
    }

    // Funzione che riporta alla stato iniziale i bottoni
    private void resetButtons() {

        // Cella 1
        resetSingleButton(btnCell1);

        // Cella 2
        resetSingleButton(btnCell2);

        // Cella 3
        resetSingleButton(btnCell3);

        // Cella 4
        resetSingleButton(btnCell4);

        // Cella 5
        resetSingleButton(btnCell5);

        // Cella  6
        resetSingleButton(btnCell6);

        //Cella 7
        resetSingleButton(btnCell7);

        // Cella 8
        resetSingleButton(btnCell8);

        // Cella 9
        resetSingleButton(btnCell9);

        // Bottone home
        btnHome.setClickable(true);

        // Bottone reset
        btnReset.setClickable(true);
    }

    // Funzione che prende in input un bottone ed esegue su di esso le operazioni per ripristinarlo allo stato inziale
    private void resetSingleButton(AppCompatButton currentButton) {

        // Riabilito il bottone perchè disabilitato durante il dialog di fine partita
        currentButton.setEnabled(true);

        // Riattivo il bottone per essere cliccato la prossima partita, viene disabilitato dopo che vivne premuto la prima volta nella scorsa partita
        currentButton.setClickable(true);

        // Rimposto lo sfondo come era prima che fosse cliccato
        currentButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_back_white));
    }

    // Funzione che esegue la mossa per entrambi i giocatori
    private void performMove(View v, int currentCell) {

        // Casto la view generica che mi viene passata in un bottone che è quello che mi serve
        AppCompatButton clickedButton = (AppCompatButton) v;

        //Controllo che sia una mossa di gioco e non di uno dei due bottoni in basso
        if(clickedButton != btnHome && clickedButton != btnReset){

            // Controllo che sia il turno del giocatore 1 se devo fare una mossa del giocatore 1
            if(playerTurn){

                // Operazione che aggiunge il marker alla casella seleionata
                clickedButton.setBackgroundDrawable(player_marker);

                // Aggiungo il numero della casella scelta dal giocatore 1 alla lista delle sue mosse
                player1Cells.add(currentCell);

                // Aggiungo il numero della casella scelta dal giocatore alla lista delle caselle già selezionate nella partita
                alreadyHittedCells.add(currentCell);

                // Cambio turno
                playerTurn = false;

                // Dopo la mossa del giocatore 1 possono succedere tre casi:
                // O la mossa del giocatore 1 era vincente --> Mostro il dialog di vittoria del giocatore 1
                // O la mossa del giocaotre 1 non era vincente ma era l'ultima possibile nella partita --> Mostro dialog di pareggio
                // O la mossa del giocatore 1 non era vincente e non era l'ultima possibile nella partita --> Continua la partita

                // Invoco la funzione che determina lo stato della partita e controllo se il giocatore 1 risulta vincente
                if(getMatchStatus() == 1){

                    // Mostro dialog di vittoria del giocatore 1
                    player1win();

                // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
                } else if(getMatchStatus() == 0){

                    // Mostro dialog di pareggio
                    draw();
                }

            // Altrimenti se il turno è dell'avversario allora farò le stesse operazioni ma per il giocatore 2
            } else {

                // Operazione che aggiunge il marker alla casella seleionata
                clickedButton.setBackgroundDrawable(enemy_marker);

                // Aggiungo il numero della casella scelta dal giocatore 2 alla lista delle sue mosse
                player2Cells.add(currentCell);

                // Aggiungo il numero della casella scelta dal giocatore alla lista delle caselle già selezionate nella partita
                alreadyHittedCells.add(currentCell);

                // Cambio turno
                playerTurn = true;

                // Dopo la mossa del giocatore 2 possono succedere tre casi:
                // O la mossa del giocatore 2 era vincente --> Mostro il dialog di vittoria del giocatore 2
                // O la mossa del giocaotre 2 non era vincente ma era l'ultima possibile nella partita --> Mostro dialog di pareggio
                // O la mossa del giocatore 2 non era vincente e non era l'ultima possibile nella partita --> Continua la partita

                // Invoco la funzione che determina lo stato della partita e controllo se il giocatore 1 risulta vincente
                if(getMatchStatus() == 2){

                    // Mostro dialog di vittoria del giocatore 2
                    player2win();

                // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
                } else if(getMatchStatus() == 0){

                    // Mostro dialog di pareggio
                    draw();
                }
            }
        }
    }

    // Funzione che ritorna lo stato del gioco
    // Return 1 -> giocatore vincente
    // Return 2 -> cpu vincente
    // Return 0 -> pareggio
    // Return 3 -> partita da continuare
    private int getMatchStatus(){


        // Controllo se le celle dell'utente corrispondono ad un pattern vincente
        if((player1Cells.contains(1) && player1Cells.contains(2) && player1Cells.contains(3)) ||
                (player1Cells.contains(4) && player1Cells.contains(5) && player1Cells.contains(6)) ||
                (player1Cells.contains(7) && player1Cells.contains(8) && player1Cells.contains(9)) ||
                (player1Cells.contains(1) && player1Cells.contains(4) && player1Cells.contains(7)) ||
                (player1Cells.contains(2) && player1Cells.contains(5) && player1Cells.contains(8)) ||
                (player1Cells.contains(3) && player1Cells.contains(6) && player1Cells.contains(9)) ||
                (player1Cells.contains(1) && player1Cells.contains(5) && player1Cells.contains(9)) ||
                (player1Cells.contains(3) && player1Cells.contains(5) && player1Cells.contains(7))){
            return 1;

        // Controllo se le celle della cpu corrispondono ad un pattern vincente
        } else if ((player2Cells.contains(1) && player2Cells.contains(2) && player2Cells.contains(3)) ||
                (player2Cells.contains(4) && player2Cells.contains(5) && player2Cells.contains(6)) ||
                (player2Cells.contains(7) && player2Cells.contains(8) && player2Cells.contains(9)) ||
                (player2Cells.contains(1) && player2Cells.contains(4) && player2Cells.contains(7)) ||
                (player2Cells.contains(2) && player2Cells.contains(5) && player2Cells.contains(8)) ||
                (player2Cells.contains(3) && player2Cells.contains(6) && player2Cells.contains(9)) ||
                (player2Cells.contains(1) && player2Cells.contains(5) && player2Cells.contains(9)) ||
                (player2Cells.contains(3) && player2Cells.contains(5) && player2Cells.contains(7))) {
            return 2;

        // Nel caso in cui ne il giocatore ne la cpu siano stati decretati vincitori controllo se tutte le celle sono state selezionate e quindi si tratta di pareggio oppure se la partita va avanti
        } else if (alreadyHittedCells.size() == 9) {
            return 0;

        // In questo caso la partita va avanti
        } else return 3;
    }

    // Funzione che crea e visualizza il dialog di vittoria del giocatore 1
    private void player1win(){

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.player1_win_rematch);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setPositiveButton(R.string.ok, (dialog, which) -> reset());
        builder.setNegativeButton(R.string.exit, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Incremento del punteggio del giocaotore 1 di questa serie di partite
        player1Score += 1;

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che crea e visualizza il dialog di pareggio
    private void draw(){

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.draw);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setPositiveButton(R.string.ok, (dialog, which) -> reset());
        builder.setNegativeButton(R.string.exit, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che crea e visualizza il dialog di vittoria del giocatore 2
    private void player2win(){

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.player2_win);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setPositiveButton(R.string.ok, (dialog, which) -> reset());
        builder.setNegativeButton(R.string.exit, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Incremento del punteggio del giocaotore 2 di questa serie di partite
        player2Score += 1;

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che disabilita le caselle di gioco e i due bottoni in basso durante la visualizzazione del dialog di fine partita
    private void disableAnyButton() {
        btnCell1.setClickable(false);
        btnCell2.setClickable(false);
        btnCell3.setClickable(false);
        btnCell4.setClickable(false);
        btnCell5.setClickable(false);
        btnCell6.setClickable(false);
        btnCell7.setClickable(false);
        btnCell8.setClickable(false);
        btnCell9.setClickable(false);
        btnHome.setClickable(false);
        btnReset.setClickable(false);
    }

}