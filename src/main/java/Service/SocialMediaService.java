package Service;

import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;

import java.util.*;

/**
 * This class provides access to the SocialMediaDAO and handles any "business logic".
 * The business logic will mostly check if inputs are valid to seperate that concern from the DAO.
 */
public class SocialMediaService {
    SocialMediaDAO socialMediaDAO;

    /**
     * Basic constructor to create the social media DAO.
     */
    public SocialMediaService(){
        this.socialMediaDAO = new SocialMediaDAO();
    }

    /**
     * Constructe allowing for a DAO to be provided instead of created.
     * This allows us to mock the socialMediaDAO for testing purposes.
     * 
     * @param socialMediaDAO
     */
    public SocialMediaService(SocialMediaDAO socialMediaDAO){
        this.socialMediaDAO = socialMediaDAO;
    }

    /**
     * Checks input to ensure validity, then updates the database using the DAO.
     * 
     * @param Account to be added
     * @return Account on success, null on fail.
     */
    public Account createAccount(Account account){
        //Don't perform the actions of this method if the password doesn't meet the required length.
        if (account.getUsername().isBlank() || account.getPassword().length() < 4){
            return null;
        }
        return socialMediaDAO.createAccount(account.getUsername(), account.getPassword());
    }

    /**
     * Checks input to ensure validity, returns account if it exists.
     * 
     * @param Account to be added
     * @return Account on success, null on fail.
     */
    public Account userLogin(Account account){
        //Don't perform the actions on known bad input
        if (account.getUsername().isBlank() || account.getPassword().length() < 4){
            return null;
        }
        return socialMediaDAO.userLogin(account.getUsername(), account.getPassword());
    }

    /**
     * Creates a message on the database using the DAO.
     * 
     * @param Message to be added
     * @return Message on success, null on fail.
     */
    public Message createMessage(Message message){
        //Message must be 255 characters or less.
        if (message.getMessage_text().length() > 255 || message.getMessage_text().isBlank()){
            return null;
        }
        return socialMediaDAO.createMessage(message.getMessage_text(), message.getPosted_by(), message.getTime_posted_epoch());
    }

    /**
     * Returns all messages currently existing within the database.
     * 
     * @return list of messages on success.
     */
    public List<Message> getAllMessages(){
        return socialMediaDAO.getAllMessages();
    }

    /**
     * Returns message matching the provided id if it exists.
     * 
     * @param messageID must exist within the database
     * @return Message on success, null on fail.
     */
    public Message getMessageByID(int messageID){
        return socialMediaDAO.getMessageByID(messageID);
    }

    /**
     * Deletes a message givin its id.
     * 
     * @param messageID must exist within the database
     * @return Message on success, null on fail.
     */
    public Message deleteMessageByID(int messageID){
        return socialMediaDAO.deleteMessageByID(messageID);
    }

    /**
     * Updates a message in the database given the messageID is exists and the messageText meets requirements.
     * 
     * @param Message containing new text and id to identify which message to update
     * @return Message on success, null on fail.
     */
    public Message updateMessageByID(Message message, int messageID){
        //Message must be 255 characters or less.
        if (message.getMessage_text().length() > 255 || message.getMessage_text().isBlank()){
            return null;
        }
        return socialMediaDAO.updateMessageByID(messageID, message.getMessage_text());
    }

    /**
     * Given the user exists within the database, return all messages posted by them.
     * 
     * @param userID must exist within the database
     * @return list of messages on success, null on fail
     */
    public List<Message> getMessagesByUserID(int userID){
        return socialMediaDAO.getMessagesByUserID(userID);
    }
}
