package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertQuery {
    public String executeInsertQuery(String insertQuery, String path) {
        // Extract table name, column names, and values from the INSERT query
        String tableName = extractInsertQueryTableName(insertQuery);
        String values = extractValues(insertQuery);

        // Append values to the text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + tableName + ".txt", true)) // 'true' for append mode
        ) {
            String[] valuesArray = values.split(",");
            for (String value : valuesArray) {
                writer.write(value + "|");
            }
            writer.newLine(); // Add a new line for the next record
            return " Values inserted in table";
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractInsertQueryTableName(String insertQuery) {
        String pattern = "INSERT INTO (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(insertQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null; // Default table name if not found
    }

    private static String extractValues(String insertQuery) {
        String pattern = "VALUES \\(([^)]+)\\)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(insertQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null; // Default values if not found
    }
}
