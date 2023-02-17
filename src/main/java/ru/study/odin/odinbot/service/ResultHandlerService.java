package ru.study.odin.odinbot.service;

import it.tdlight.client.GenericResultHandler;
import it.tdlight.client.Result;
import it.tdlight.jni.TdApi;

public class ResultHandlerService {

    private static ResultHandlerService instance = null;

    private ResultHandlerService() {

    }

    public static ResultHandlerService getInstance() {
        if (instance == null) {
            instance = new ResultHandlerService();
        }
        return instance;
    }

    public void onCharMembersResult(Result<TdApi.ChatMembers> chatMembersResult) {
        TdApi.ChatMembers result = chatMembersResult.get();
        long userId = 0;
        String status = null;

        // Print the message
        for (TdApi.ChatMember member : result.members) {
            var messageSender = member.memberId;
            if (messageSender instanceof TdApi.MessageSenderUser messageSenderUser) {
                userId = messageSenderUser.userId;
            }
            var memberStatus = member.status;
            if (memberStatus instanceof TdApi.ChatMemberStatusMember) {
                status = "member";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusAdministrator) {
                status = "admin";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusBanned) {
                status = "banned";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusCreator) {
                status = "creator";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusLeft) {
                status = "left";
            } else if (memberStatus instanceof TdApi.ChatMemberStatusRestricted) {
                status = "restricted";
            }
            System.out.println("id: " + userId + "; status: " + status);
        }
    }

}
