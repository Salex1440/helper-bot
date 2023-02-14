package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CallbackQuery {

    private String id;

    private User from;

    private Message message;

    @SerializedName("inline_message_id")
    private String inlineMessageId;

    @SerializedName("chat_instance")
    private String chatInstance;

    private String data;

    @SerializedName("game_short_name")
    private String gameShortName;

    public CallbackQuery() {
    }

}
