package ru.study.odin.odinbot.api;

import ru.study.odin.odinbot.api.entity.Chat;
import ru.study.odin.odinbot.api.entity.Message;
import ru.study.odin.odinbot.api.entity.Update;
import ru.study.odin.odinbot.api.entity.User;

import java.util.List;

public interface TelegramBotApi {

    User getMe();

    Chat getChat(Long chatId);

    List<Update> getUpdates();

    Message sendMessage(Long chatId, String text);
}
