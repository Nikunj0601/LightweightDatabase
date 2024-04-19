package org.example;

import static org.example.QueryTypeKeywords.*;
public class QueryHandler {
    public String queryHandler(String query, String path) {

        CreateQuery createQuery = new CreateQuery();
        InsertQuery insertQuery = new InsertQuery();
        SelectQuery selectQuery = new SelectQuery();
        UpdateQuery updateQuery = new UpdateQuery();
        DeleteRowQuery deleteRowQuery = new DeleteRowQuery();
        DropTableQuery dropTableQuery = new DropTableQuery();
        DatabaseQuery databaseQuery = new DatabaseQuery();
        String queryLowerCase = query.trim().toLowerCase();

        if(queryLowerCase.contains(CREATE_DATABASE_KEYWORD)){
            return databaseQuery.executeCreateDatabase(query, path);
        }
        else if(queryLowerCase.contains(CREATE_TABLE_KEYWORD)){
            return createQuery.createQuery(query, path);
        }
        else if(queryLowerCase.contains(INSERT_KEYWORD)){
            return insertQuery.executeInsertQuery(query, path);
        }
        else if(queryLowerCase.contains(SELECT_KEYWORD)){
            return selectQuery.executeSelectQuery(query, path);
        }
        else if(queryLowerCase.contains(UPDATE_KEYWORD)){
            return updateQuery.executeUpdateQuery(query, path);
        }
        else if(queryLowerCase.contains(DROP_TABLE_KEYWORD)){
            return dropTableQuery.executeDropTableQuery(query, path);
        }
        else if(queryLowerCase.contains(DROP_DATABASE_KEYWORD)){
            return databaseQuery.executeDropDatabase(query, path);
        }
        else if(queryLowerCase.contains(DELETE_ROW_KEYWORD)){
            return deleteRowQuery.executeDeleteQuery(query, path);
        }
        return "Invalid SQL syntax.";
    }
}
