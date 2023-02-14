package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Update {

    @SerializedName("update_id")
    private long updateId;

    private Message message;

    @SerializedName("edited_message")
    private Message editedMessage;

    @SerializedName("channel_post")
    private Message channelPost;

    @SerializedName("edited_channel_post")
    private Message editedChannelPost;

    @SerializedName("callback_query")
    private CallbackQuery callbackQuery;

    public Update() {
    }

    public Update(long updateId) {
        this.updateId = updateId;
    }

}
