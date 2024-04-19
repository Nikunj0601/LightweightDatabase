package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.example.QueryTypeKeywords.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        TwoFactorAuthentication auth = new TwoFactorAuthentication();
        DatabaseQuery databaseQuery = new DatabaseQuery();
        QueryHandler queryHandler = new QueryHandler();
        Scanner scanner = new Scanner(System.in);

        String databaseName = null;
        String username = null;
        Boolean isAuthenticated = false;
        Boolean exit = false;
        String folderPath = "Database/";

        while (true) {
            System.out.println("Do you want to login or signup?");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.print("Choose an option (1/2): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character


            // User registration
            if (choice == 1) {
                System.out.print("Enter a new username: ");
                String newUsername = scanner.nextLine();
                System.out.print("Enter a password for the new user: ");
                String newPassword = scanner.nextLine();
                auth.registerUser(newUsername, newPassword);
                auth.saveUserDataToTextFile();
                System.out.println("Registration successful.");
            } else if (choice == 2) {
                System.out.print("Enter your username: ");
                username = scanner.nextLine();
                System.out.print("Enter your password: ");
                String password = scanner.nextLine();

                auth.generateCaptcha(username);
                System.out.println("Enter the Captcha: " + auth.userCaptcha.get(username));

                String enteredCaptcha = scanner.nextLine();

                if (auth.authenticateUser(username, password, enteredCaptcha)) {
                    isAuthenticated = true;
                    System.out.println("Authentication successful.");
                    break;
                } else {
                    System.out.println("Authentication failed.");
                }
            } else {
                System.out.print("Invalid choice.");
            }
        }
        String path = folderPath + username + "/";
        while (isAuthenticated && !exit) {
            System.out.println("Enter SQL query or (exit) to exit the program:");
            StringBuilder query = new StringBuilder();
            String inputLine;
            while (true) {
                inputLine = scanner.nextLine();
                if (inputLine.equalsIgnoreCase("")) {
                    break;
                }
                query.append(inputLine).append("\n");
            }

            String sqlQuery = query.toString();
            if(sqlQuery.trim().equalsIgnoreCase("exit")){
                exit = true;
                System.out.println("successfully exited");
                scanner.close();
            }
            else if(sqlQuery.trim().toLowerCase().contains(BEGIN_TRANSACTION_KEYWORD) && sqlQuery.trim().toLowerCase().contains(END_TRANSACTION_KEYWORD)) {
                path = folderPath + username + "/" + databaseName + "/";
                List<String> queries = new ArrayList(Arrays.asList(sqlQuery.split("\n")));
                List<String> finalQueries = new ArrayList<>();
                String first = queries.remove(0);
                String last = queries.remove(sqlQuery.length() - 2);
                for(String q: queries) {
                    finalQueries.add(q);
                    if(q.trim().toLowerCase().contains(COMMIT_KEYWORD)){
                        for(String finalQuery: finalQueries) {
                            queryHandler.queryHandler(finalQuery.trim(),path);
                        }
                        finalQueries.clear();
                    }
                    if (q.trim().toLowerCase().contains(ROLLBACK_KEYWORD)){
                        finalQueries.clear();
                    }
                }
                System.out.println("Transaction ended.");
            }
            else if(sqlQuery.trim().toLowerCase().contains(USE_DATABASE_KEYWORD)) {
                String dbName = databaseQuery.executeUseDatabase(sqlQuery,path);
                if(dbName == null){
                    System.out.println("Database does not exists, select valid database.");
                }
                else {
                    databaseName = dbName;
                    System.out.println("Selected database: " + databaseName);
                }
            }
            else if (sqlQuery.trim().toLowerCase().contains(DROP_DATABASE_KEYWORD)) {
                path = folderPath + username + "/";
                System.out.println(queryHandler.queryHandler(sqlQuery,path));
                databaseName = null;
            }
            else if (!sqlQuery.trim().toLowerCase().contains(CREATE_DATABASE_KEYWORD) && databaseName == null) {
                System.out.println("Select valid database.");
            }
            else if(!sqlQuery.trim().toLowerCase().contains(CREATE_DATABASE_KEYWORD) && databaseName != null) {
                path = folderPath + username + "/" + databaseName + "/";
                System.out.println(queryHandler.queryHandler(sqlQuery,path));
            }
            else if (sqlQuery.trim().toLowerCase().contains(CREATE_DATABASE_KEYWORD)) {
                path = folderPath + username + "/";
                System.out.println(queryHandler.queryHandler(sqlQuery,path));
            }
            else {
                System.out.println("Invalid SQL query.");
            }
        }
    }
}