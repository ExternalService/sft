package com.nbnw.sft.network.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.config.Configuration;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.PacketManager;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;

public class SleepFeatureMessageHandler {
    public void sendScreenMessageToClient(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            if (!message.getType().equals(MessageType.CLIENT_KEY_PRESSED_CODE)) {
                return;
            }
            int key = message.getClientKeyPressedCode();
            // 在服务器的主线线程中执行配置更改和消息发送
            // 当服务端收到按键数据包后，发送一个屏幕消息数据包给所有玩家
            // 切换配置并保存
            if(key == PacketManager.getInstance().getSingleSleepKeyCode()) {
                boolean newSetting = !ModConfig.getInstance().isSinglePlayerSleepEnabled();
                ModConfig.getInstance().getConfig().get(Configuration.CATEGORY_GENERAL, "several_player_sleep", true).set(newSetting);
                ModConfig.getInstance().reloadConfig();
                IMessage screenMessagePacket = new CommonMessagePacket(MessageType.SERVER_SCREEN_MESSAGE, "", 5, newSetting);
                // 发送给所有在线的客户端
                ModEntry.network.sendToAll(screenMessagePacket);
            }
        }
    }
}
