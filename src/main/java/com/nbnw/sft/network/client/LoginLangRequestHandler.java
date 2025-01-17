package com.nbnw.sft.network.client;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.network.CommonMessagePacket;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
/**
 * 将客户端设置的语言信息发送到服务端
 * 以便服务端能够根据客户端的语言信息返回对应语言的其它信息
 */

public class LoginLangRequestHandler {
    public void sendLangCodeToServer(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            LanguageManager languageManager = Minecraft.getMinecraft().getLanguageManager();
            Language currentLanguage = languageManager.getCurrentLanguage();
            // 发送回应包到服务器
            ModEntry.network.sendToServer(new CommonMessagePacket(CommonMessagePacket.MessageType.CLIENT_LANGUAGE_CODE, currentLanguage.getLanguageCode()));
        }
    }
}
