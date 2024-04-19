package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateQuery {
    public String createQuery(String query, String path) {
        // Extract table name and column names from the CREATE TABLE query
        String tableName = extractCreateQueryTableName(query);
        String columnNames = extractCreateQueryColumnNames(query);

        // Write column names to the text file with '|' as separator
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + tableName + ".txt"))) {
            String[] columnNamesArray = columnNames.split(",");
            for (String columnName : columnNamesArray) {
                writer.write(columnName + "|");
            }
            writer.newLine();
            return "Table created successfully";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractCreateQueryTableName(String createTableQuery) {
        String pattern = "create table (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(createTableQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static String extractCreateQueryColumnNames(String createTableQuery) {
        String pattern = "\\((.*?)\\);";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(createTableQuery);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}
