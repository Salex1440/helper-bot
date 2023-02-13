package ru.study.odin.odinbot.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private static String botToken;

    private static String name;

    private static String version;

    private static int longPollingTimeoutTelegram;

    private static int clientTelegramReadTimeoutInSeconds;

    private PropertyReader() {}

    /**
     * This method reads a property file and saves property values.
     * After calling this method it's possible to use getters properly.
     */
    public static void readProperties() {

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        botToken = appProps.getProperty("telegram.bot.token");
        name = appProps.getProperty("telegram.bot.name");
        version = appProps.getProperty("version");
        longPollingTimeoutTelegram = Integer.parseInt(appProps.getProperty("telegram.longPollingTimeout"));
        clientTelegramReadTimeoutInSeconds = Integer.parseInt(appProps.getProperty("telegram.clientReadTimeoutInSeconds"));
    }

    public static String getBotToken() {
        return botToken;
    }


    public static String getName() {
        return name;
    }

    public static String getVersion() {
        return version;
    }

    public static int getLongPollingTimeoutTelegram() {
        return longPollingTimeoutTelegram;
    }

    public static int getClientTelegramReadTimeoutInSeconds() {
        return clientTelegramReadTimeoutInSeconds;
    }

}
