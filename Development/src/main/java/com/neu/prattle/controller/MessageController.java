package com.neu.prattle.controller;

import com.neu.prattle.dto.MessageDTO;
import com.neu.prattle.exceptions.UserDoesNotExistException;
import com.neu.prattle.model.HashTag;
import com.neu.prattle.model.Message;
import com.neu.prattle.service.HashTagService;
import com.neu.prattle.service.MessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A REST controller for handling CRUD operations on Message objects.
 */
@RestController
@RequestMapping(path = "/rest/message")
@CrossOrigin(origins = {"http://com.northeastern.cs5500.team1.s3-website.us-east-2.amazonaws.com", "http://localhost:3000"})
public class MessageController {

    private MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(MessageController.class);
    private HashTagService hashTagService;

    @Autowired
    public void setHashTagService(HashTagService hashTagService) {
        this.hashTagService = hashTagService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Handles a HTTP GET request for getting the past messages between two users.
     *
     * @param firstPersonUsername  the username of the first user in the conversation.
     * @param secondPersonUsername the username of the second user in the conversation.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{firstPersonUsername}/chathistory/{secondPersonUsername}")
    public List<Message> getChatHistory(@PathVariable String firstPersonUsername,
                                        @PathVariable String secondPersonUsername) {
        try {
            logger.info("Attempting to read chat history of {} and {}", firstPersonUsername, secondPersonUsername);
            return messageService.findMessagesBetweenTwoUsers(firstPersonUsername, secondPersonUsername,
                    true);
        } catch (UserDoesNotExistException e) {
            logger.error("Either or both users do not exist");
            return new ArrayList<>();
        }
    }

    /**
     * Handles a HTTP GET request for getting the number of new messages available for a user in a conversation with
     * another user.
     *
     * @param firstPersonUsername  the username of the first user in the conversation.
     * @param secondPersonUsername the username of the second user in the conversation.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/{firstPersonUsername}/chathistory/{secondPersonUsername}/newmsgcount")
    public int getNewMessageCount(@PathVariable String firstPersonUsername,
                                  @PathVariable String secondPersonUsername) {
        try {
            logger.info("Returning new message count for {} and {}", firstPersonUsername, secondPersonUsername);
            return messageService.getNewMessageCount(firstPersonUsername, secondPersonUsername);
        } catch (UserDoesNotExistException e) {
            logger.error("Either or both users do not exist");
            return -1;
        }
    }

    /**
     * Handles a HTTP PUT request for updating the messageStatus for the given message id.
     *
     * @param messageDTO the put body that contains the new messageStatus.
     * @param messageId  the message id of the message to update.
     * @return A Response indicating the outcome of the requested operation.
     */
    @PutMapping(value = "/updatemessagestatus/{messageId}")
    public Message updateMesageStatus(@RequestBody MessageDTO messageDTO, @PathVariable int messageId) {
        logger.info("Attempting to Update messageStatus");
        return messageService.updateMessage(messageDTO, messageId);
    }

    /**
     * Handles a HTTP GET request for getting the list of messages in a conversation between two users.
     *
     * @param username1 the username of one of the users.
     * @param username2 the username of the other user.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/conversation/{username1}/{username2}")
    public List<Message> getConversationBetweenUsers(@PathVariable String username1, @PathVariable String username2) {
        logger.info("Attempting to get conversation between two users");
        try {
            return messageService.findMessagesBetweenTwoUsers(username1, username2, false);
        } catch (UserDoesNotExistException e) {
            logger.info("One or both users could not be found.");
            return new ArrayList<>();
        }
    }

    /**
     * Handles a HTTP GET request for searching for messages to and from a user that contain a hashtag.
     *
     * @param hashtag  the hashtag to search for.
     * @param username the username of the username whose messages will be returned.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/search/{hashtag}/{username}")
    public List<Message> searchMessagesUsingHashTag(@PathVariable String hashtag,
                                                    @PathVariable String username) {
        Optional<HashTag> optionalHashTag = hashTagService.getHashTag(hashtag);
        if (!optionalHashTag.isPresent()) {
            return new ArrayList<>();
        }
        HashTag hashTagObject = optionalHashTag.get();
        messageService.updateHashTagSearchHits(hashTagObject);
        return messageService.findMessagesByHashtag(hashtag, username, true);
    }

    /**
     * Handles a HTTP GET request to get the top hashtags used by all users.
     *
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/search/tophashtags")
    public List<HashTag> searchTopHashTags() {
        return hashTagService.getTopHashTags();
    }

    /**
     * Handles a HTTP GET request to get all messages in a thread that starts with a message.
     *
     *  @param sourceMessageID the id of the message to find the subsequent thread.
     * @return A Response indicating the outcome of the requested operation.
     */
    @GetMapping(value = "/thread/{sourceMessageID}")
    public List<Message> searchMessagesInThread(@PathVariable int sourceMessageID) {
        return messageService.getMessagesForThread(sourceMessageID);
    }
}
