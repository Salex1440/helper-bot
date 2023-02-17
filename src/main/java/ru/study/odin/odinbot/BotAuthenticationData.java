package ru.study.odin.odinbot;

import it.tdlight.client.AuthenticationData;
import ru.study.odin.odinbot.utils.PropertyReader;

public final class BotAuthenticationData implements AuthenticationData {

    private final String botToken;

    public BotAuthenticationData() {
        botToken = PropertyReader.getBotToken();
    }

    @Override
    public boolean isQrCode() {
        return false;
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public long getUserPhoneNumber() {
        return 0;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
