package com.nbnw.sft.network.client;

import com.nbnw.sft.common.LangManager;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.config.Configuration;

import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.handler.ScreenMessageHandler;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;


/**
 * 切换模组功能的开启和关闭
 * 由于可以将阈值设为1来关闭功能，所以这个类变得没有意义，也没有实际使用，将来可能会移除
 */
@Deprecated
public class SleepFeatureToggleHandler {
    private static final int  rgbColor = 0xE367E9;
    public void showServerResultMessage(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            if (!message.getType().equals(MessageType.SERVER_SCREEN_MESSAGE)) {
                return;
            }
            // 同步服务端信息
            boolean newSetting = message.getSleepToggle();
            ModConfig.getInstance().getConfig().get(Configuration.CATEGORY_GENERAL, "several_player_sleep", true).set(newSetting);
            ModConfig.getInstance().reloadConfig();
            String finalMessage = LangManager.getFinalMessage(message.getSleepToggle());
            ScreenMessageHandler.getInstance().showMessage(finalMessage, "" , message.getDuration(), rgbColor);
        }
    }
}

