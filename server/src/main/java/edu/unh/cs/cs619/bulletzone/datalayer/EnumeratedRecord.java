package edu.unh.cs.cs619.bulletzone.datalayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class EnumeratedRecord {

    abstract int getID();
    abstract void setID(int id);
    abstract String getRecordInsertString();

    void insertInto(Connection dataConnection, String recordType) throws SQLException {
        PreparedStatement insertStatement = dataConnection.prepareStatement(
                getRecordInsertString(), Statement.RETURN_GENERATED_KEYS);
        int affectedRows = insertStatement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating Entity of type " + recordType + " failed.");

        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            setID(generatedKeys.getInt(1));
        }
        else {
            throw new SQLException("Created Entity of type " + recordType + " but failed to obtain ID.");
        }
    }
}