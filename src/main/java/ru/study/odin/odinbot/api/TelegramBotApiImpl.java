package ru.study.odin.odinbot.api;

import com.google.gson.Gson;
import okhttp3.*;
import ru.study.odin.odinbot.api.dto.ChatResponse;
import ru.study.odin.odinbot.api.dto.GetUpdatesResponse;
import ru.study.odin.odinbot.api.dto.UserResponse;
import ru.study.odin.odinbot.api.entity.*;
import ru.study.odin.odinbot.utils.PropertyReader;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TelegramBotApiImpl implements TelegramBotApi {

    private final OkHttpClient client;

    private static final String url = "https://api.telegram.org/bot";

    private final String token;

    private long nextUpdateId = 0;

    private final int longPollingTelegramTimeout;

    //region singleton implementation
    private static TelegramBotApiImpl instance = null;

    private TelegramBotApiImpl() {
        token = PropertyReader.getBotToken();
        longPollingTelegramTimeout = PropertyReader.getLongPollingTimeoutTelegram();
        int clientTelegramReadTimeoutInSeconds = PropertyReader.getClientTelegramReadTimeoutInSeconds();
        client = new OkHttpClient.Builder()
                .readTimeout(clientTelegramReadTimeoutInSeconds, TimeUnit.SECONDS)
                .build();
    }

    public static TelegramBotApiImpl getInstance() {
        if (instance == null) {
            instance = new TelegramBotApiImpl();
        }
        return instance;
    }

    @Override
    public User getMe() {
        Response response = sendRequest("getMe", null);
        UserResponse userResponse = null;
        if (!response.isSuccessful()) {
            System.out.println("Bot not found!!! Check bot token is correct in app.properties!");
            throw new RuntimeException("No bot found!");
        }
        Gson gson = new Gson();
        try {
            userResponse = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), UserResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userResponse == null ? null : userResponse.getUser();
    }

    @Override
    public List<Update> getUpdates() {

        GetUpdatesParameters params = new GetUpdatesParameters(nextUpdateId, longPollingTelegramTimeout);
        Response response = sendRequest("getUpdates", params);
        GetUpdatesResponse updatesResponse = null;
        try {
            Gson gson = new Gson();
            updatesResponse = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), GetUpdatesResponse.class);
            nextUpdateId = updatesResponse.updates().stream()
                    .mapToLong(u -> u.getUpdateId() + 1)
                    .max()
                    .orElse(nextUpdateId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        GetUpdatesResponse getUpdatesResponse = null;
        try {
            getUpdatesResponse = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), GetUpdatesResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getUpdatesResponse == null ? null : getUpdatesResponse.updates();
    }

    @Override
    public Chat getChat(Integer chatId) {
        GetChatParameters parameters = new GetChatParameters(chatId);
        Response response = sendRequest("getChat", parameters);
        ChatResponse chatResponse = null;
        Gson gson = new Gson();
        try {
            chatResponse = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), ChatResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatResponse.getChat();
    }

    private Response sendRequest(String method, Object params) {
        String fullUrl = url + token + "/" + method;
        Request request;
        if (params == null) {
            request = new Request.Builder()
                    .url(fullUrl)
                    .build();
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(params);
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
            request = new Request.Builder()
                    .url(fullUrl)
                    .post(requestBody)
                    .build();
        }

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
