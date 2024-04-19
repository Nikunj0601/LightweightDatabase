package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteRowQuery {

    SelectQuery selectQuery = new SelectQuery();
    public String executeDeleteQuery(String deleteQuery, String path) {
        // Extract table name from the DELETE query
        String tableName = extractTableName(deleteQuery);

        // Extract the WHERE condition from the DELETE query
        String whereCondition = extractWhereCondition(deleteQuery);

        // Read data from the original file and store it in a list
        List<String> data = new ArrayList<>();
        List<String> updatedData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path + tableName + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create a list for updated data
        String[] columnNames = data.getFirst().split("\\|");

        // Iterate over the data, apply the WHERE condition, and add non-matching rows to updatedData
        for (String line : data) {
            String[] rowData = line.split("\\|");
            if (!selectQuery.matchesWhereCondition(whereCondition, rowData, columnNames)) {
                updatedData.add(line);
            }
        }
        // Rewrite the original file with the updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + tableName + ".txt"))) {
            for (String line : updatedData) {
                writer.write(line);
                writer.newLine(); // Add a new line for the next record
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "rows deleted";
    }

    private static String extractTableName(String deleteQuery) {
        String pattern = "FROM (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(deleteQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static String extractWhereCondition(String deleteQuery) {
        String pattern = "WHERE (.+);";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(deleteQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}
