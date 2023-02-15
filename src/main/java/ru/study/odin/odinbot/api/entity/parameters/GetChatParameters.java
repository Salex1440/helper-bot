package ru.study.odin.odinbot.api.entity.parameters;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetChatParameters {

    @SerializedName("chat_id")
    private Long chatId;

    public GetChatParameters(Long chatId) {
        this.chatId = chatId;
    }
}
