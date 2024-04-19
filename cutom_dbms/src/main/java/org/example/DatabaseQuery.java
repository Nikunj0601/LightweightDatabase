package org.example;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseQuery {
    public String executeCreateDatabase(String query, String path) {
        String databaseName = extractDatabaseNameCreateQuery(query);
        if(databaseName == null){
            return "Invalid SQL syntax.";
        }
        String databasePath = path + databaseName;
        File databaseDirectory = new File(databasePath);
        if (databaseDirectory.exists()) {
            return "Database already exists.";
        } else {
            if (databaseDirectory.mkdirs()) {
                return "Database created successfully.";
            } else {
                return "Failed to create the database.";
            }
        }
    }

    public String executeUseDatabase(String query, String path) {
        String databaseName = extractDatabaseNameUseQuery(query);
        String databasePath = path + databaseName;
        File databaseDirectory = new File(databasePath);
        if(databaseDirectory.exists()) {
            return databaseName;
        }
        else {
            return null;
        }
    }

    public String executeDropDatabase(String query, String path) {
        String databaseName = extractDatabaseNameDropQuery(query);
        if(databaseName == null){
            return "Invalid SQL syntax.";
        }
        String databasePath = path + databaseName;
        File databaseDirectory = new File(databasePath);
        if (databaseDirectory.exists()) {
            if(databaseDirectory.delete()){
                return "Database deleted successfully";
            }
            return "Database could not be deleted.";
        } else {
            return "Database does not exists";
        }
    }

    public static String extractDatabaseNameCreateQuery(String sqlQuery) {
        // Regular expression to match SQL CREATE statement and capture the database name
        String regex = "create database (\\w+);";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractDatabaseNameUseQuery(String sqlQuery) {
        // Regular expression to match SQL USE statement and capture the database name
        String regex = "use (\\w+);";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extractDatabaseNameDropQuery(String sqlQuery) {
        // Regular expression to match SQL CREATE statement and capture the database name
        String regex = "drop database (\\w+);";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
