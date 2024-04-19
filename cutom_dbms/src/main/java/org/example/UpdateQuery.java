package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateQuery {
    SelectQuery selectQuery = new SelectQuery();

    public String executeUpdateQuery(String updateQuery, String path) {
        // Extract table name from the UPDATE query
        String tableName = extractTableName(updateQuery);

        // Extract the SET clause and WHERE condition from the UPDATE query
        String setClause = extractSetClause(updateQuery);
        String whereCondition = extractWhereCondition(updateQuery);

        // Read data from the original file and store it in a list
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path + tableName + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] columnNames = data.getFirst().split("\\|");

        // Add the first row (column names) to the updated data
        // Iterate over the data, apply the WHERE condition, and update matching rows
        for (int i = 1; i < data.size(); i++) {
            String line = data.get(i);
            String[] rowData = line.split("\\|");
            if (selectQuery.matchesWhereCondition(whereCondition, rowData, columnNames)) {
                data.set(i,updateRow(setClause, rowData, columnNames));
            }
        }

        // Rewrite the original file with the updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + tableName + ".txt"))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine(); // Add a new line for the next record
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "rows updated";
    }

    private static String extractTableName(String updateQuery) {
        String pattern = "UPDATE (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(updateQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static String extractSetClause(String updateQuery) {
        String pattern = "SET (.+?) WHERE";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(updateQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static String extractWhereCondition(String updateQuery) {
        String pattern = "WHERE (.+);";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(updateQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null; // No WHERE condition found
    }

    private String updateRow(String setClause, String[] rowData, String[] columnNames) {
        String[] updates = setClause.split(",");
        for (String update : updates) {
            String[] parts = update.trim().split("=");
            if (parts.length == 2) {
                String column = parts[0].trim();
                String value = parts[1].trim();
                for (int i = 0; i < columnNames.length; i++) {
                    if (columnNames[i].split(" ")[0].equals(column)) {
                        rowData[i] = value;
                        break;
                    }
                }
            }
        }
        return String.join("|", rowData);
    }
}
