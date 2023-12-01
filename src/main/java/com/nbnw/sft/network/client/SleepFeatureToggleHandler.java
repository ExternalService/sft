package com.nbnw.sft.network.client;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.config.Configuration;

import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.handler.ScreenMessageHandler;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;

public class SleepFeatureToggleHandler {
    private int rgbColor = 0xE367E9;
    public void showServerResultMessage(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            if (!message.getType().equals(MessageType.SERVER_SCREEN_MESSAGE)) {
                return;
            }
            // 同步服务端信息
            boolean newSetting = message.getSleepToggle();
            ModConfig.getInstance().getConfig().get(Configuration.CATEGORY_GENERAL, "several_player_sleep", true).set(newSetting);
            ModConfig.getInstance().reloadConfig();
            ScreenMessageHandler.getInstance().showMessage(message.getScreenMessage(), message.getDuration(), rgbColor);
        }
    }
}

