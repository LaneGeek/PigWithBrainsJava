package sekhah.lane.pigwithbrainsjava;

import java.util.Random;

class PigGame {

    private final int WINNING_SCORE = 20;

    private Random rand = new Random();
    private int player1Score = 0;
    private int player2Score = 0;
    private int turnPoints = 0;
    private int currentPlayer = 1;

    int getPlayer1Score() {
        return player1Score;
    }

    void setPlayer1Score(int score) {
        player1Score = score;
    }

    int getPlayer2Score() {
        return player2Score;
    }

    void setPlayer2Score(int score) {
        player2Score = score;
    }

    int getTurnPoints() {
        return turnPoints;
    }

    void setTurnPoints(int points) {
        turnPoints = points;
    }

    int getCurrentPlayer() {
        return currentPlayer;
    }

    void setCurrentPlayer(int player) {
        currentPlayer = player;
    }

    int rollDie() {
        int roll = rand.nextInt(6) + 1;
        if (roll != 1) {
            turnPoints += roll;
        } else {
            turnPoints = 0;
            changeTurn();
        }
        return roll;
    }

    void changeTurn() {
        if (currentPlayer == 1) {
            player1Score += turnPoints;
            currentPlayer = 2;
        } else {
            player2Score += turnPoints;
            currentPlayer = 1;
        }
        turnPoints = 0;
    }

    int checkForWinner() {
        // returns the player number or 0 for a tie and -1 if no winner or tie
        if (player1Score >= WINNING_SCORE || player2Score >= WINNING_SCORE) {
            if (player2Score > player1Score)
                return 2;
            if (player1Score > player2Score && currentPlayer == 1)
                return  1;
            if (player1Score == player2Score)
                return  0;
        }
        return -1;
    }
}
