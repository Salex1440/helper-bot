package ru.study.odin.odinbot.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.study.odin.odinbot.api.entity.Chat;

@Getter
@Setter
@ToString
public class ChatResponse extends BaseResponse {

    @SerializedName("result")
    private Chat chat;

}
