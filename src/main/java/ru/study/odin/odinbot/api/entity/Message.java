package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message {

    @SerializedName("message_id")
    private long messageId;

    private User from;

    @SerializedName("sender_chat")
    private Chat senderChat;

    private long date;

    private Chat chat;

    private String text;

    public Message() {
    }

    public Message(long messageId) {
        this.messageId = messageId;
    }

}
