package ru.study.odin.odinbot.bot;

import ru.study.odin.odinbot.api.TelegramBotApi;
import ru.study.odin.odinbot.api.TelegramBotApiImpl;
import ru.study.odin.odinbot.api.entity.Chat;
import ru.study.odin.odinbot.api.entity.Update;
import ru.study.odin.odinbot.api.entity.User;

import java.util.List;

public class AdminBot implements Bot {

    private static final TelegramBotApi telegramBotApi = TelegramBotApiImpl.getInstance();

    private static AdminBot instance = null;

    private AdminBot() {
    }

    public static AdminBot getInstance() {
        if (instance == null) {
            instance = new AdminBot();
        }
        return instance;
    }

    @Override
    public void run() {
        User user  = telegramBotApi.getMe();
        System.out.println(user.getFirstName() + " is bot: " + user.isBot());

        while (!Thread.interrupted()) {
            List<Update> updates = telegramBotApi.getUpdates();
            for (Update update : updates) {
                Long chatId = update.getMessage().getChat().getId();
                String text = "Meow";
//                telegramBotApi.sendMessage(chatId, text);
            }
        }

//        Chat chat = telegramBotApi.getChat(-1001242056182L);
        System.out.println();
    }
}
