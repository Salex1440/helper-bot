package ru.study.odin.odinbot.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import ru.study.odin.odinbot.api.entity.User;

@Getter
@Setter
public class UserResponse extends BaseResponse {

    @SerializedName("result")
    private User user;
}
