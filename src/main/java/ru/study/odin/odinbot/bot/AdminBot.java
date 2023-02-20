package ru.study.odin.odinbot.bot;

import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.service.TdPhacadeService;
import ru.study.odin.odinbot.tdlib.BotAuthenticationData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdminBot implements Bot {

    private static final TdApi.MessageSender ADMIN_ID = new TdApi.MessageSenderUser(242832690);

    private static SimpleTelegramClient client;
    private static AdminBot instance = null;
    private AuthenticationData authenticationData;

    private static TdPhacadeService tdPhacadeService;

    private static long myId;
    private static Set<Long> waitingChatMembersResponseUsers = new HashSet<>();
    private static Map<String, Long> savedChats = new HashMap<>();

    private AdminBot() {
        try {
            initBot();
        } catch (CantLoadLibrary e) {
            e.printStackTrace();
        }

        // Start the client
        client.start(authenticationData);

        // Wait for exit
        try {
            client.waitForExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static AdminBot getInstance() {
        if (instance == null) {
            instance = new AdminBot();
        }
        return instance;
    }

    private void initBot() throws CantLoadLibrary {
        Init.start();

        // Obtain the API token
        var apiToken = APIToken.example();

        // Configure the client
        var settings = TDLibSettings.create(apiToken);

        // Create a client
        client = new SimpleTelegramClient(settings);

        tdPhacadeService = TdPhacadeService.getInstance(client);

        // Configure the authentication info
        authenticationData = new BotAuthenticationData();
//        var authenticationData = AuthenticationData.consoleLogin();

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, AdminBot::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        client.addUpdateHandler(TdApi.UpdateNewMessage.class, AdminBot::onUpdateNewMessage);

        client.addUpdateHandler(TdApi.UpdateChatMember.class, AdminBot::onUpdateChatMember);

        // Add an example command handler that stops the bot
        client.addCommandHandler("stop", new AdminBot.StopCommandHandler());
        client.addCommandHandler("help", new AdminBot.HelpCommandHandler());
        client.addCommandHandler("chat_members", new AdminBot.ChatMembersCommandHandler());

    }

    private static void onUpdateChatMember(TdApi.UpdateChatMember update) {
        long chatId = update.chatId;
        TdApi.MessageSender memberId = update.newChatMember.memberId;
        client.send(new TdApi.GetChat(chatId),
                chatResult -> {
                    TdApi.Chat chat = chatResult.get();
                    savedChats.put(chat.title, chatId);
                    System.out.println("Chat title: " + chat.title);
                });
    }

    /**
     * Print new messages received via updateNewMessage
     */
    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {

        TdApi.MessageContent messageContent = update.message.content;
        String messageText = null;
        if (messageContent instanceof TdApi.MessageText text) {
            messageText = text.text.text;
        }
        if (messageText.startsWith("/")) return;

        long chatId = update.message.chatId;
        long messageThreadId = 0;
        long replyToMessageId = 0;
        TdApi.MessageSendOptions options = null;
        TdApi.ReplyMarkup markup = null;
        boolean disableWebPagePreview = true;
        boolean clearDraft = true;

        Long userId = null;
        TdApi.MessageSender messageSender = update.message.senderId;
        if (messageSender instanceof TdApi.MessageSenderUser user) {
            userId = user.userId;
        }

        if (waitingChatMembersResponseUsers.contains(userId)) {
            String text = "Вывод списка участников группы";
            TdApi.FormattedText formattedText = new TdApi.FormattedText(text, null);
            TdApi.InputMessageContent content = new TdApi.InputMessageText(formattedText, disableWebPagePreview, clearDraft);
            client.send(new TdApi.SendMessage(chatId, messageThreadId, replyToMessageId, options, markup, content),
                    result -> {});
            waitingChatMembersResponseUsers.remove(userId);
            System.out.println(text);
        } else if (userId == myId) {
            // Do nothing...
            System.out.println("My message");
        }

//        tdPhacadeService.getInfoAboutChatMembers(chatId);
    }

    /**
     * Print the bot status
     */
    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        var authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }

        client.send(new TdApi.GetMe(),
                result -> {
                    TdApi.User user = result.get();
                    myId = user.id;
                    System.out.println("My id: " + myId);
                });
    }

    /**
     * Check if the command sender is admin
     */
    private static boolean isAdmin(TdApi.MessageSender sender) {
        return sender.equals(ADMIN_ID);
    }

    /**
     * Close the bot if the /stop command is sent by the administrator
     */
    private static class StopCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            // Check if the sender is the admin
            if (isAdmin(commandSender)) {
                // Stop the client
                System.out.println("Received stop command. closing...");
                client.sendClose();
            }
        }
    }

    private static class HelpCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            long chatId = chat.id;
            long messageThreadId = 0;
            long replyToMessageId = 0;
            TdApi.MessageSendOptions options = null;
            TdApi.ReplyMarkup markup = null;

            String text = "Help message";
            TdApi.FormattedText formattedText = new TdApi.FormattedText(text, null);
            boolean disableWebPagePreview = true;
            boolean clearDraft = true;
            TdApi.InputMessageContent content = new TdApi.InputMessageText(formattedText, disableWebPagePreview, clearDraft);
            client.send(new TdApi.SendMessage(chatId, messageThreadId, replyToMessageId, options, markup, content),
                    result -> {
                    });
        }
    }

    private static class ChatMembersCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            long chatId = chat.id;
            long messageThreadId = 0;
            long replyToMessageId = 0;
            TdApi.MessageSendOptions options = null;
            TdApi.ReplyMarkup markup = null;

            String text = "Введите название/уникальный идентификатор/пригласительную ссылку группы";
            TdApi.FormattedText formattedText = new TdApi.FormattedText(text, null);
            boolean disableWebPagePreview = true;
            boolean clearDraft = true;
            TdApi.InputMessageContent content = new TdApi.InputMessageText(formattedText, disableWebPagePreview, clearDraft);
            client.send(new TdApi.SendMessage(chatId, messageThreadId, replyToMessageId, options, markup, content),
                    result -> {
                        if (commandSender instanceof TdApi.MessageSenderUser user) {
                            waitingChatMembersResponseUsers.add(user.userId);
                        }
                    });
        }
    }

}
