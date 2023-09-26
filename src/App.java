import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class App {
    // Defining ANSI escape codes
    final static String RESET = "\u001B[0m";
    final static String YELLOW = "\u001B[33m";
    final static String GREEN = "\u001B[32m";
    final static String BLUE = "\u001B[34m";

    public static void main(String[] args) throws Exception {
        // Initializing variables
        ArrayList<String> dictionary = new ArrayList<String>();
        dictionary = parseDictionary();
        String word = rollWord(dictionary);
        String guess = "";
        String checkedGuess;
        int error = 0;
        int tries = 6;
        Scanner scanGuess = new Scanner(System.in);

        // Initial terminal request
        System.out.println("The word is " + BLUE + word + RESET + ".");
        System.out.println("Please insert guess: ");
        
        // Main cycle of the program runs through iterations of successive guesses
        while (tries > 0) {
            // Input reading
            guess = scanGuess.nextLine();
            // Call guessErrorHandler to check if answer is valid
            error = guessErrorHandler(guess, dictionary);
            // If no error was thrown, compare guess with word
            if (error == 0) {
                guess = guess.toUpperCase();
                System.out.println("Your guess was: " + guess);
                checkedGuess = partialCheck(word, guess);
                System.out.println(checkedGuess);
                if (word.equals(guess)) {
                    System.out.println("Correct!");
                    scanGuess.close();
                    return;
                } else {
                    System.out.println("Incorrect!");
                    tries -= 1;
                    if (tries > 0) {
                        System.out.println(tries + " attempts remaining. Please try again:");
                    }
                }
            } else {
                System.out.println("Please insert valid guess:");
            }
        }
        // Failure message and shutdown after number of tries reaches zero and while cycle is exited
        System.out.println("Out of attempts. Failed to correctly guess word.");
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
        //ArrayList<String> dictionary = new ArrayList<String>();
        //dictionary = parseDictionary();
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
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return fullDictionary;
    }

    static String partialCheck (String word, String guess) {
        int length = word.length();
        String letter;
        String result = "";
        int inc = 0;
        while (inc < length) {
            letter = String.valueOf(guess.charAt(inc));
            if (word.contains(letter)) {
                if (guess.charAt(inc) == word.charAt(inc)) {
                    result += GREEN;
                } else {
                    result += YELLOW;
                }
            } else {
                result += RESET;
            }
            result += guess.charAt(inc);
            inc++;
        }
        result += RESET;
        return result;
    }
}