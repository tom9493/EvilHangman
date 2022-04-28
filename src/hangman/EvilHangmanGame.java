package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private Set<String> dictionaryList;
    private Set<String> biggestSubset;
    private SortedSet<Character> usedLetters;
    private StringBuilder wordSoFar;
    private Map<String, Set<String>> map;
    private int guessCorrect;
    private int wordSize;

    public EvilHangmanGame() {
        dictionaryList = new HashSet<>();
        biggestSubset = new HashSet<>();
        map = new HashMap<>();
        usedLetters = new TreeSet<>();
        wordSoFar = new StringBuilder();
        guessCorrect = 0;
        wordSize = 0;
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {

        reset();
        wordSize = wordLength;
        Scanner scan = new Scanner(dictionary);
        int longestWord = 0;

        while (scan.hasNext()) {
            String word = scan.next().toLowerCase();
            if (word.length() == wordSize) { dictionaryList.add(word); }
            if (word.length() > longestWord) { longestWord = word.length(); }
        }

        if (dictionaryList.size() == 0) { throw new EmptyDictionaryException("Empty dictionary..."); }
        if (wordLength <= 1) { throw new EmptyDictionaryException(("Word length not > 1...")); }
        if (wordLength > longestWord) { throw new EmptyDictionaryException("wordLength longer than longestWord"); }
        for (int i = 0; i < wordSize; ++i) { wordSoFar.append("_"); }

        scan.close();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        guessCorrect = 0;
        if (guess >= 'A' & guess <= 'Z') { guess = Character.toLowerCase(guess); }
        // Check if guess has already been made. Add it to list if not and continue
        if (usedLetters.contains(guess)) { throw new GuessAlreadyMadeException("Guess already made!"); }
        usedLetters.add(guess);

        // Add all dictionary words to map. Use guessed character to determine where they go
        for (String word : dictionaryList) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < word.length(); ++i) {
                if (word.charAt(i) == guess) { s.append(guess); }
                else { s.append('_'); }
            }
            if (!map.containsKey(s.toString())) { map.put(s.toString(), new HashSet<>()); }
            map.get(s.toString()).add(word); // Uses map key s to access HashSet and add new word from dictionary
        }

        String s = ""; // Used to update wordSoFar and
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > biggestSubset.size()) {
                biggestSubset = entry.getValue(); s = entry.getKey();
            }
            // If the subsets are the same size go through 4 criteria to choose best
            else if (entry.getValue().size() == biggestSubset.size()) {
                // First: words with no instances of the guess
                if (hasGuessChar(entry.getKey(), guess) == 0) { biggestSubset = entry.getValue(); s = entry.getKey(); }

                // Second: fewest instances of the letters
                else if (hasGuessChar(entry.getKey(), guess) < hasGuessChar(s, guess)) {
                    biggestSubset = entry.getValue(); s = entry.getKey();
                }
                else if (hasGuessChar(entry.getKey(), guess) > hasGuessChar(s, guess)) {/* NA */ }

                // Third: If the number of instances is equal, choose the one where it is furthest right
                else if (rightMost(entry.getKey(), guess, wordSize) > rightMost(s, guess, wordSize)) {
                    biggestSubset = entry.getValue(); s = entry.getKey();
                }
                else if (rightMost(entry.getKey(), guess, wordSize) < rightMost(s, guess, wordSize)) {/* NA */}

                // Fourth: Get the next right most letter
                else if (rightMost(entry.getKey(), guess, rightMost(s, guess, wordSize)) >
                        rightMost(s, guess, rightMost(s, guess, wordSize))) {
                    biggestSubset = entry.getValue(); s = entry.getKey();
                }
            }
        }

        // Update wordSoFar with new
        int instances = 0;
        for (int i = 0; i < wordSize; ++i) {
            if (s.charAt(i) == guess) {
                wordSoFar.setCharAt(i, guess);
                ++instances; }
        }

        if (instances != 0) { System.out.println("Yes! There are (" + instances + ") " + guess + "'s!"); guessCorrect = 1; }
        else { System.out.println("Sorry, there are no " + guess); }

        dictionaryList = biggestSubset;
        biggestSubset = new HashSet<>();
        map = new HashMap<>();
        return dictionaryList;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return usedLetters;
    }

    public String getWordSoFar() { return wordSoFar.toString(); }

    public int hasGuessChar(String s, char guess) {
        int instances = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == guess) { ++instances; }
        }
        return instances;
    }

    public int rightMost(String s, char guess, int maxIndex) {
        int index = 0;
        for (int i = 0; i < maxIndex; ++i) {
            if (s.charAt(i) == guess) { index = i; }
        }
        return index;
    }

    public boolean isFull(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '_') { return false; }
        }
        return true;
    }

    public void reset() {
        dictionaryList = new HashSet<>();
        biggestSubset = new HashSet<>();
        map = new HashMap<>();
        usedLetters = new TreeSet<>();
        wordSoFar = new StringBuilder();
        guessCorrect = 0;
        wordSize = 0;
    }

    public int getGuessCorrect() { return guessCorrect; }

    public String getLoseWord() { return dictionaryList.toArray()[0].toString(); }
}