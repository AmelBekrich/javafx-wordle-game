package com.wordle.wordlegame;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class App extends Application {

    private Label[][] board = new Label[6][5];
    private int currentCol = 0;
    private int currentRow = 0;
    private int highScore = 0;
    private String secretWord;
    private Label invalidWordLabel = new Label("");
    private Label highScoreLabel = new Label("High Score");


    private void checkWord(String guess) {
        char[] guessCharacters = guess.toCharArray();
        char[] hiddenCharacters = secretWord.toCharArray();
        int[] letterCount = new int[26];

        for (char c: hiddenCharacters) {
            letterCount[c - 'A']++;
        }

        for (int i = 0; i < 5; i++) {
            if (guessCharacters[i] == hiddenCharacters[i]) {
                board[currentRow][i].getStyleClass().add("green");
                letterCount[guessCharacters[i] - 'A']--;
                guessCharacters[i] = '-';
            }
        }

        for (int i = 0; i < 5; i++) {
            if (guessCharacters[i] == '-') continue;
            if (letterCount[guessCharacters[i] - 'A'] > 0) {
                board[currentRow][i].getStyleClass().add("gold");
                letterCount[guessCharacters[i] - 'A']--;
            } else {
                board[currentRow][i].getStyleClass().add("gray");
            }
        }

        if (guess.equals(secretWord)) {
            highScore++;
            highScoreLabel.setText("High Score: " +highScore);
        }
    }


    @Override
    public void start(Stage stage) throws IOException {

        WordLoader wordLoader = new WordLoader();
        secretWord = wordLoader.getSecretWord();
//        System.out.println("Secret word is: " +secretWord);

        Label titleLabel = new Label("WORDLE");
        titleLabel.getStyleClass().add("title");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(12);
        gridPane.setVgap(12);
        gridPane.add(invalidWordLabel, 0, 6, 5, 1);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                Label cell = new Label("");
                cell.setMinSize(50,50);
                cell.getStyleClass().add("tile");

                gridPane.add(cell, col, row);
                board[row][col] = cell;
            }
        }

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> gameRestart());
        restartButton.getStyleClass().add("button");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 700);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        scene.setOnKeyTyped(keyEvent -> {
            String letter = keyEvent.getCharacter().toUpperCase();
            if (letter.matches("[A-Z]") && currentCol < 5) {
                board[currentRow][currentCol].setText(letter);
                currentCol++;
            }
        });
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    if (currentCol == 5) {
                        String guessWord = "";
                        for (int i = 0; i < 5; i++) {
                            guessWord += board[currentRow][i].getText();
                        }

                        if (!WordLoader.invalidWord(guessWord)) {
                            invalidWordLabel.getStyleClass().add("message");
                            invalidWordLabel.setText("Invalid Word!");
                            invalidWordLabel.setOpacity(1);
                            FadeTransition fadeOutWord = new FadeTransition(Duration.seconds(2), invalidWordLabel);
                            fadeOutWord.setFromValue(1.0);
                            fadeOutWord.setToValue(0.0);
                            fadeOutWord.play();

                            for (int i = 0; i < 5; i++) {
                                board[currentRow][i].setText("");
                            }
                            currentCol = 0;
                            return;
                        }

                        checkWord(guessWord);

                        if (guessWord.equals(secretWord)) {
                            winMessage();
                            return;
                        }

                        currentRow++;
                        currentCol = 0;

                        if (currentRow == 6) {
                            loseMessage();
                        }
                    }
                    break;

                case BACK_SPACE:
                    if (currentCol > 0) {
                        currentCol--;
                        board[currentRow][currentCol].setText("");
                    }
                    break;

                default:
                    break;
            }
        });

        BorderPane topPane = new BorderPane();

        highScoreLabel.setText("High Score: 0");
        highScoreLabel.getStyleClass().add("high-score");
        root.setTop(titleLabel);
        root.setCenter(gridPane);
        root.setBottom(restartButton);
        root.setPadding(new Insets(30,0,30,0));
        topPane.setCenter(titleLabel);
        topPane.setBottom(highScoreLabel);
        root.setTop(topPane);
        BorderPane.setAlignment(highScoreLabel, Pos.CENTER);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setAlignment(restartButton, Pos.CENTER);


        stage.setTitle("Wordle");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void winMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("You Win!");
        alert.setContentText("The correct word is:  " +secretWord);
        alert.showAndWait();
        newGame();
    }

    private void loseMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("You Lose!");
        alert.setContentText("The correct word is:  " +secretWord);
        alert.showAndWait();
        highScore = 0;
        highScoreLabel.setText("High Score: 0");
        newGame();
    }

    private void newGame() {
        WordLoader wordLoader = new WordLoader();
        secretWord = wordLoader.getSecretWord();
//        System.out.println("New secret word is:  " +secretWord);

        currentRow = 0;
        currentCol = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                board[row][col].setText("");
                board[row][col].setStyle("");
                board[row][col].getStyleClass().clear();
                board[row][col].getStyleClass().add("tile");
            }
        }
    }

    private void gameRestart() {
        WordLoader wordLoader = new WordLoader();
        secretWord = wordLoader.getSecretWord();
//        System.out.println("New word after restart is: " +secretWord);

        currentRow = 0;
        currentCol = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                board[row][col].setText("");
                board[row][col].setStyle("");
                board[row][col].getStyleClass().clear();
                board[row][col].getStyleClass().add("tile");
            }
        }
        highScore = 0;
        highScoreLabel.setText("High Score: 0");
    }

    public static void main(String[] args) {
        launch();
    }
}
