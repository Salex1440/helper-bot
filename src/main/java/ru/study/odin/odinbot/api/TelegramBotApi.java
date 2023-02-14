package ru.study.odin.odinbot.api;

import ru.study.odin.odinbot.api.entity.Chat;
import ru.study.odin.odinbot.api.entity.User;

import java.io.IOException;

public interface TelegramBotApi {

    User getMe();

    Chat getChat(String chatId);
}
