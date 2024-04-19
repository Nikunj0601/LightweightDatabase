package org.example;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
public class TwoFactorAuthentication {
    private Map<String, String> userCredentials = new HashMap<>();
    public Map<String, String> userCaptcha = new HashMap<>();
    private String textFileName = "user_data.txt"; // Change this to your desired text file name

    public TwoFactorAuthentication() {
        loadUserDataFromTextFile(); // Load user data from the text file when the class is initialized
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 hashing algorithm not available.", e);
        }
    }

    public void registerUser(String username, String password) {
        String hashPassword = hashPassword(password);
        userCredentials.put(username, hashPassword);
    }

    public boolean authenticateUser(String username, String password, String captcha) {
        if (userCredentials.containsKey(username)) {
            String storedPassword = userCredentials.get(username);
            String hashPassword = hashPassword(password);
            if (storedPassword.equals(hashPassword) && validateCaptcha(username, captcha)) {
                return true;
            }
        }
        return false;
    }

    private boolean validateCaptcha(String username, String enteredCaptcha) {
        if (userCaptcha.containsKey(username)) {
            String storedCaptcha = userCaptcha.get(username);
            return storedCaptcha.equals(enteredCaptcha);
        }
        return false;
    }

    public void generateCaptcha(String username) {
        Random random = new Random();
        String captcha = String.format("%04d", random.nextInt(10000)); // Generate a 4-digit random captcha
        userCaptcha.put(username, captcha);
    }

    private void loadUserDataFromTextFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(textFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 2) {
                    String username = userData[0];
                    String password = userData[1];
                    userCredentials.put(username, password);
                }
            }
            System.out.println("User data loaded from the text file.");
        } catch (IOException e) {
            System.err.println("Error loading user data from the text file.");
        }
    }

    public void saveUserDataToTextFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFileName))) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
            System.out.println("User data has been saved to the text file.");
        } catch (IOException e) {
            System.err.println("Error saving user data to the text file.");
        }
    }
}
