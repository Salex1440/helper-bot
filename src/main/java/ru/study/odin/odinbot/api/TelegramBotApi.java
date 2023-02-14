package ru.study.odin.odinbot.api;

import ru.study.odin.odinbot.api.entity.Chat;
import ru.study.odin.odinbot.api.entity.Update;
import ru.study.odin.odinbot.api.entity.User;

import java.io.IOException;
import java.util.List;

public interface TelegramBotApi {

    User getMe();

    Chat getChat(Integer chatId);

    List<Update> getUpdates();
}
