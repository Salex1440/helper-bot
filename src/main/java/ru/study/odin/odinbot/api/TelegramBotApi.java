package ru.study.odin.odinbot.api;

import ru.study.odin.odinbot.api.dto.Chat;
import ru.study.odin.odinbot.api.dto.User;

public interface TelegramBotApi {

    User getMe();

    Chat getChat(String chatId);
}
