package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {

        String file = args[0];
        File dictionaryFile = new File(file);
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);
        int fullWordCheck = 0;

        Scanner scan = new Scanner(System.in);
        EvilHangmanGame game = new EvilHangmanGame();

        try {
            game.startGame(dictionaryFile, wordLength);
            char c;

            while (guesses > 0 & fullWordCheck == 0) {

                System.out.println("You have " + guesses + " guesses left");
                System.out.println("Used letters: " + game.getGuessedLetters().toString());
                System.out.println("Word: " + game.getWordSoFar());
                System.out.println("Enter guess: ");

                // Make sure input is lower case letter
                c = scan.next().toLowerCase().charAt(0);
               // if (c >= 'A' & c <= 'Z') { c = Character.toLowerCase(c); }
                while (c < 'a' | c > 'z') { System.out.println("Invalid input"); c = scan.next().toLowerCase().charAt(0); }

                try {
                    if (game.makeGuess(c) != null & game.getGuessCorrect() != 1) { --guesses; }
                } catch (GuessAlreadyMadeException e) { System.out.println("Guess already made!"); }
                if (game.isFull(game.getWordSoFar())) { fullWordCheck = 1; }
            }
        }
        catch (EmptyDictionaryException | IOException e) { e.printStackTrace(); }

        if (fullWordCheck == 1) { System.out.println("You win! You guessed the word: " + game.getWordSoFar()); }
        if (fullWordCheck == 0) { System.out.println("You lose! Word was: " + game.getLoseWord()); }

        scan.close();
    }

}
