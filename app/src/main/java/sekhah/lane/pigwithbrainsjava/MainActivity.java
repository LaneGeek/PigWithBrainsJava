package sekhah.lane.pigwithbrainsjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText player1NameEditText;
    private EditText player2NameEditText;
    private TextView player1ScoreTextView;
    private TextView player2ScoreTextView;
    private TextView nextTurnTextView;
    private TextView turnPointsTextView;
    private TextView aiModeTextView;
    private TextView aiMovesTextView;
    private TextView aiScoreTextView;
    private ImageView dieImageView;
    private Button rollDieButton;
    private Button turnButton;

    private PigGame pigGame = new PigGame();
    private int die = 0;

    // Variables for user settings
    private SharedPreferences prefs;
    private boolean aiMode = false;
    private int aiMoves = 2;
    private int aiScore = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1NameEditText = findViewById(R.id.player1NameEditText);
        player2NameEditText = findViewById(R.id.player2NameEditText);
        player1ScoreTextView = findViewById(R.id.player1ScoreTextView);
        player2ScoreTextView = findViewById(R.id.player2ScoreTextView);
        nextTurnTextView = findViewById(R.id.nextTurnTextView);
        turnPointsTextView = findViewById(R.id.turnPointsTextView);
        aiModeTextView = findViewById(R.id.aiModeTextView);
        aiMovesTextView = findViewById(R.id.aiMovesTextView);
        aiScoreTextView = findViewById(R.id.aiScoreTextView);
        dieImageView = findViewById(R.id.dieImageView);
        rollDieButton = findViewById(R.id.rollDieButton);
        turnButton = findViewById(R.id.turnButton);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        updateScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_about:
                Toast.makeText(this, "Pig Game", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        // Save the current game state
        Editor editor = prefs.edit();
        editor.putInt("player1Score", pigGame.getPlayer1Score());
        editor.putInt("player2Score", pigGame.getPlayer2Score());
        editor.putInt("turnPoints", pigGame.getTurnPoints());
        editor.putInt("currentPlayer", pigGame.getCurrentPlayer());
        editor.putInt("die", die);
        editor.putBoolean("rollDieButtonEnabled", rollDieButton.isEnabled());
        editor.putBoolean("turnButtonEnabled", turnButton.isEnabled());
        editor.putString("turnButtonText", turnButton.getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        pigGame.setPlayer1Score(prefs.getInt("player1Score", 0));
        pigGame.setPlayer2Score(prefs.getInt("player2Score", 0));
        pigGame.setTurnPoints(prefs.getInt("turnPoints", 0));
        pigGame.setCurrentPlayer(prefs.getInt("currentPlayer", 0));
        die = prefs.getInt("die", 0);
        rollDieButton.setEnabled(prefs.getBoolean("rollDieButtonEnabled", false));
        turnButton.setEnabled(prefs.getBoolean("turnButtonEnabled", true));
        turnButton.setText(prefs.getString("turnButtonText", "Start Turn"));

        aiMode = prefs.getBoolean("ai_mode", false);
        aiMoves = Integer.parseInt(prefs.getString("number_of_moves", "2"));
        aiScore = Integer.parseInt(prefs.getString("score_limit", "10"));

        updateScreen();
    }

    public void rollDieClick(View view) {
        die = pigGame.rollDie();
        if (die == 1) {
            rollDieButton.setEnabled(false);
            turnButton.setText("End Turn");
            endOfTurn();
        }
        updateScreen();
    }

    public void turnClick(View view) {
        if (turnButton.getText().toString().equals("Start Turn")) {
            turnButton.setText("End Turn");
            rollDieButton.setEnabled(true);
        } else {
            turnButton.setText("Start Turn");
            rollDieButton.setEnabled(false);
            endOfTurn();
            die = 0;
        }
        updateScreen();
    }

    public void newGameClick(View view) {
        pigGame = new PigGame();
        die = 0;
        rollDieButton.setEnabled(false);
        turnButton.setText("Start Turn");
        turnButton.setEnabled(true);
        updateScreen();
    }

    private void endOfTurn() {
        pigGame.changeTurn();
        if (pigGame.getCurrentPlayer() == 2 && aiMode) {
            computerTurn();
            Toast.makeText(this, "Computer's Turn", Toast.LENGTH_SHORT).show();
        }
    }

    private void computerTurn() {
        for (int i = 0; i < aiMoves; i++)
        {
            die = pigGame.rollDie();
            if (die == 1 || pigGame.getTurnPoints() > aiScore) {
                rollDieButton.setEnabled(false);
                turnButton.setText("End Turn");
                pigGame.changeTurn();
            }
            updateScreen();
        }
    }

    private void updateScreen() {
        if (pigGame.getCurrentPlayer() == 1)
            nextTurnTextView.setText(player1NameEditText.getText() + "'s Turn");
        else
            nextTurnTextView.setText(player2NameEditText.getText() + "'s Turn");

        player1ScoreTextView.setText(Integer.toString(pigGame.getPlayer1Score()));
        player2ScoreTextView.setText(Integer.toString(pigGame.getPlayer2Score()));
        turnPointsTextView.setText(Integer.toString(pigGame.getTurnPoints()));

        if (aiMode) {
            aiModeTextView.setText("AI Mode ON");
            aiMovesTextView.setText("Move Limit: " + aiMoves);
            aiScoreTextView.setText("Score Limit: " + aiScore);
            player2NameEditText.setText("Computer");
        } else {
            aiModeTextView.setText("");
            aiMovesTextView.setText("");
            aiScoreTextView.setText("");
            player2NameEditText.setText("Player 2");
        }

        // Check for winner
        if (pigGame.checkForWinner() != -1) {
            switch (pigGame.checkForWinner()) {
                case 0:
                    nextTurnTextView.setText("It is a tie!");
                    break;
                case 1:
                    nextTurnTextView.setText(player1NameEditText.getText() + " Wins!");
                    break;
                case 2:
                    nextTurnTextView.setText(player2NameEditText.getText() + " Wins!");
                    break;
            }
            rollDieButton.setEnabled(false);
            turnButton.setEnabled(false);
        }

        // Update the die image
        switch (die) {
            case 0:
                dieImageView.setImageResource(R.drawable.pig);
                break;
            case 1:
                dieImageView.setImageResource(R.drawable.die1);
                break;
            case 2:
                dieImageView.setImageResource(R.drawable.die2);
                break;
            case 3:
                dieImageView.setImageResource(R.drawable.die3);
                break;
            case 4:
                dieImageView.setImageResource(R.drawable.die4);
                break;
            case 5:
                dieImageView.setImageResource(R.drawable.die5);
                break;
            case 6:
                dieImageView.setImageResource(R.drawable.die6);
                break;
        }
    }
}
