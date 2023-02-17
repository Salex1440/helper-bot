package ru.study.odin.odinbot.bot;

import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import ru.study.odin.odinbot.service.ResultHandlerService;
import ru.study.odin.odinbot.tdlib.BotAuthenticationData;

public class AdminBot implements Bot {

    private static final TdApi.MessageSender ADMIN_ID = new TdApi.MessageSenderUser(242832690);

    private static SimpleTelegramClient client;
    private static AdminBot instance = null;
    private AuthenticationData authenticationData;

    private static ResultHandlerService resultHandlerService;

    private AdminBot() {
        resultHandlerService = ResultHandlerService.getInstance();
    }

    public static AdminBot getInstance() {
        if (instance == null) {
            instance = new AdminBot();
        }
        return instance;
    }

    @Override
    public void run() {

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

//        Chat chat = telegramBotApi.getChat(-1001242056182L);
        System.out.println();
    }

    private void initBot() throws CantLoadLibrary {
        Init.start();

        // Obtain the API token
        var apiToken = APIToken.example();

        // Configure the client
        var settings = TDLibSettings.create(apiToken);

        // Create a client
        client = new SimpleTelegramClient(settings);

        // Configure the authentication info
        authenticationData = new BotAuthenticationData();
//        var authenticationData = AuthenticationData.consoleLogin();

        // Add an example update handler that prints when the bot is started
        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, AdminBot::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        client.addUpdateHandler(TdApi.UpdateNewMessage.class, AdminBot::onUpdateNewMessage);

        // Add an example command handler that stops the bot
        client.addCommandHandler("stop", new AdminBot.StopCommandHandler());
    }

    /**
     * Print new messages received via updateNewMessage
     */
    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {

        client.send(
                new TdApi.SearchChatMembers(update.message.chatId, null, 10, null),
                resultHandlerService::onCharMembersResult);

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

}
