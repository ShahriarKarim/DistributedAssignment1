import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        String serverAddress = "localhost";
        int serverPort = 1099; //default port we are using

        try {

            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            boolean loggedIn = false;
            String username = "";

            while (true) {
                if (!loggedIn) {
                    System.out.println("Please choose an option:");
                    System.out.println("1. To Register");
                    System.out.println("2. To Login");

                    int option = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline

                    if (option == 1) {
                        // Registration if user selects 1
                        System.out.print("Enter a new username: ");
                        String newUsername = scanner.nextLine();
                        System.out.print("Enter a password: ");
                        String newPassword = scanner.nextLine();

                        out.println("REGISTER " + newUsername + " " + newPassword);
                        String response = in.readLine();
                        System.out.println("Server: " + response);

                    } else if (option == 2) {
                        // Login if user selects 2
                        System.out.print("Enter your username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter your password: ");
                        String password = scanner.nextLine();

                        out.println("LOGIN " + username + " " + password);
                        String response = in.readLine();
                        System.out.println("Server: " + response);

                        if (response.equals("Login successful.")) {
                            loggedIn = true;
                        }
                    }
                } else {

                    //user welcome and selection options
                    System.out.println("WELCOME TO FULL FITNESS AND HEALTH APP");
                    System.out.println("This program will help you on all aspects of your Fitness Journey");

                    System.out.println("Choose an option:");
                    
                    System.out.println("3. Provide Initial Health Information");
                    System.out.println("4. Get TDEE");
                    System.out.println("5. Get BMI");


                    System.out.println("6. Time to reach Weight Goal");

                    //logs user out, sends logout request to server
                    System.out.println("7. Logout");


                    int option = scanner.nextInt();
                    scanner.nextLine(); // consume the newline

                    //user selections for novel features and health information
                    if (option == 3) {
                        System.out.print("(Space Seperated)Enter your age, weight, height(IN CM), activity level(1-2), and weight goal: ");
                        String healthInfo = scanner.nextLine();
                        out.println("INITIAL_HEALTH_INFO_REQUIRED");
                        out.println(healthInfo);
                        String response = in.readLine();
                        System.out.println("Server: " + response);
                    } else if (option == 4) {
                        out.println("GET_TDEE");
                        String response = in.readLine();
                        System.out.println("Server: " + response);
                    } else if (option == 5) {
                        out.println("BMI");
                        String response = in.readLine();
                        System.out.println("Server: " + response);
                    } 
                    
                    else if (option == 6) {
                        out.println("WeightGoal");
                        String response = in.readLine();
                        System.out.println("Server: " + response);
                    }

                    //logs out user if 7 selection
                    else if (option == 7) {
                        loggedIn = false;
                    } 


                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
