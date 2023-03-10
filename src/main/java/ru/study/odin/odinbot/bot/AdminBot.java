package ru.study.odin.odinbot.bot;

import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.service.TdPhacadeService;
import ru.study.odin.odinbot.tdlib.BotAuthenticationData;

import java.io.IOException;
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
    private static final Set<Long> waitingChatMembersResponseUsers = new HashSet<>();
    private static final Map<String, Long> savedChats = new HashMap<>();

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
        client.addCommandHandler("start", new AdminBot.StartCommandHandler());
        client.addCommandHandler("stop", new AdminBot.StopCommandHandler());
        client.addCommandHandler("help", new AdminBot.HelpCommandHandler());
        client.addCommandHandler("chat_members", new AdminBot.ChatMembersCommandHandler());

    }

    private static void onUpdateChatMember(TdApi.UpdateChatMember update) {
        long chatId = update.chatId;
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
        long requestChatId = update.message.chatId;
        TdApi.MessageContent messageContent = update.message.content;
        String messageText = null;
        if (messageContent instanceof TdApi.MessageText text) {
            messageText = text.text.text;
        }
        assert messageText != null;
        if (messageText.startsWith("/")) return;

        Long userId = null;
        TdApi.MessageSender messageSender = update.message.senderId;
        if (messageSender instanceof TdApi.MessageSenderUser user) {
            userId = user.userId;
        }

        if (waitingChatMembersResponseUsers.contains(userId)) {
            if (savedChats.containsKey(messageText)) {
                long chatId = savedChats.get(messageText);
                tdPhacadeService.getInfoAboutChatMembers(chatId, requestChatId, messageText);
                System.out.println("User(" + userId + ") received list of users.");
            } else {
                String filename = "txt/unknown_group_message.txt";
                try {
                    tdPhacadeService.sendMessageFromFile(requestChatId, filename);
                    System.out.println("User(" + userId + ") couldn't get list of users.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            waitingChatMembersResponseUsers.remove(userId);
        } else if (userId == myId) {
            // Do nothing...
        }

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

    private static class StartCommandHandler implements CommandHandler {
        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            long chatId = chat.id;
            String filename = "txt/start_message.txt";
            try {
                tdPhacadeService.sendMessageFromFile(chatId, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (commandSender instanceof TdApi.MessageSenderUser user) {
                long userId = user.userId;
                System.out.println("User(" + userId + ") started bot.");
            }
        }
    }

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
            String filename = "txt/info_message.txt";
            try {
                tdPhacadeService.sendMessageFromFile(chatId, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (commandSender instanceof TdApi.MessageSenderUser user) {
                long userId = user.userId;
                System.out.println("User(" + userId + ") asked help.");
            }
        }
    }

    private static class ChatMembersCommandHandler implements CommandHandler {

        @Override
        public void onCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
            long chatId = chat.id;
            String filename = "txt/chat_members_enter.txt";
            try {
                tdPhacadeService.sendMessageFromFile(chatId, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (commandSender instanceof TdApi.MessageSenderUser user) {
                waitingChatMembersResponseUsers.add(user.userId);
                System.out.println("User(" + user.userId + ") called chat_members.");
            }
        }
    }

}
