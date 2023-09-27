import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class App {
    // Defining ANSI escape codes
    final static String RESET = "\u001B[0m";
    final static String YELLOW = "\u001B[33m";
    final static String GREEN = "\u001B[32m";
    final static String BLUE = "\u001B[34m";

    public static void main(String[] args) throws Exception {
        // Initializing variables
        String word = "";
        String guess = "";
        int error = 0;
        int tries = 6;
        boolean gameOver = false;
        Scanner scanGuess = new Scanner(System.in);
        boolean correct;
        boolean firstCycle = true;

        // initializing records variables
        File records = new File("files/records.txt");
        int gamesTotal = 0;
        int gamesWon = 0;
        int bestTry = 0;
        int currentStreak = 0;
        int maxStreak = 0;

        // initializing dictionary
        ArrayList<String> dictionary = new ArrayList<String>();
        dictionary = parseDictionary();

        // check if a records file exists, create one if not
        if (!records.exists()) {
            initializeRecords(records);
        }

        // load records file data
        try {
            Scanner importRecords = new Scanner(records);
            gamesTotal = Integer.parseInt(importRecords.nextLine());
            gamesWon = Integer.parseInt(importRecords.nextLine());
            bestTry = Integer.parseInt(importRecords.nextLine());
            maxStreak = Integer.parseInt(importRecords.nextLine());
            importRecords.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        // main loop
        while (gameOver == false) {
            boolean cycleEnd = false;
            // display records
            if (firstCycle == true) {
                System.out.println(BLUE + "Welcome to Wordle82." + RESET);
            } else {
                System.out.println(BLUE + "Restarting Wordle82." + RESET);
            }
            displayRecords(gamesTotal, gamesWon, bestTry, currentStreak, maxStreak);
            // determine word to guess
            word = rollWord(dictionary);
            // terminal request
            System.out.println("The word is " + BLUE + word + RESET + ".");
            System.out.println("Please insert guess: ");
            // cycle until player either guesses correctly or runs out of tries
            while (cycleEnd == false) {
                // input reading
                guess = scanGuess.nextLine();
                // Call guessErrorHandler to check if answer is valid
                error = guessErrorHandler(guess, dictionary);
                // if no error was thrown, compare guess with word
                if (error == 0) {
                    correct = guessCheck(guess, word);
                    if (correct == true) {
                        // player guessed correctly
                        System.out.println("Correct!");
                        gamesTotal++;
                        gamesWon++;
                        if (bestTry == 0){
                            bestTry = 7 - tries;
                        } else {
                            if ((7 - tries) < bestTry) {
                                bestTry = 7 - tries;
                            }
                        }
                        currentStreak++;
                        if (currentStreak > maxStreak) {
                            maxStreak = currentStreak;
                        }
                        writeRecords(records, gamesTotal, gamesWon, bestTry, maxStreak);
                        tries = 6;
                        firstCycle = false;
                        cycleEnd = true;
                    } else {
                        // player guessed incorrectly
                        System.out.println("Incorrect!");
                        tries -= 1;
                        // player is allowed to retry until tries run out
                        if (tries > 0) {
                            System.out.println(tries + " attempts remaining. Please try again:");
                        } else {
                            System.out.println("No attempts remaining. Game over.");
                            System.out.println("Your streak was " + currentStreak);
                            System.out.println("Shutting down.");
                            gameOver = true;
                            gamesTotal++;
                            writeRecords(records, gamesTotal, gamesWon, bestTry, maxStreak);
                            cycleEnd = true;
                        }
                    }
                } else {
                    System.out.println("Please insert valid guess:");
                }
            }
        }
        scanGuess.close();
        return;
    }

    // guessErrorHandler checks if the user's input is a valid answer, returns an error code if not.
    static int guessErrorHandler(String guess, ArrayList<String> dictionary) {
        if (guess.matches("[a-zA-Z]+")) {
            if (guess.length() == 5) {
                guess = guess.toUpperCase();
                if (dictionary.contains(guess)) {
                    System.out.println("Guess is all letters.");
                    return 0;
                } else {
                    System.out.println("Guess is not a dictionary word. Guess must be a real word.");
                    return 3;
                }
            } else {
                System.out.println("Wrong guess length. Guess must have 5 letters.");
                return 2;
            }
        } else {
            System.out.println("Guess has invalid characters. Guess must have only letters.");
            return 1;
        }
    }

    // rollWord returns a random word from a dictionary
    static String rollWord(ArrayList<String> dictionary) {
        String roll = dictionary.get(new Random().nextInt(dictionary.size()));
        return roll;
    }

    // parseDictionary reads the dictionary text file and returns an ArrayList with only the 5-letter words in it
    static ArrayList<String> parseDictionary() {
        ArrayList<String> fullDictionary = new ArrayList<String>();
        String fileLine;
        try {
            File txtDictionary = new File("files/dictionary.txt");
            Scanner importDictionary = new Scanner(txtDictionary);
            while (importDictionary.hasNextLine()) {
                fileLine = importDictionary.nextLine();
                if (fileLine.length() == 5) {
                    fileLine = fileLine.toUpperCase();
                    fullDictionary.add(fileLine);
                }
            }
            importDictionary.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. Dictionary file not found.");
            e.printStackTrace();
        }
        return fullDictionary;
    }

    // partialCheck runs through the player's input and checks which letters match the word, and which of those are placed correctly
    static String partialCheck (String word, String guess) {
        int length = word.length();
        String letter;
        String result = "";
        String correct = GREEN;
        String misplaced = YELLOW;
        int inc = 0;
        while (inc < length) {
            letter = String.valueOf(guess.charAt(inc));
            if (word.contains(letter)) {
                if (guess.charAt(inc) == word.charAt(inc)) {
                    correct += guess.charAt(inc);
                    result += GREEN;
                } else {
                    misplaced += guess.charAt(inc);
                    result += YELLOW;
                }
            } else {
                result += RESET;
            }
            result += guess.charAt(inc);
            inc++;
        }
        correct += RESET;
        misplaced += RESET;
        System.out.println("Correctly placed letters: " + correct);
        System.out.println("Letters present but misplaced: " + misplaced);
        result += RESET;
        return result;
    }

    // initializeRecords creates a new records file if none is present
    static void initializeRecords(File records) {
        try {
            records.createNewFile();
            writeRecords(records, 0, 0, 0, 0);
        } catch (IOException e) {
            System.out.println("Error creating records file.");
            e.printStackTrace();
        }
    }

    // writeRecords rewrites the records after round completion
    static void writeRecords(File records, int gamesTotal, int gamesWon, int bestTry, int maxStreak) {
            try {
                PrintWriter pw = new PrintWriter(records);
                pw.close();
            } catch (IOException e) {
                System.out.println("Error clearing file.");
                e.printStackTrace();
            }
            try {
                FileWriter newRecords = new FileWriter(records);
                newRecords.write(String.valueOf(gamesTotal) + "\n");
                newRecords.write(String.valueOf(gamesWon) + "\n");
                newRecords.write(String.valueOf(bestTry) + "\n");
                newRecords.write(String.valueOf(maxStreak) + "\n");
                newRecords.close();
            } catch (IOException e) {
                System.out.println("Error writing to file.");
                e.printStackTrace();
            }
    }

    // displayRecords prints the records to the terminal
    static void displayRecords(int gamesTotal, int gamesWon, int bestTry, int currentStreak, int maxStreak) {
        System.out.println("Games played: " + gamesTotal);
        System.out.println("Games won: " + gamesWon);
        if (gamesTotal == 0) {
            System.out.println("Percentage of games won: 0%");
        } else {
            System.out.println("Percentage of games won: " + (int)(((double)gamesWon / (double)gamesTotal) * 100) + "%");
        }
        System.out.println("Best try: " + bestTry);
        System.out.println("Current streak: " + currentStreak);
        System.out.println("Max streak: " + maxStreak);
        return;
    }

    // guessCheck 
    static boolean guessCheck(String guess, String word) {
        String checkedGuess;
        guess = guess.toUpperCase();
        System.out.println("Your guess was: " + guess);
        checkedGuess = partialCheck(word, guess);
        System.out.println(checkedGuess);
        if (word.equals(guess)) {
            return true;
        } else {
            return false;
        }
    }
}