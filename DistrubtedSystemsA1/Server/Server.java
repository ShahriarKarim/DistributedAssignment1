import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, String> loggedInUsers = new HashMap<>();
    private static Map<String, Map<String, Integer>> clientHealthData = new HashMap<>();

    public static void main(String[] args) {

        int port = 1099; //defaultt port
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                /*create a new thread to handle each client requests*/
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {/*client handler, handles client requests*/
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        private String username;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String request = in.readLine();

                    if (request == null) {
                        break;
                    }

                    if (request.startsWith("REGISTER")) {
                        /*user regisration details*/
                        /*check if username is unique, can not be same*/
                        String[] parts = request.split(" ");
                        String newUsername = parts[1];
                        String newPassword = parts[2];

                        if (users.containsKey(newUsername)) {
                            out.println("Registration failed: Username already exists.");
                        } else {
                            users.put(newUsername, newPassword);
                            out.println("Registration successful.");
                        }
                    } else if (request.startsWith("LOGIN")) {
                        // Logging in details
                        String[] parts = request.split(" ");
                        String enteredUsername = parts[1];
                        String enteredPassword = parts[2];
                        /*check if userpass combo in hashmap*/

                        if (users.containsKey(enteredUsername) && users.get(enteredUsername).equals(enteredPassword)) {
                            username = enteredUsername;
                            loggedInUsers.put(username, "");
                            out.println("Login successful.");
                        } else {
                            out.println("Login failed.");
                            System.out.println("Users HashMap after registration: " + users);
                        }
                    } else if (username != null && request.startsWith("INITIAL_HEALTH_INFO_REQUIRED")) {
                        out.println("Please enter your age, weight, height, and activity level, separated by spaces.");
                        String healthInfo = in.readLine();
                        String[] healthInfoParts = healthInfo.split(" ");

                        if (healthInfoParts.length == 5) {
                            try {
                                int age = Integer.parseInt(healthInfoParts[0]);
                                int weight = Integer.parseInt(healthInfoParts[1]);

                                int height = Integer.parseInt(healthInfoParts[2]);
                                int activityLevel = Integer.parseInt(healthInfoParts[3]);
                                int WeightGoal = Integer.parseInt(healthInfoParts[4]);
                                /*extract the client informtion and store to unique client hashmap*/


                                Map<String, Integer> healthData = new HashMap<>();
                                healthData.put("AGE", age);
                                healthData.put("WEIGHT", weight);
                                healthData.put("HEIGHT", height);
                                healthData.put("ACTIVITY_LEVEL", activityLevel);
                                healthData.put("WEIGHTGOAL", WeightGoal);
                                clientHealthData.put(username, healthData);

                                out.println("Initial health information saved.");
                            } catch (NumberFormatException e) {
                                out.println("Invalid input. Please enter valid numeric values.");
                            }
                        } else {
                            out.println("Invalid input. Please enter age, weight, height, activity level, and lastly goal weight.");
                        }
                        } else if (username != null && request.startsWith("GET_TDEE")) {
                        if (clientHealthData.containsKey(username)) {
                            Map<String, Integer> healthData = clientHealthData.get(username);
                            int age = healthData.get("AGE");
                            int weight = healthData.get("WEIGHT");
                            int height = healthData.get("HEIGHT");
                            int activityLevel = healthData.get("ACTIVITY_LEVEL");

                            int tdee = calculateTDEE(age, weight, height, activityLevel);
                            out.println("Your TDEE is: " + tdee);

                        } else { //if no healtth data of client then tell them to put in health data
                            out.println("Health information is missing. Please provide your initial health information.");
                        }
                        }
                        else if (username != null && request.startsWith("BMI")) {
                        if (clientHealthData.containsKey(username)) {
                            Map<String, Integer> healthData = clientHealthData.get(username);
                            int weight = healthData.get("WEIGHT");
                            int height = healthData.get("HEIGHT");

                            double BMI = calculateBMI(weight, height);


                            out.println("Your BMI is " + BMI);
                        }
                        else { /*missing client info*/
                            out.println("Health info is missing. Please provide your initial health info");
                        }
                        }

                        else if (username != null && request.startsWith("WeightGoal")) {
                        if (clientHealthData.containsKey(username)) {
                            Map<String, Integer> healthData = clientHealthData.get(username);
                            int weight = healthData.get("WEIGHT");
                            int WeightGoal = healthData.get("WEIGHTGOAL");

                            int GoalTime = WeeksUntilGoal(weight, WeightGoal);

                            out.println("The number of weeks to get to your goal weight is " + GoalTime);
                        }
                        }
                        
                        
                        else {
                        out.println("Unknown command.");
                    }
                }




            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private int calculateTDEE(int age, int weight, int height, int activityLevel) {
            // TDEE FORMULA
            return (int) ((10 * weight) + (6.25 * height) - (5 * age) + (5 * activityLevel));
        }

        private double calculateBMI(int weightPounds, int heightCM) {
            // Convert height to metres
            // and weight from pounds to kg
            double heightMeters = heightCM / 100.0;
            double weightKg = weightPounds * 0.45359237;
        
            return weightKg / (heightMeters * heightMeters);
        }

        private int WeeksUntilGoal(int weight, int WeightGoal) {
            // weeks until reach your weight goal from current weight
            //WeightGoal = (double) WeightGoal * 1.20;
            int WeeksUntilGoals = WeightGoal - weight;
            return (WeeksUntilGoals / 7);

        }



    }
}
