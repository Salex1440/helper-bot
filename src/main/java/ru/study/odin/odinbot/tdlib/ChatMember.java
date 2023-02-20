package ru.study.odin.odinbot.tdlib;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
public class ChatMember {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String username;

    private String status;

    @Override
    public String toString() {
        return "ChatMember{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status +
                '}';
    }
}
