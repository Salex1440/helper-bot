package ru.study.odin.odinbot.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;
import ru.study.odin.odinbot.api.entity.Update;

import java.util.List;

@ToString
public class GetUpdatesResponse  extends BaseResponse {

    @SerializedName("result")
    private List<Update> updates;

    public GetUpdatesResponse() {
    }

    public List<Update> updates() {
        return updates;
    }
}
