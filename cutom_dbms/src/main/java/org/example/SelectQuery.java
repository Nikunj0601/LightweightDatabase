package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQuery {
    public String executeSelectQuery(String selectQuery, String path) {
        // Extract table name and column names from the SELECT query
        String tableName = extractTableName(selectQuery);
        String[] columnNames = extractColumnNames(selectQuery);
        // Extract the WHERE condition from the SELECT query
        String whereCondition = extractWhereCondition(selectQuery);
        // Read data from the text file and filter based on the SELECT query
        StringBuilder results = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path + tableName + ".txt"))) {
            String line;
            String[] columns = reader.readLine().split("\\|");
            if(selectQuery.split(" ")[1].equals("*")) {
                columnNames = columns;
            }
            Integer[] selectedColumnIndexes = getSelectedColumnIndexes(columnNames, columns);
            results.append(getSelectedColumnNamesInOrder(selectedColumnIndexes,columns)).append("\n");
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split("\\|");
                if (matchesWhereCondition(whereCondition, rowData, columns)) {
                    results.append(getSelectedColumnsData(selectedColumnIndexes, rowData)).append("\n");
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results.toString();
    }

    private String getSelectedColumnNamesInOrder(Integer[] selectedColumnIndexes, String[] rowData){
        StringBuilder selectedColumnName = new StringBuilder();
        for(Integer index: selectedColumnIndexes){
            selectedColumnName.append(rowData[index].split(" ")[0]).append(" | ");
        }
        return selectedColumnName.toString().trim();
    }
    private static String extractTableName(String selectQuery) {
        String pattern = "FROM (\\w+)";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(selectQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null; // Default table name if not found
    }

    private static String[] extractColumnNames(String selectQuery) {
        String pattern = "SELECT (.+?) FROM";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(selectQuery);
        if(m.find()){
            return m.group(1).split(",");
        }
        return null;
    }

    private static String extractWhereCondition(String selectQuery) {
        String pattern = "WHERE (.+);";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(selectQuery);

        if (m.find()) {
            return m.group(1);
        }

        return null; // No WHERE condition found
    }

    public boolean matchesWhereCondition(String whereCondition, String[] rowData, String[] columns) {
        if (whereCondition == null) {
            return true; // No WHERE condition specified
        }

        // Implement your custom WHERE logic here
        String[] parts = whereCondition.split("=");
        if (parts.length == 2) {
            String[] column = { parts[0].trim() };
            String value = parts[1].trim();
            Integer[] columnIndex = getSelectedColumnIndexes(column,columns);
            return rowData[columnIndex[0]].equals(value);
        }

        return true; // Invalid WHERE condition
    }

    public Integer[] getSelectedColumnIndexes(String[] selectedColumnNames, String[] rowData) {
        Integer[] indexes = new Integer[selectedColumnNames.length];
        String[] allColumns = new String[rowData.length];
        for(int i =0; i<rowData.length;i++){
            String col = rowData[i].split(" ")[0];
            allColumns[i] = col;
        }
        for (int j =0; j < selectedColumnNames.length; j++){
            for(int i = 0; i < allColumns.length; i++){
                if(allColumns[i].equals(selectedColumnNames[j].split(" ")[0])){
                    indexes[j] = i;
                }
            }
        }
        return indexes;
    }
    private static String getSelectedColumnsData(Integer[] selectedIndexes, String[] rowData) {
        StringBuilder selectedData = new StringBuilder();
        for(Integer selectedIndex: selectedIndexes){
            selectedData.append(rowData[selectedIndex].trim()).append(" | ");
        }
        return selectedData.toString().trim();
    }
}
