/* Name: Kelsey Lopez Aldas
 * Email: klopezaldas@hawk.iit.edu
 * Course: CS 451
 */

import java.io.*;
import java.net.*;

public class klopezaldas_client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9806;

    public static void main(String[] args) {
        try (Socket soc = new Socket(SERVER_ADDRESS, SERVER_PORT)){
            System.out.println("Client Started.");
            System.err.println("");
            
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            String serverResponse;

            while ((serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                if (serverResponse.contains("play again?")) { //play again
                    String answer = userInput.readLine(); //gets answer from user
                    out.println(answer); //sends decision to server
                    //if (!answer.equalsIgnoreCase("yes") || !answer.equalsIgnoreCase("y")) {
                    //    break; //exits if user is done playing
                    //}
                } else if (serverResponse.contains("Enter a letter")){ //guess a letter
                    String guess = userInput.readLine(); //reads user input
                    out.println(guess); //sends to server
                } else if (serverResponse.contains("Exiting")){ //exit
                    break; //exits
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server from client: " + e.getMessage());
        }
    }
}
