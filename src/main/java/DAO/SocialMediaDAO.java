package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.*;

/**
 * This Data Access Object allows provides access to the database and the functionality to create accounts, verify login credentials, create messages, retrieve messages, update messages, and delete messages.
 */
public class SocialMediaDAO {

    /**
     * Create and return an account provided a username and password.
     * On success the account is added to the database.
     * 
     * @param username
     * @param password 
     * @return User account on success, null on fail.
     */
    public Account createAccount(String username, String password){
        Connection connection = ConnectionUtil.getConnection();
        try {
            //Attempt to insert the username and password into the database, if 0 rows were inserted, we know that the username already existed.
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            //Get count of inserted rows
            int rowsInserted = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (rowsInserted > 0 && generatedKeys.next()){
                //Success
                int accountID = (int) generatedKeys.getInt("account_id");
                return new Account(accountID, username, password);
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        //Fail
        return null;
    }

    /**
     * Return the user account if the username and password match.
     * Return null if no match is found.
     * 
     * @param username
     * @param password
     * @return Account on success, null on fail.
     */
    public Account userLogin(String username, String password){
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Search database for matching username and password
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            //Get results from query
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //Success
                return new Account(resultSet.getInt("account_id"), resultSet.getString("username"), resultSet.getString("password"));
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        //Fail
        return null;
    }

    /**
     * Create and store a message in the database when conditions for the message are met.
     * 
     * @param messageText. Must be 255 characters or less. Must not be blank.
     * @param postedBy
     * @return Message stored in the database on success, null on fail.
     */
    public Message createMessage(String messageText, int postedBy, long postedAt){
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Attempt to insert the message into the database, if no rows are returned, we know the user doesn't exist.
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, postedBy);
            preparedStatement.setString(2, messageText);
            preparedStatement.setLong(3, postedAt);
            //Get results
            int rowsInserted = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (rowsInserted > 0 && generatedKeys.next()){
                //Success
                int messageID = generatedKeys.getInt("message_id");
                return new Message(messageID, postedBy, messageText, postedAt);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        //Fail
        return null;
    }

    /**
     * Returns all messages currently stored within the database.
     * 
     * @return list containing all messages currently within the database.
     */
    public List<Message> getAllMessages(){
        List<Message> messages = new ArrayList<Message>();
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Query all messages within the message table
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //Get results
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int messageID = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long postedAt = resultSet.getLong("time_posted_epoch");
                messages.add(new Message(messageID, postedBy, messageText, postedAt));
            }
            //Success
            return messages;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        //Fail, this point should never be reached
        return null;
    }

    /**
     * Given a messageID, this method returns the associated message from the database if it exists.
     * 
     * @param messageID
     * @return Message on success, null on fail.
     */
    public Message getMessageByID(int messageID){
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Qeury for message with a matching id
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageID);
            //Get results
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                //Success
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long postedAt = resultSet.getLong("time_posted_epoch");
                return new Message(messageID, postedBy, messageText, postedAt);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        //Message did not exist
        return null;
    }

    /**
     * Given a messageID, this method returns and deletes the associated message from the database if it exists.
     * 
     * @param messageID
     * @return Message on success, null on fail.
     */
    public Message deleteMessageByID(int messageID){
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Select the row to be deleted
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageID);
            //Get results
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                //Success
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long postedAt = resultSet.getLong("time_posted_epoch");

                //Delete row after gathering info
                sql = "DELETE FROM message WHERE message_id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, messageID);
                preparedStatement.executeUpdate();

                return new Message(messageID, postedBy, messageText, postedAt);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        //Message did not exist
        return null;
    }

    /**
     * Given a messageID and messageText, this method updates and returns the updated message in the database if it exists.
     * 
     * @param messageID
     * @param messageText must be 255 characters or less. Cannot be blank.
     * @return Updated message on success, null on fail.
     */
    public Message updateMessageByID(int messageID, String messageText){
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Update and return message with matching id
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, messageText);
            preparedStatement.setInt(2, messageID);
            //See if any rows were updated
            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows > 0){
                sql = "SELECT * FROM message WHERE message_id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, messageID);
                //Get Select results
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    int postedBy = resultSet.getInt("posted_by");
                    long postedAt = resultSet.getLong("time_posted_epoch");
                    return new Message(messageID, postedBy, messageText, postedAt);
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        //No message by this id existed
        return null;
    }

    /**
     * Given a userID this method returns a list of messages written by the provided user.
     * 
     * @param userID, must exist within the database
     * @return list of messages by specified user, null on fail.
     */
    public List<Message> getMessagesByUserID(int userID){
        List<Message> messages = new ArrayList<Message>();
        Connection connection = ConnectionUtil.getConnection();
        try{
            //Query the database for all messages written by a particular user.
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            //Get results
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int messageID = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long postedAt = resultSet.getLong("time_posted_epoch");
                messages.add(new Message(messageID, postedBy, messageText, postedAt));
            }
            //Success
            return messages;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        //Fail, this point should never be reached.
        return null;
    }
}
