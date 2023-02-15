package ru.study.odin.odinbot.api.entity.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GetUpdatesParameters {

    long offset;

    int timeout;

    public GetUpdatesParameters(long offset, int timeout) {
        this.offset = offset;
        this.timeout = timeout;
    }

}
