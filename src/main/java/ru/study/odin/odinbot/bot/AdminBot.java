package ru.study.odin.odinbot.bot;

import ru.study.odin.odinbot.api.TelegramBotApi;
import ru.study.odin.odinbot.api.TelegramBotApiImpl;

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
        telegramBotApi.getMe();
    }
}
