package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class User {

    private long id;

    @SerializedName("is_bot")
    private boolean isBot;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String username;

    @SerializedName("language_code")
    private String languageCode;

    @SerializedName("can_join_groups")
    private boolean canJoinGroups;

    @SerializedName("can_read_all_group_messages")
    private boolean canReadAllGroupMessages;

    @SerializedName("supports_inline_queries")
    private boolean supportsInlineQueries;

    public User() {
    }

    public User(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", isBot=" + isBot +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", canJoinGroups=" + canJoinGroups +
                ", canReadAllGroupMessages=" + canReadAllGroupMessages +
                ", supportsInlineQueries=" + supportsInlineQueries +
                '}';
    }
}
