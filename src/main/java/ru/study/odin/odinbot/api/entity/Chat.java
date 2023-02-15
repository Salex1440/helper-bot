package ru.study.odin.odinbot.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class Chat {
    private Long id;

    private String type;

    private String title;

    private String username;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("is_forum")
    private Boolean isForum;

//    private ChatPhoto photo;

    @SerializedName("active_usernames")
    private List<String> activeUsernames;

    @SerializedName("emoji_status_custom_emoji_id")
    private String emojiStatusCustomEmojiId;

    private String bio;

    @SerializedName("has_private_forwards")
    private Boolean hasPrivateForwards;

    @SerializedName("has_restricted_voice_and_video_messages")
    private Boolean hasRestrictedVoiceAndVideoMessages;

    @SerializedName("join_to_send_messages")
    private Boolean joinToSendMessages;

    @SerializedName("join_by_request")
    private Boolean joinByRequest;

    private String description;

    @SerializedName("invite_link")
    private String inviteLink;

    @SerializedName("pinned_message")
    private Message pinnedMessage;

//    private ChatPermissions permissions;

    @SerializedName("slow_mode_delay")
    private Integer slowModeDelay;

    @SerializedName("message_auto_delete_time")
    private Integer messageAutoDeleteTime;

    @SerializedName("has_aggressive_anti_spam_enabled")
    private Boolean hasAggressiveAntiSpamEnabled;

    @SerializedName("has_hidden_members")
    private Boolean hasHiddenMembers;

    @SerializedName("has_protected_content")
    private Boolean hasProtectedContent;

    @SerializedName("sticker_set_name")
    private String stickerSetName;

    @SerializedName("can_set_sticker_set")
    private Boolean canSetStickerSet;

    @SerializedName("linked_chat_id")
    private Integer linkedChatId;

//    private ChatLocation location;

    public Chat() {
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
