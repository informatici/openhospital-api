package org.isf.xmpp.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.Authorization;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.xmpp.dto.FileDTO;
import org.isf.xmpp.dto.MessageDTO;
import org.isf.xmpp.manager.Interaction;
import org.isf.xmpp.service.Server;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.isf.shared.mapper.OHModelMapper.getObjectMapper;

@RestController
@Api(value = "/xmpp", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class XmppController {

    private final Logger logger = LoggerFactory.getLogger(XmppController.class);

    @Autowired
    protected Interaction interaction;

    public XmppController(Interaction interaction) {
        this.interaction = interaction;
    }

    /**
     * Return the chat manager
     *
     * @return NO_CONTENT if there aren't chat manager, ChatManager otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/getChatManager", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatManager> getChatManager() {
        logger.info("Get chat manager");
        ChatManager chatManager = interaction.getChatManager();
        if (chatManager == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(chatManager);
        }
    }

    /**
     * Return the connection
     *
     * @return NO_CONTENT if there aren't connection, Connection otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/getConnection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Connection> getConnection() {
        logger.info("Get connection");
        Connection connection = interaction.getConnection();
        if (connection == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(connection);
        }
    }

    /**
     * Return the roster
     *
     * @return NO_CONTENT if there aren't roster, Roster otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/getRoster", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Roster> getRoster() {
        logger.info("Get roster");
        Roster roster = interaction.getRoster();
        if (roster == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(roster);
        }
    }

    /**
     * Return the roster
     *
     * @return NO_CONTENT if there aren't server, Server otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/getServer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Server> getServer() {
        logger.info("Get server");
        Server server = interaction.getServer();
        if (server == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(server);
        }
    }

    /**
     * Send file
     *
     * @param fileDTO object with file and others metadata
     * @return Error if there are some problem with the creation of the file, ok otherwise
     * @throws OHServiceException
     */
    @PostMapping(value = "/sendFile",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity sendFile(@RequestBody FileDTO fileDTO) throws OHServiceException {
        logger.info("Send file user: " + fileDTO.getUser() + ", file: " + fileDTO.getFilename());
        File file = new File("/tmp/" + fileDTO.getFilename());
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new OHAPIException(new OHExceptionMessage(null, "Problem uploading file", OHSeverityLevel.ERROR));
        }
        try {
            fileOutputStream.write(fileDTO.getBlobFile());
        } catch (IOException e) {
            throw new OHAPIException(new OHExceptionMessage(null, "Problem uploading file", OHSeverityLevel.ERROR));
        }
        interaction.sendFile(fileDTO.getUser(), file, fileDTO.getDescription());
        return ResponseEntity.ok(null);
    }

    /**
     * Return user from email address
     *
     * @param address
     * @return Error if the email address is not formatted correctly, user String otherwise
     * @throws OHServiceException
     */
    @GetMapping(value = "/userFromAddress/{address}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> userFromAddress(@PathVariable String address) throws OHServiceException {
        logger.info("User from address");
        String user = interaction.userFromAddress(address);
        if (user == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Problem with the address ", OHSeverityLevel.ERROR));
        }
        return ResponseEntity.ok(user);

    }

    /**
     * Get the online contacts
     * @return NO_CONTENT if there aren't contacts online, List<String> otherwise
     */
    @GetMapping(value = "/getContactOnline", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getContactOnline() {
        logger.info("Get contacts online");
        Collection<String> contacts = interaction.getContactOnline();
        List<String> listContacts = contacts.stream().map(contact -> getObjectMapper().map(contact, String.class)).collect(Collectors.toList());
        if (listContacts.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listContacts);
        }
    }

    /**
     * Send message
     * @param messageDTO object containing the text message
     * @return ok if there aren't problems
     */
    @PostMapping(value = "/sendMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendMessage(@RequestBody MessageDTO messageDTO) {
        logger.info("Send message to: " + messageDTO.getTo());
        interaction.sendMessage(messageDTO.getListener(), messageDTO.getTextMessage(), messageDTO.getTo(),
                messageDTO.isVisualize());
        return ResponseEntity.ok(null);
    }
}
