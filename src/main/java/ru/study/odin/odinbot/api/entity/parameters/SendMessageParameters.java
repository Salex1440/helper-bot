package ru.study.odin.odinbot.api.entity.parameters;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class SendMessageParameters {

    @SerializedName("chat_id")
    private Long chatId;

    private String text;

    public SendMessageParameters(Long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

}
