package ru.study.odin.odinbot.service;

import it.tdlight.client.Result;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.bot.AdminBot;
import ru.study.odin.odinbot.tdlib.ChatMember;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TdPhacadeService {

    private static TdPhacadeService instance = null;

    private static SimpleTelegramClient client;
    private int totalCount = 0;

    private Map<Long, ChatMember> chatMembers = new HashMap<>();
    CountDownLatch latchSearchChatMembers;
    CountDownLatch latchGetChatMembers;
    CountDownLatch latchWaitUsersFill;

    private TdPhacadeService(SimpleTelegramClient client) {
        this.client = client;
    }

    public static TdPhacadeService getInstance(SimpleTelegramClient client) {
        if (instance == null) {
            instance = new TdPhacadeService(client);
        }
        return instance;
    }

    public void getInfoAboutChatMembers(long groupChatId, long responseChatId) {
        latchSearchChatMembers = new CountDownLatch(1);
        latchWaitUsersFill = new CountDownLatch(1);
        Thread threadSearchChatMembers = new Thread(new SearchChatMembers(groupChatId));
        Thread threadGet = new Thread(new GetChatMembersSync());
        Thread threadWait = new Thread(new WaitChatMembersFillInfo());
        Thread threadSendChatMembers = new Thread(new SendChatMembers(responseChatId));

        threadWait.start();
        threadGet.start();
        threadSearchChatMembers.start();
        threadSendChatMembers.start();
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
            latchWaitUsersFill.countDown();
//            System.out.println(chatMembers);
        }

        private void sendChatMembers(long chatId) {
            try {
                latchWaitUsersFill.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();

            for (ChatMember m : chatMembers.values()) {
                sb.append(String.format("first name: %s%n", m.getFirstName()));
                sb.append(String.format("last name: %s%n", m.getLastName()));
                sb.append(String.format("username: @%s%n", m.getUsername()));
                sb.append(String.format("phone number: %s%n", m.getPhoneNumber()));
                sb.append(String.format("group status: %s%n", m.getStatus()));
                sb.append(String.format("%n"));
            }

            System.out.println(sb.toString());
            sendMessage(chatId, sb.toString());
        }

    }

    private class SearchChatMembers implements Runnable {

        private long chatId;

        public SearchChatMembers(long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void run() {
            ManagerClass managerClass = new ManagerClass();
            managerClass.SearchChatMembers(chatId);
        }
    }

    private class GetChatMembersSync implements Runnable {

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

    private class SendChatMembers implements Runnable {

        private long chatId;

        public SendChatMembers(long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void run() {
            ManagerClass managerClass = new ManagerClass();
            managerClass.sendChatMembers(chatId);
        }
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
                messageResult -> {
                });
    }

    public void sendMessageFromFile(long chatId, String filename) throws IOException {
        long messageThreadId = 0;
        long replyToMessageId = 0;
        TdApi.MessageSendOptions options = null;
        TdApi.ReplyMarkup markup = null;
        boolean disableWebPagePreview = true;
        boolean clearDraft = true;

        String text = getFileContent(filename);
        TdApi.FormattedText formattedText = new TdApi.FormattedText(String.format(text), null);
        TdApi.InputMessageContent content = new TdApi.InputMessageText(formattedText, disableWebPagePreview, clearDraft);
        client.send(new TdApi.SendMessage(chatId, messageThreadId, replyToMessageId, options, markup, content),
                result -> {
                });
    }

    private String getFileContent(String filename) throws IOException {
        ClassLoader classLoader = AdminBot.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder txt = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            txt.append(line);
        }
        return txt.toString();
    }

    public Map<Long, ChatMember> getChatMembers() {
        return chatMembers;
    }

}
