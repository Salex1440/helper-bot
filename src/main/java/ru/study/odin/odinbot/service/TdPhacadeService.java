package ru.study.odin.odinbot.service;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.tdlib.ChatMember;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        GenericResultHandler<TdApi.ChatMembers> onSearchChatMembers = this::onSearchChatMembers;
//        client.send(
//                new TdApi.SearchChatMembers(chatId, null, 10, null),
//                onSearchChatMembers);

//        client.send(
//                new TdApi.GetChatHistory(chatId, 0, 10, 10, false),
//                this::onGetChatHistory);
//
//        client.send(
//                new TdApi.GetChatStatistics(chatId, false),
//                this::onGetChatStatistics
//        );

//        client.send(
//                new TdApi.SearchChatMessages(chatId, null, null, 0, 0, 10, null, 0),
//                this::onSearchChatMessages
//        );


        TdApi.ChatList chatList = new TdApi.ChatListMain();
        client.send(
                new TdApi.GetChats(null, 10),
                this::onGetChats
        );

        System.out.println();

    }

    private void onGetChats(Result<TdApi.Chats> chatsResult) {
        TdApi.Chats chats = chatsResult.get();
    }

    private void onSearchChatMessages(Result<TdApi.FoundChatMessages> foundChatMessagesResult) {
        TdApi.FoundChatMessages messages = foundChatMessagesResult.get();
    }

    public void onSearchChatMembers(Result<TdApi.ChatMembers> chatMembersResult) {
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

            client.send(
                    new TdApi.GetUser(userId),
                    this::onGetUserResult);
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
