package com.nbnw.sft.network.client;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.network.CommonMessagePacket;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;

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
