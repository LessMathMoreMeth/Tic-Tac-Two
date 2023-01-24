package com.example.tictactwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HumanOnlineGameplayActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton btnCell1;
    AppCompatButton btnCell2;
    AppCompatButton btnCell3;
    AppCompatButton btnCell4;
    AppCompatButton btnCell5;
    AppCompatButton btnCell6;
    AppCompatButton btnCell7;
    AppCompatButton btnCell8;
    AppCompatButton btnCell9;
    TextView tvPlayer1;
    TextView tvPlayer2;
    TextView tvturns;
    TextView tvTurnsHeader;
    TextView tvTimer;
    Drawable enemy_marker;
    Drawable player_marker;

    // Lista delle celle selezionate dal giocatore (me)
    ArrayList<Integer> player1Cells = new ArrayList<>();

    // Lista delle celle selezionate dal mio avversario
    ArrayList<Integer> player2Cells = new ArrayList<>();

    // Lista che contiene tutte le celle già selezionate
    ArrayList<Integer> alreadyHittedCells = new ArrayList<>();

    // Stringa che conterrà l'id del giocatore
    String playerUniqueId = "0";

    // Firebase reference
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tictactwo-618e8-default-rtdb.firebaseio.com/");

    // Variabile che tiene traccia se è stato trovato un avversario o meno
    boolean opponentFound = false;

    // Variabile che conterrà l'id del nostro avversario una volta trovato
    String opponentUniqueId = "0";

    // Questa variabile può assumere due valori possibili "matching" e "waiting"
    // Quando il giocatore crea una nuova connessione allora il valore diventa "waiting"
    String status;

    // Variabile che tiene traccia di quale giocatore è il turno attualmente, questa stringa conterrà l'id di uno dei due giocatori
    String playerTurn = "";

    // Variabile che contiene l'id della connessione che il giocaotore ha joinato per la partita
    String connectionId = "";

    // EventListener del database per i turni
    ValueEventListener opponentEventListener;

    // Variabile che contiene il nome del mio avversario
    String opponentName = "";

    // Variabile che contiene il
    String myName = "";

    // Costante che definisce la durata di un turno
    long TURN_DURATION = 30000;

    // Variabile countdown
    CountDownTimer countDownTimer;

    // Variabile che indica il tempo rimasto
    Long timeLeft = TURN_DURATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_online_gameplay);

        // Ottengo i riferimenti delle celle di gioco
        btnCell1 = findViewById(R.id.btnOnlineHumanCell1);
        btnCell2 = findViewById(R.id.btnOnlineHumanCell2);
        btnCell3 = findViewById(R.id.btnOnlineHumanCell3);
        btnCell4 = findViewById(R.id.btnOnlineHumanCell4);
        btnCell5 = findViewById(R.id.btnOnlineHumanCell5);
        btnCell6 = findViewById(R.id.btnOnlineHumanCell6);
        btnCell7 = findViewById(R.id.btnOnlineHumanCell7);
        btnCell8 = findViewById(R.id.btnOnlineHumanCell8);
        btnCell9 = findViewById(R.id.btnOnlineHumanCell9);

        // Ottengo il riferimento alle TextView del giocatore e della cpu
        tvPlayer1 = findViewById(R.id.tvOnlineHumanPlayer1);
        tvPlayer2 = findViewById(R.id.tvOnlineHumanPlayer2);

        // Ottengo il riferimento alla "barra" dei turni
        tvturns = findViewById(R.id.tvTurns);
        tvTurnsHeader = findViewById(R.id.tvTurnsHeader);

        // Ottengo il riferimento al TextView del timer
        tvTimer = findViewById(R.id.tvTimer);

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

        // Prendo il riferimento ai due Drawable che rappresentano la "X" e lo "0" del gioco
        enemy_marker = ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.circle_rounded_85, null);
        player_marker = ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.cross_rounded_85, null);


        // Valore di default della variabile status, quando avrò creato una nuova connessione diventerà "waiting"
        // Ho spezzato definizione e assegnamento perchè mi dava dei problemi "android.content.res.Resources android.content.Context.getResources()' on a null object reference"
        // Penso che sia perchè getString ha bisogno del contesto per essere chiamata e il contesto lo ottengo solamente dopo che la OnCreate è stata eseguita
        status = getString(R.string.matching);

        // Ottengo il nome del giocatore dall'intent che ha aperto questa activity
        String getPlayerName = getIntent().getStringExtra(getString(R.string.player_name));

        // Setto il testo della TV del player con il nome appena ottenuto
        tvPlayer1.setText(getPlayerName);

        // Mostro questo dialog mentre sono in attesa di un avversario
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getApplicationContext().getString(R.string.progress_dialog_message));
        progressDialog.show();

        // Genero l'identificativo del player
        playerUniqueId = String.valueOf(System.currentTimeMillis());

        databaseReference.child(getString(R.string.connections)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Controllo se ho già trovato un avversario o meno
                if(!opponentFound){

                    // Non ho ancora trovato un avversario, provo a vedere se esiste già una connessione nel db non completa da poter joinare
                    if(snapshot.hasChildren()){


                        // Controllo tutte le connessioni alla ricerca di altri utenti in attesa, cosi da joinare la loro connessione
                        for(DataSnapshot connections : snapshot.getChildren()) {

                            // Per ogni connessione ottengo l'id, cosi facendo dopo aver verificato se tale connessione non è completa allora mi posso unire
                            String conId = connections.getKey();

                            // Controllo per ogni connessione quanti player "si sono collegati"
                            // getPlayersCount == 1 allora tale connessione non è ancora stata completata e posso quindi joinarla
                            // getPlayersCount == 2 allora tale connessione è già completa e quindi non la prenderà in considerazione
                            int getPlayersCount = (int) connections.getChildrenCount();

                            // Parte di codice che viene eseguita se ho creato la stanza e quindi sono in attesa di un giocatore
                            if (status.equals(getString(R.string.waiting))) {

                                // Se getPlayerCount == 2 allora qualcuno ha joinato la nostra connessione
                                if (getPlayersCount == 2) {

                                    // Il primo turno di gioco è sempre di chi ha creato la stanza
                                    // Se sono entrato qui dentro era perchè ero in attesa quindi vuol dire che ho creato io la stanza e quindi mi assegno il primo turno della partita
                                    playerTurn = playerUniqueId;

                                    // Variabile che diventa true quando abbiamo due giocatori nella connessione
                                    boolean playerFound = false;

                                    // Ciclo per ottenere i dettagli dei giocatori nella connessione
                                    for (DataSnapshot players : connections.getChildren()) {

                                        // Ottengo gli id dei giocatori all'interno della connessione
                                        String getPlayerUniqueId = players.getKey();

                                            // Controllo se l'id del giocatore matcha l'id di chi ha creato la connessione
                                            if (getPlayerUniqueId.equals(playerUniqueId)) {

                                                // Se questo è vero ed getPlayersCount == 2 allora vuol dire che un avversario ha joinato la nostra connessione
                                                playerFound = true;

                                            }
                                            // Se invece l'id non fa match allora vuol dire che mi sto riferendo al mio avversario nella connessione e ottengo i suoi dati
                                            else if (playerFound) {

                                                Log.d("STRAMA", "ARRIVATO DA CREAZIONE");
                                                // Ottengo il nome dell'avversario
                                                String getOpponentName = players.child(getString(R.string.player_name)).getValue(String.class);

                                                // Ottengo l'id dell'avversario
                                                opponentUniqueId = players.getKey();

                                                // Dopo aver ottenuto il nome del mio avversario setto la TextView
                                                tvPlayer2.setText(getOpponentName);

                                                // Assegno l'id della connessione (NON SONO SICURO CI SIA BISOGNO DI QUESTA COSA DEVO CAPIRE SE POSSO USARE DIRETTAMENTE conId)
                                                connectionId = conId;

                                                // La connessione è quindi completa e ho trovato un avversario
                                                opponentFound = true;

                                                // Aggiungo un valore che non potrà essere valido in modo da popolare quel particolare path, cosi facendo posso aggiungere un listener per quel path
                                                databaseReference.child(getString(R.string.connections)).child(connectionId).child(opponentUniqueId).child(getString(R.string.moves)).getRef().setValue(0);

                                                // Aggiungo il listener che reagisce tutte le volte che una nuova mossa viene fatto dal nostro avversario
                                                databaseReference.child(getString(R.string.connections)).child(connectionId).child(opponentUniqueId).child(getString(R.string.moves)).addValueEventListener(opponentEventListener);

                                                // Se sono arrivato qui allora la partita è pronta per incominciare quindi se il progressdialog è ancora mostrato lo nascondo
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }

                                                // Cosi posso avere un riferimento al di fuori della funzione del mio nome
                                                myName = getPlayerName;

                                                // Cosi posso avere un riferimento al di fuori della funzione del nome del mio avversario
                                                opponentName = getOpponentName;

                                                // Setto la TextView dei turni con il nome del giocatore che ha creato la stanza e che quindi avrà il primo turno
                                                tvturns.setText(getPlayerName);

                                                // Setto il background color con il colore che rappresenta quel giocatore
                                                tvturns.setBackgroundColor(Color.GREEN);

                                                // Setto il background color dell header della TextView dei turni
                                                tvTurnsHeader.setBackgroundColor(Color.GREEN);

                                                // La connessione è stata effettuata quindi tolgo il relativo listener dal database
                                                databaseReference.child(getString(R.string.connections)).removeEventListener(this);

                                                // Inizliazzo il timer per la mossa
                                                countDownTimer = new CountDownTimer(timeLeft, 1000) {

                                                    // FUnzione che viene eseguita ogni 1000 millisecondi
                                                    @Override
                                                    public void onTick(long millisUntilFinished) {

                                                        // Millisecondi mancanti
                                                        timeLeft = millisUntilFinished;

                                                        // Invoco la funzione che li trasforma in secondi e aggiorna la TextView
                                                        updateCounterTextView();
                                                    }

                                                    // FUnzione che viene eseguita al termine del countdown
                                                    @Override
                                                    public void onFinish() {

                                                        // Cambio il turno
                                                        changeTurn(playerTurn);

                                                        // Resetto la durata del timer
                                                        timeLeft = TURN_DURATION;

                                                        // Aggiorno la TextView
                                                        updateCounterTextView();

                                                        // Controllo se la partita non è già finita
                                                        if(getMatchStatus() == 0){

                                                            // Se la partita non è finita allora rifaccio partire il timer
                                                            countDownTimer.start();
                                                        }
                                                    }
                                                }.start();
                                            }

                                    }
                                }
                            }
                            // Entriamo in questo ramo se non siamo stati noi a creare la connessione ma stiamo joinando una già esistente
                            else {

                                // Controllo per ogni connessione se ne esiste una che ha un solo player che quindi necessita di un altro giocatore per essere completata
                                // In caso positivo ho trovato una partita da joinare e la joino
                                if (getPlayersCount == 1) {

                                    // Con questa linea di codice aggiungo "me stesso" (inteso come giocatore) alla connessione scelta
                                    connections.child(playerUniqueId).child(getString(R.string.player_name)).getRef().setValue(getPlayerName);

                                    // Adesso ottengo i dati del mio avversario
                                    for (DataSnapshot players : connections.getChildren()) {

                                        // Ottengo il nome dell' avversario
                                        String getOpponentName = players.child(getString(R.string.player_name)).getValue(String.class);

                                        // Ottengo l'id dell'avversario
                                        opponentUniqueId = players.getKey();

                                        // Assegno il primo turno della partita al giocatore che ha creato la stanza, in questo caso il mio avversario
                                        playerTurn = opponentUniqueId;

                                        // Utilizzo il nome dell'avversario appena ottenuto per riempire la TextView dell'avversario
                                        tvPlayer2.setText(getOpponentName);

                                        // Adesso che ho joinato una connessione mi salvo il suo id
                                        connectionId = conId;

                                        // Ho trovato un avversario allora setto a true la variabile, cosi facendo non eseguo di nuovo la parte di codice che crea/joina una connessione
                                        opponentFound = true;

                                        // Aggiungo un valore che non potrà essere valido in modo da popolare quel particolare path, cosi facendo posso aggiungere un listener per quel path
                                        databaseReference.child(getString(R.string.connections)).child(connectionId).child(opponentUniqueId).child(getString(R.string.moves)).getRef().setValue(0);

                                        // Aggiungo il listener che reagisce tutte le volte che una nuova mossa viene fatto dal nostro avversario
                                        databaseReference.child(getString(R.string.connections)).child(connectionId).child(opponentUniqueId).child(getString(R.string.moves)).addValueEventListener(opponentEventListener);

                                        Log.d("STRAMA", "ARRIVATO DA JOIN");

                                        // Se sono arrivato qui allora la partita è pronta per incominciare quindi se il progressdialog è ancora mostrato lo nascondo
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }

                                        // Cosi posso avere un riferimento al di fuori della funzione del mio nome
                                        myName = getPlayerName;

                                        // Cosi posso avere un riferimento al di fuori della funzione del nome del mio avversario
                                        opponentName = getOpponentName;

                                        // Setto la TextView dei turni con il nome del giocatore che ha creato la stanza e che quindi avrà il primo turno
                                        tvturns.setText(getOpponentName);

                                        // Setto il background color con il colore che rappresenta quel giocatore
                                        tvturns.setBackgroundColor(Color.RED);

                                        // Setto il background color dell header della TextView dei turni
                                        tvTurnsHeader.setBackgroundColor(Color.RED);

                                        // La connessione è stata effettuata quindi tolgo il relativo listener dal database
                                        databaseReference.child(getString(R.string.connections)).removeEventListener(this);

                                        // Inizliazzo il timer per la mossa
                                        countDownTimer = new CountDownTimer(timeLeft, 1000) {

                                            // FUnzione che viene eseguita ogni 1000 millisecondi
                                            @Override
                                            public void onTick(long millisUntilFinished) {

                                                // Millisecondi mancanti
                                                timeLeft = millisUntilFinished;

                                                // Invoco la funzione che li trasforma in secondi e aggiorna la TextView
                                                updateCounterTextView();
                                            }

                                            // FUnzione che viene eseguita al termine del countdown
                                            @Override
                                            public void onFinish() {

                                                // Cambio il turno
                                                changeTurn(playerTurn);

                                                // Resetto la durata del timer
                                                timeLeft = TURN_DURATION;

                                                // Aggiorno la TextView
                                                updateCounterTextView();

                                                // Controllo se la partita non è già finita
                                                if(getMatchStatus() == 0){

                                                    // Se la partita non è finita allora rifaccio partire il timer
                                                    countDownTimer.start();
                                                }
                                            }
                                        }.start();

                                        break;
                                    }
                                }
                            }
                        }
                        // Controllo se non ho trovato un avversario ma il giocaotre non è piu in attesa, in questo caso creerà una nuova connessione
                        if(!opponentFound && !status.equals(getString(R.string.waiting))){

                            // Genero un id per la connessione appena creata
                            String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                            // Creo una nuova connessione nel database
                            // Questa connessione avrà un figlio con l'id della connesione
                            // L'id della connessione avrà a sua volta un figlio con l'id del giocatore che ha creato la stanza
                            // Che a sua volta avrà un figlio di nome playerName che ha come valore il nome del giocatore
                            snapshot.child(connectionUniqueId).child(playerUniqueId).child(getString(R.string.player_name)).getRef().setValue(getPlayerName);

                            // Adesso mi metto in attesa che qualcuno joini la mia connessione
                            status = getString(R.string.waiting);
                        }
                    }
                    // Se non ci sono connessioni all'interno del database allora ne creerò una e attenderò che qualcuno la joini
                    else {

                        // Genero un id per la connessione appena creata
                        String connectionUniqueId = String.valueOf(System.currentTimeMillis());

                        // Creo una nuova connessione nel database
                        // Questa connessione avrà un figlio con l'id della connesione
                        // L'id della connessione avrà a sua volta un figlio con l'id del giocatore che ha creato la stanza
                        // Che a sua volta avrà un figlio di nome playerName che ha come valore il nome del giocatore
                        snapshot.child(connectionUniqueId).child(playerUniqueId).child(getString(R.string.player_name)).getRef().setValue(getPlayerName);

                        // Adesso mi metto in attesa che qualcuno joini la mia connessione
                        status = getString(R.string.waiting);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Istanziazione del listener delle mosse
        opponentEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Controllo che lo snapshot esiste per sicurezza
                if(snapshot.exists()){

                    // Mi salvo in una variabile il valore trovato nel path delle mosse del mio avversario
                    int value = snapshot.getValue(int.class);

                    // Utilizzo il valore ottenuto per visualizzare in locale la mossa del mio avversario
                    performOpponentMove(value);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

    }

    // Funzione che preso il numero della cella cliccata dal mio avversario me la mostra in lcoale
    private void performOpponentMove(int cell) {

        // Definisco il bottone che diventerà il bottone premuto dal giocatore avversario
        AppCompatButton hittedCell;

        // Controllo che il valore sia compreso tra 1 e 9 perchè in inizializzazione dovevo per forza inserire un valore e ho optato per 0
        if(cell <= 9 && cell >=1){
            if(cell == 1){
                hittedCell = btnCell1;
            } else if(cell == 2){
                hittedCell = btnCell2;
            } else if(cell == 3){
                hittedCell = btnCell3;
            } else if(cell == 4){
                hittedCell = btnCell4;
            } else if(cell == 5){
                hittedCell = btnCell5;
            } else if(cell == 6){
                hittedCell = btnCell6;
            } else if(cell == 7){
                hittedCell = btnCell7;
            } else if(cell == 8){
                hittedCell = btnCell8;
            } else {
                hittedCell = btnCell9;
            }

            // Operazione che aggiunge il marker alla casella seleionata
            hittedCell.setBackgroundDrawable(enemy_marker);

            // Aggiungo il numero della casella scelta dall'avversario alla lista delle sue mosse
            player2Cells.add(cell);

            // Aggiungo il numero della casella scelta dall'avversario alla lista delle caselle già selezionate nella partita
            alreadyHittedCells.add(cell);

            // Invoco la funzione che determina lo stato della partita e controllo se il giocatore 1 risulta vincente
            if(getMatchStatus() == 2){

                // Mostro dialog di sconfitta
                opponentWin();

                // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
            } else if(getMatchStatus() == 0){

                // Mostro dialog di pareggio
                draw();
            }

            // Cambio il turno
            changeTurn(playerTurn);
        }
    }

    // Funzione eseguita in seguito della pressione di uno dei tasti
    @Override
    public void onClick(View v) {

        // Variabile che conterrà la mossa a seconda del tasto premuto dal giocatore
        int cellId = 0;

        // Variabile che contiene l'id del tasto premuto dall'utente
        int id = v.getId();

        // Controllo quale tasto è stato premuto
        if(id == R.id.btnOnlineHumanCell1) {
            cellId = 1;
        } else if(id == R.id.btnOnlineHumanCell2) {
            cellId = 2;
        } else if(id == R.id.btnOnlineHumanCell3) {
            cellId = 3;
        } else if(id == R.id.btnOnlineHumanCell4) {
            cellId = 4;
        } else if(id == R.id.btnOnlineHumanCell5) {
            cellId = 5;
        } else if(id == R.id.btnOnlineHumanCell6) {
            cellId = 6;
        } else if(id == R.id.btnOnlineHumanCell7) {
            cellId = 7;
        } else if(id == R.id.btnOnlineHumanCell8) {
            cellId = 8;
        } else if(id == R.id.btnOnlineHumanCell9) {
            cellId = 9;
        }

        // Dopo aver determinato quale tasto viene premuto dal giocatore invoco la funzione che effettivamente esegue la mossa
        performPlayerMove(v, cellId);
    }

    private void performPlayerMove(View v, int currentCell) {

            // Controllo che sia il turno del gicoatore per evitare che esso prema 2 celle prima che l'avversario possa premerne una
            if(playerTurn.equals(playerUniqueId)){

                // Casto la view generica che mi viene passata in un bottone che è quello che mi serve
                AppCompatButton clickedButton = (AppCompatButton) v;

                // Operazione che aggiunge il marker alla casella seleionata
                clickedButton.setBackgroundDrawable(player_marker);

                // Aggiungo il numero della casella scelta dal giocatore 1 alla lista delle sue mosse
                player1Cells.add(currentCell);

                // Aggiungo il numero della casella scelta dal giocatore alla lista delle caselle già selezionate nella partita
                alreadyHittedCells.add(currentCell);

                // Aggiungo la mossa al database
                databaseReference.child(getString(R.string.connections)).child(connectionId).child(playerUniqueId).child(getString(R.string.moves)).setValue(currentCell);

                // Invoco la funzione che determina lo stato della partita e controllo se il giocatore 1 risulta vincente
                if(getMatchStatus() == 1){

                    // Mostro dialog di vittoria del giocatore 1
                    player1win();

                // Invoco la funzione che determina lo stato della partita e controllo se ci troviamo in parità senza ulteriori mosse disponibili
                } else if(getMatchStatus() == 0){

                    // Mostro dialog di pareggio
                    draw();
                }

                // Cambio il turno
                changeTurn(playerTurn);
            }

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
        builder.setNegativeButton(R.string.exit, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che crea e visualizza il dialog di vittoria del giocatore 1
    private void player1win() {

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.you_win);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setNegativeButton(R.string.home, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che crea e visualizza il dialog di vittoria del nostro avversario
    private void opponentWin() {

        // Creazione dell'handler che gestisce la visualizzazione dopo qualche secondo
        Handler handler = new Handler();

        // Creazione del dialog da mostrare
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inizializzazione del dialog
        builder.setTitle(R.string.game_over);
        builder.setMessage(R.string.opponent_win);
        builder.setCancelable(false);

        // Definizione e comportamento tasti di risposta del dialog
        builder.setNegativeButton(R.string.home, (dialog, which) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        // Disabilito tutti i tasti in modo che l'utente sia obbligato a fare una scelta tra le due proposte nel dialog
        disableAnyButton();

        // Dopo un secondo mostro il dialog
        handler.postDelayed(builder::show, 1000);
    }

    // Funzione che viene invocata durante la visualizzazione del messaggio di fine partita per fare in modo che non possano essere premuti tasti durante la visualizzazione
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

    // Funzione che viene invocata quando devo cambiare il turno
    private void changeTurn(String currentTurn){

        // Controllo se il turno è al momento del nostro avversario il prossima sarà nostro
        if(currentTurn.equals(opponentUniqueId)){

            // Modifico la variabile del turno
            playerTurn = playerUniqueId;

            // Modifico la "barra" in basso del turno
            tvturns.setText(myName);
            tvturns.setBackgroundColor(Color.GREEN);
            tvTurnsHeader.setBackgroundColor(Color.GREEN);

        // Altrimenti era il mio turno e diveta quello dell avversario
        } else {

            // Modifico la variabile del turno
            playerTurn = opponentUniqueId;

            // Modifico la "barra" in basso del turno
            tvturns.setText(opponentName);
            tvturns.setBackgroundColor(Color.RED);
            tvTurnsHeader.setBackgroundColor(Color.RED);
        }
    }


    // Funzione che viene usata per aggiornare il timer
    private void updateCounterTextView(){

        // Funzione che calcola i seconodi dati i millisecondi
        int seconds = (int) (timeLeft/1000) % 60;

        // Aggiornamento della TextView
        tvTimer.setText(String.valueOf(seconds));
    }
}