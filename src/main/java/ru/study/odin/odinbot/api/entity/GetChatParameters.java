package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetChatParameters {

    @SerializedName("chat_id")
    private Integer chatId;

    public GetChatParameters(Integer chatId) {
        this.chatId = chatId;
    }
}
