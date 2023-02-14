package ru.study.odin.odinbot.api.dto;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    private boolean ok;

    @SerializedName("error_code")
    private int errorCode;

    private String description;

    public BaseResponse() {
    }

    public boolean isOk() {
        return ok;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "ok=" + ok +
                ", errorCode=" + errorCode +
                ", description='" + description + '\'' +
                '}';
    }
}
