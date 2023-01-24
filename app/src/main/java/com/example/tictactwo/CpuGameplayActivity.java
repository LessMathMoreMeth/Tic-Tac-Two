package com.example.tictactwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class CpuGameplayActivity extends AppCompatActivity implements View.OnClickListener {

    int playerScore = 0;
    int cpuScore = 0;
    boolean gameOver = false;

    ArrayList<Integer> player1Cells = new ArrayList<>();
    ArrayList<Integer> cpuCells = new ArrayList<>();
    ArrayList<Integer> alreadyHittedCells = new ArrayList<>();
    TextView tvPlayer;
    TextView tvCpu;
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
        setContentView(R.layout.activity_cpu_gameplay);

        // Ottengo il riferimento alle TextView del giocatore e della cpu
        tvPlayer = findViewById(R.id.tvCpuPlayer1);
        tvCpu = findViewById(R.id.tvCpuPlayer2);

        // Ottengo i riferimenti delle celle di gioco
        btnCell1 = findViewById(R.id.btnCpuCell1);
        btnCell2 = findViewById(R.id.btnCpuCell2);
        btnCell3 = findViewById(R.id.btnCpuCell3);
        btnCell4 = findViewById(R.id.btnCpuCell4);
        btnCell5 = findViewById(R.id.btnCpuCell5);
        btnCell6 = findViewById(R.id.btnCpuCell6);
        btnCell7 = findViewById(R.id.btnCpuCell7);
        btnCell8 = findViewById(R.id.btnCpuCell8);
        btnCell9 = findViewById(R.id.btnCpuCell9);

        // Ottengo riferimento dei bottoni in basso nella schermata
        btnHome = findViewById(R.id.btnCpuHome);
        btnReset = findViewById(R.id.btnCpuReset);

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

    // Funzione eseguita in seguito della pressione di uno dei tasti
    @Override
    public void onClick(@NonNull View v) {

        // Variabile che conterrà la mossa a seconda del tasto premuto dal giocatore
        int cellId =0;

        // Variabile che contiene l'id del tasto premuto dall'utente
        int id = v.getId();

        // Controllo quale tasto è stato premuto
        if(id == R.id.btnCpuReset){
            reset();
        } else if(id == R.id.btnCpuHome){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if(id == R.id.btnCpuCell1) {
            cellId = 1;
        } else if(id == R.id.btnCpuCell2) {
            cellId = 2;
        } else if(id == R.id.btnCpuCell3) {
            cellId = 3;
        } else if(id == R.id.btnCpuCell4) {
            cellId = 4;
        } else if(id == R.id.btnCpuCell5) {
            cellId = 5;
        } else if(id == R.id.btnCpuCell6) {
            cellId = 6;
        } else if(id == R.id.btnCpuCell7) {
            cellId = 7;
        } else if(id == R.id.btnCpuCell8) {
            cellId = 8;
        } else if(id == R.id.btnCpuCell9) {
            cellId = 9;
        }

        // Dopo aver determinato quale tasto viene premuto dal giocatore invoco la funzione che effettivamente esegue la mossa
        performMove(v, cellId);
    }

    // Funzione che aggiorna le TextView del giocatore e della cpu
    // Aggiorna dopo ogni partita il counter di vittorie per questa serie di partite
    private void updateTvScore(){
        String myScoreStringPlayer = getString(R.string.player1) + " " + playerScore;
        tvPlayer.setText(myScoreStringPlayer);
        String myScoreStringCpu = getString(R.string.cpu) + " " + cpuScore;
        tvCpu.setText(myScoreStringCpu);
    }

    // Funzione che resetta il gioco per iniziare una nuova partita
    private void reset() {

        // Svuoto la lista delle caselle premute dal giocatore
        player1Cells.clear();

        // Svuoto la lista delle caselle premute dalla cpu
        cpuCells.clear();

        // Svuoto la lista delle caselle premute da entrambi
        alreadyHittedCells.clear();

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

    // Funzione che esegue mosse del giocatore e della cpu e controlla dopo ognuna di essa lo stato del gioco
    private void performMove(View v, int currentCell) {

        // Casto la view generica che mi viene passata in un bottone che è quello che mi serve
        AppCompatButton clickedButton = (AppCompatButton) v;

        //Controllo che sia una mossa di gioco e non di uno dei due bottoni in basso
        if(clickedButton != btnHome && clickedButton != btnReset){

            // Operazione che aggiunge il marker alla casella seleionata
            clickedButton.setBackgroundDrawable(player_marker);

            // Aggiungo il numero della casella scelta dal giocatore alla lista delle sue mosse
            player1Cells.add(currentCell);

            // Aggiungo il numero della casella scelta dal giocatore alla lista delle caselle già selezionate nella partita
            alreadyHittedCells.add(currentCell);

            // Dopo la mossa del giocatore possono succedere tre casi:
            // O la mossa del giocatore era vincente --> Mostro il dialog di vittoria
            // O la mossa del giocaotre non era vincente ma era l'ultima possibile nella partita --> Mostro dialog di pareggio
            // O la mossa del giocatore non era vincente e non era l'ultima possibile nella partita --> Continua la partita

            // Invoco la funzione che determina lo stato della partita e controllo se il giocatore risulta vincente
            if(getMatchStatus() == 1){

                // Mostro dialog di vittoria del giocatore
                player1win();

            // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
            } else if(getMatchStatus() == 0){

                // Mostro dialog di pareggio
                draw();

            // Altrimenti la partita continua
            } else {
                gameOver = false;
            }

            // Se la partita non è finita facciamo eseguire una mossa alla cpu e controlliamo di nuovo lo stato della partita
            if(!gameOver){

                // Funzione che esegue una mossa alla cpu
                cpuMove();

                // Dopo la mossa della cpu possono succedere tre casi:
                // O la mossa della cpu era vincente --> Mostro il dialog di sconfitta
                // O la mossa della cpu non era vincente ma era l'ultima possibile nella partita --> Mostro dialog di pareggio
                // O la mossa della cpu non era vincente e non era l'ultima possibile nella partita --> Continua la partita

                // Invoco la funzione che determina lo stato della partita e controllo se la cpu risulta vincente
                if(getMatchStatus() == 2){

                    // Mostro dialog di sconfitta
                    cpuWin();

                // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
                } else if(getMatchStatus() == 0){

                    // Mostro dialog di pareggio
                    draw();
                }
            }
        }
    }

    // Funzione che disabilita le caselle di gioco e i due bottoni in basso durante la visualizzazione del dialog di fine partita
    private void disableAnyButton(){
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

    // Funzione che crea e visualizza il dialog di vittoria del giocatore
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

        // Incremento del punteggio del giocaotore di questa serie di partite
        playerScore += 1;

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che crea e visualizza il dialog di vittoria della cpu
    private void cpuWin(){

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.cpu_win_rematch);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setPositiveButton(R.string.ok, (dialog, which) -> reset());
        builder.setNegativeButton(R.string.exit, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Incremento del punteggio dlla cpu di questa serie di partite
        cpuScore += 1;

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

    // Funzione che randomicamente sceglie quale cella premere come mossa della cpu
    private void cpuMove() {

        // Un modo per ottenere un numero random tra 1 e 9 compresi
        int min = 1;
        int max = 9;
        int random = generateRandom(min, max);

        // Controllo che la cella scelta per la mossa non sia già stata premuta in questa partita
        // In caso positivo riprovo generando un nuovo numero di cella
        if(alreadyHittedCells.contains(random)){

            cpuMove();

            // In caso contrario procedo a visualizzare la mossa della cpu a schermo
            // A seconda del numero generato random seleziono la relativa casella
        } else {
            switch (random){
                // Cella 1
                case 1:
                    btnCell1.setBackgroundDrawable(enemy_marker);
                    break;
                // Cella 2

                case 2:
                    btnCell2.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 3
                case 3:
                    btnCell3.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 4
                case 4:
                    btnCell4.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 5
                case 5:
                    btnCell5.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 6
                case 6:
                    btnCell6.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 7
                case 7:
                    btnCell7.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 8
                case 8:
                    btnCell8.setBackgroundDrawable(enemy_marker);
                    break;

                // Cella 9
                case 9:
                    btnCell9.setBackgroundDrawable(enemy_marker);
                    break;

            }

            // Una volta eseguita la mossa aggiungo il numero della cella alla lista delle celle della cpu
            cpuCells.add(random);

            // Aggiungo anche il numero della cella alla lista delle celle gia selezionate durante la partita
            alreadyHittedCells.add(random);

        }
    }

    // Funzione che genera un numero random tra 1 e 9 compresi
    public static int generateRandom(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }

    // Funzione che ritorna lo stato del gioco
    // Return 1 -> giocatore vincente
    // Return 2 -> cpu vincente
    // Return 0 -> pareggio
    // Return 3 -> partita da continuare
    private int getMatchStatus() {

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
        } else if ((cpuCells.contains(1) && cpuCells.contains(2) && cpuCells.contains(3)) ||
                (cpuCells.contains(4) && cpuCells.contains(5) && cpuCells.contains(6)) ||
                (cpuCells.contains(7) && cpuCells.contains(8) && cpuCells.contains(9)) ||
                (cpuCells.contains(1) && cpuCells.contains(4) && cpuCells.contains(7)) ||
                (cpuCells.contains(2) && cpuCells.contains(5) && cpuCells.contains(8)) ||
                (cpuCells.contains(3) && cpuCells.contains(6) && cpuCells.contains(9)) ||
                (cpuCells.contains(1) && cpuCells.contains(5) && cpuCells.contains(9)) ||
                (cpuCells.contains(3) && cpuCells.contains(5) && cpuCells.contains(7))) {
            return 2;

        // Nel caso in cui ne il giocatore ne la cpu siano stati decretati vincitori controllo se tutte le celle sono state selezionate e quindi si tratta di pareggio oppure se la partita va avanti
        } else if (alreadyHittedCells.size() == 9) {
            return 0;

        // In questo caso la partita va avanti
        } else return 3;
    }
}
