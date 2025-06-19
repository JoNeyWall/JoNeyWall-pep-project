package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;

import Service.SocialMediaService;

/**
 * You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    SocialMediaService socialMediaService;

    public SocialMediaController(){
        this.socialMediaService = new SocialMediaService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::userLoginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIDHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIDHander);
        app.patch("/messages/{message_id}", this::updateMessageByIDHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByUserIDHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    /**
     * This handles the post /register endpoint for account creation
     * 
     * @param context
     * @throws JsonProcessingException
     */
    private void createAccountHandler(Context context) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account addedAccount = socialMediaService.createAccount(account);
        if (addedAccount == null){
            context.status(400);
        }
        else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }

    /**
     * This handles the post /login endpoint for account login.
     * @param context
     * @throws JsonProcessingException
     */
    private void userLoginHandler(Context context) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account userAccount = socialMediaService.userLogin(account);
        if (userAccount == null){
            context.status(401);
        }
        else {
            context.json(mapper.writeValueAsString(userAccount));
        }
    }

    /**
     * This handles the post /messages endpoint for message creation
     * @param context
     * @throws JsonProcessingException
     */
    private void createMessageHandler(Context context) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        Message addedMessage = socialMediaService.createMessage(message);
        if (addedMessage == null){
            context.status(400);
        }
        else{
            context.json(mapper.writeValueAsString(addedMessage));
        }
    }

    /**
     * This handles the get /messages endpoint for retrieving all existing messages
     * 
     * @param context
     * @throws JsonProcessingException
     */
    private void getAllMessagesHandler(Context context){
        List<Message> messages = socialMediaService.getAllMessages();
        if (messages == null){
            //This should never happen
            context.status(400);
        }
        else{
            context.json(messages);
        }
    }

    /**
     * This handles the get /messages/{message_id} endpoint for retrieving a message by its id
     * 
     * @param context
     */
    private void getMessageByIDHandler(Context context){
        Message message = socialMediaService.getMessageByID(Integer.parseInt(context.pathParam("message_id")));
        if (message == null){
            context.status(200);
        }
        else{
            context.json(message);
        }
    }

    /**
     * This handles the delete /messages/{message_id} endpoint for deleting a message by its id
     * 
     * @param context
     */
    private void deleteMessageByIDHander(Context context){
        Message message = socialMediaService.deleteMessageByID(Integer.parseInt(context.pathParam("message_id")));
        if (message == null){
            context.status(200);
        }
        else{
            context.json(message);
        }
    }

    /**
     * This handles the patch /messages/{message_id} endpoint for updateing a message by its id
     * @param context
     * @throws JsonProcessingException
     */
    private void updateMessageByIDHandler(Context context) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        Message updatedMessage = socialMediaService.updateMessageByID(message, Integer.parseInt(context.pathParam("message_id")));
        if (updatedMessage == null){
            context.status(400);
        }
        else{
            context.json(mapper.writeValueAsString(updatedMessage));
        }
    }

    private void getAllMessagesByUserIDHandler(Context context){
        List<Message> messages = socialMediaService.getMessagesByUserID(Integer.parseInt(context.pathParam("account_id")));
        if (messages == null){
            context.status(200);
        }
        else{
            context.json(messages);
        }
    }

}