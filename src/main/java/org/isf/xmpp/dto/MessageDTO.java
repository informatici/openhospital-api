package org.isf.xmpp.dto;

import io.swagger.annotations.ApiModelProperty;
import org.jivesoftware.smack.MessageListener;

import javax.validation.constraints.NotNull;

public class MessageDTO {

    @NotNull
    @ApiModelProperty(notes = "listener of the message", example = "", position = 0)
    MessageListener listener;

    @NotNull
    @ApiModelProperty(notes = "text of the message", example = "Hi, how are you?", position = 1)
    String textMessage;

    @NotNull
    @ApiModelProperty(notes = "Recipient of the message", example = "John", position = 2)
    String to;

    @ApiModelProperty(notes = "Not Used", example = "John", position = 3)
    boolean visualize;

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isVisualize() {
        return visualize;
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

}
