/* Name: Kelsey Lopez Aldas
 * Email: klopezaldas@hawk.iit.edu
 * Course: CS 451
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class klopezaldas_server {
    private static final int PORT = 9806;
    private static final int MAX_LIVES = 10;
    private static final List<String> words = new ArrayList<>();

    public static void main(String[] args) {
        loadWords();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server connected.");
                new Thread(new GameHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static void loadWords() { //loads the file that has the list of words
        try (BufferedReader br = new BufferedReader(new FileReader("words-1.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error loading word file: " + e.getMessage());
        }
    }

    private static class GameHandler implements Runnable {
        private final Socket clientSocket;

        public GameHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                   
                playGame(in, out); //plays the game
            } catch (IOException e) {
                System.err.println("Error running game: " + e.getMessage());
            }
        }

        private void playGame(BufferedReader in, PrintWriter out) throws IOException {
            String word = getRandomWord();                              //gets a random word from the file
            System.out.println("The word is: " + word);                 //prints the word on the server side
            StringBuilder currentGuess = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                currentGuess.append("-");
            }
            Set<Character> guessedLetters = new HashSet<>();
            int lives = MAX_LIVES;
            boolean win = false;

            out.println("Welcome to Hangman!");

            while ((lives > 0) && (!win)) {         //play while they have more than zero lives and have not won the game
                out.println(currentGuess);
                out.println("You have " + lives + " lives left.");
                out.println("Enter a letter: ");

                String guess = in.readLine();       //gets the guess from the client
                boolean validInput = true;
                char letter = 'a'; //filler letter

                if (guess == null || guess.length() != 1 || !Character.isLetter(guess.charAt(0))) { //invalid input check
                    out.println("Invalid input. Please enter a single alphabetic character.");
                    validInput = false; //input is not valid, sets boolean to false
                } else {
                    letter = Character.toLowerCase(guess.charAt(0)); //valid input so it replaces the filler
                }
                
                if (guessedLetters.contains(letter)) {  //checks if they've already guessed the letter
                    out.println("You've already guessed that letter.");
                    validInput = false;
                }

                if (validInput){                        //if valid input add letter to the guessed list
                    guessedLetters.add(letter);
                    if (word.toLowerCase().indexOf(letter) >= 0) {
                        updateCurrentGuess(word, currentGuess, letter);
                        if (currentGuess.toString().equalsIgnoreCase(word)) {    //check if they won
                            out.println("Congratulations! The correct word was indeed '" + word + "'");
                            win = true;
                        }
                    } else { //input a valid guess but is wrong = lose a life
                        lives--;
                    }
                }
            }

            if (lives == 0) {   //game over
                out.println("Sorry, you have used up all " + MAX_LIVES + " lives. The correct word was '" + word + "'.");
            }

            out.println("Want to play again? (yes/no)");      //play again prompt
            String playAgain = in.readLine();                   //Reads response from client (user)
            if (playAgain.contains("y") || playAgain.contains("yes")){
                playGame(in, out);          //if yes call playGame function to play again
            } else {
                out.println("Thank you for playing, come again soon!"); //else exit
                //System.exit(0); //exits but this leads to "Error connection reset"
                out.println("Exiting..."); //this allows the client to identify when to exit w/o closing the server
            }
        }

        private void updateCurrentGuess(String word, StringBuilder currentGuess, char letter) { //replaces - with correct letter guessed
            for (int i = 0; i < word.length(); i++) { 
                if (Character.toLowerCase(word.charAt(i)) == letter) {
                    currentGuess.setCharAt(i, letter);
                }
            }
        }

        private String getRandomWord() {        //gets random word from the file
            Random rand = new Random();
            return words.get(rand.nextInt(words.size()));
        }
    }
}
