package ru.study.odin.odinbot.service;

import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.bot.AdminBot;
import ru.study.odin.odinbot.tdlib.BotAuthenticationData;
import ru.study.odin.odinbot.tdlib.ChatMember;

import java.util.HashMap;
import java.util.Map;

public class TdPhacadeService {

    private static TdPhacadeService instance = null;

    private static SimpleTelegramClient client;

    private Map<Long, ChatMember> chatMembers = new HashMap<>();

    private TdPhacadeService(SimpleTelegramClient client) {
        this.client = client;
    }

    public static TdPhacadeService getInstance(SimpleTelegramClient client) {
        if (instance == null) {
            instance = new TdPhacadeService(client);
        }
        return instance;
    }

    public void getInfoAboutChatMembers(long chatId) {
        GenericResultHandler<TdApi.ChatMembers> onChatMembersResult = this::onChatMembersResult;
        client.send(
                new TdApi.SearchChatMembers(chatId, null, 10, null),
                onChatMembersResult);
    }

    public void onChatMembersResult(Result<TdApi.ChatMembers> chatMembersResult) {
        chatMembers.clear();
        TdApi.ChatMembers result = chatMembersResult.get();

        for (TdApi.ChatMember member : result.members) {
            long userId = 0;
            String status = null;
            var messageSender = member.memberId;
            if (messageSender instanceof TdApi.MessageSenderUser messageSenderUser) {
                userId = messageSenderUser.userId;
            }
            var memberStatus = member.status;
            if (memberStatus instanceof TdApi.ChatMemberStatusMember) {
                status = "member";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusAdministrator) {
                status = "admin";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusBanned) {
                status = "banned";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusCreator) {
                status = "creator";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusLeft) {
                status = "left";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusRestricted) {
                status = "restricted";
            }
            chatMembers.put(userId, ChatMember.builder().id(userId).status(status).build());

            GenericResultHandler<TdApi.User> onGetUserResult = this::onGetUserResult;
            client.send(
                    new TdApi.GetUser(userId),
                    onGetUserResult);

        }
    }

    public void onGetUserResult(Result<TdApi.User> userResult) {
        TdApi.User user = userResult.get();
        long id = user.id;

        String firstName = user.firstName;
        String lastName = user.lastName;
        String phoneNumber = user.phoneNumber;
        String username = user.usernames.editableUsername;

        ChatMember chatMember = chatMembers.get(id);
        chatMember.setFirstName(firstName);
        chatMember.setLastName(lastName);
        chatMember.setPhoneNumber(phoneNumber);
        chatMember.setUsername(username);

        chatMembers.put(id, chatMember);

        System.out.println(chatMember);
    }

    public void onGetUserFullInfoResult(Result<TdApi.UserFullInfo> userFullInfoResult) {
        TdApi.UserFullInfo userFullInfo = userFullInfoResult.get();
        TdApi.BotInfo botInfo = userFullInfo.botInfo;
        System.out.println("isBot: " + (botInfo != null));
    }

    public Map<Long, ChatMember> getChatMembers() {
        return chatMembers;
    }
}
