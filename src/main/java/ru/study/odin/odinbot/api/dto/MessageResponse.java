package ru.study.odin.odinbot.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.study.odin.odinbot.api.entity.Message;

@Getter
@Setter
@ToString
public class MessageResponse extends BaseResponse {

    @SerializedName("result")
    private Message message;

}
