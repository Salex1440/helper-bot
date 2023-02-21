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
import java.util.concurrent.CountDownLatch;

public class TdPhacadeService {

    private static TdPhacadeService instance = null;

    private static SimpleTelegramClient client;
    private int totalCount = 0;

    private Map<Long, ChatMember> chatMembers = new HashMap<>();
    CountDownLatch latchSearchChatMembers;
    CountDownLatch latchSearchChatMembers2;
    CountDownLatch latchGetChatMembers;

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

        latchSearchChatMembers = new CountDownLatch(1);
        latchSearchChatMembers2 = new CountDownLatch(1);
        Thread threadSend = new Thread(new ThreadSend(chatId));
        Thread threadGet = new Thread(new ThreadGet());
        Thread threadWait = new Thread(new WaitChatMembersFillInfo());

        threadWait.start();
        threadGet.start();
        threadSend.start();

    }

    class ManagerClass {
        public void SearchChatMembers(long chatId) {
            client.send(
                    new TdApi.SearchChatMembers(chatId, null, 10, null),
                    chatMembersResult -> {
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
                        }
                        totalCount = result.totalCount;
                        latchGetChatMembers = new CountDownLatch(totalCount);
                        latchSearchChatMembers.countDown();

                    });
        }

        public void getChatMembersSync() {

            try {
                latchSearchChatMembers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Map.Entry<Long, ChatMember> chatMember : chatMembers.entrySet()) {

                client.send(new TdApi.GetUser(chatMember.getKey()),
                        userResult -> {
                            TdApi.User user = userResult.get();
                            ChatMember member = chatMembers.get(user.id);
                            member.setFirstName(user.firstName);
                            member.setLastName(user.lastName);
                            member.setPhoneNumber(user.phoneNumber);
                            member.setUsername(user.usernames.editableUsername);
                            chatMembers.put(user.id, member);
                            latchGetChatMembers.countDown();
                        }
                );
            }
        }

        public void waitChatMembersFillInfo() {
            try {
                latchSearchChatMembers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                latchGetChatMembers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(chatMembers);
        }

    }

    private class ThreadSend implements Runnable {

        private long chatId;

        public ThreadSend(long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void run() {
            ManagerClass managerClass = new ManagerClass();
            managerClass.SearchChatMembers(chatId);
        }
    }

    private class ThreadGet implements Runnable {

        @Override
        public void run() {
            ManagerClass managerClass = new ManagerClass();
            managerClass.getChatMembersSync();
        }

    }

    private class WaitChatMembersFillInfo implements Runnable {
        @Override
        public void run() {
            ManagerClass managerClass = new ManagerClass();
            managerClass.waitChatMembersFillInfo();
        }
    }


    private void onGetUserResult(Result<TdApi.User> userResult) {
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
    }

    private void onGetUserFullInfoResult(Result<TdApi.UserFullInfo> userFullInfoResult) {
        TdApi.UserFullInfo userFullInfo = userFullInfoResult.get();
        TdApi.BotInfo botInfo = userFullInfo.botInfo;
        System.out.println("isBot: " + (botInfo != null));
    }

    public void sendMessage(long chatId, String text) {
        long messageThreadId = 0;
        long replyToMessageId = 0;
        TdApi.MessageSendOptions options = null;
        TdApi.ReplyMarkup markup = null;
        boolean disableWebPagePreview = true;
        boolean clearDraft = true;
        TdApi.FormattedText formattedText = new TdApi.FormattedText(text, null);
        TdApi.InputMessageContent content = new TdApi.InputMessageText(formattedText, disableWebPagePreview, clearDraft);
        client.send(new TdApi.SendMessage(chatId, messageThreadId, replyToMessageId, options, markup, content),
                messageResult -> {});
    }


    public Map<Long, ChatMember> getChatMembers() {
        return chatMembers;
    }

}
