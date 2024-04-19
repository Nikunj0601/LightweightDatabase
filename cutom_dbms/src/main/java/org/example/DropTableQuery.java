package org.example;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropTableQuery {
    public String executeDropTableQuery(String dropQuery, String path) {
        String tableName = extractTableName(dropQuery);
        File tableFile = new File(path + tableName + ".txt");

        if (tableFile.exists()) {
            if (tableFile.delete()) {
                return "Table " + tableName + " dropped successfully.";
            } else {
                return "Failed to drop the table " + tableName + ".";
            }
        } else {
            return "Table " + tableName + " does not exist.";
        }
    }

    private static String extractTableName(String dropQuery) {
        String pattern = "DROP TABLE (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(dropQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}
