package ru.study.odin.odinbot;

import ru.study.odin.odinbot.bot.AdminBot;
import ru.study.odin.odinbot.utils.PropertyReader;

public class OdinBotApplication {

    public static void main(String[] args) {

        System.out.println("Hello, Bot!");
        PropertyReader.readProperties();

        AdminBot.getInstance();
    }

}
