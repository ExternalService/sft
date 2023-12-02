package com.nbnw.sft.network.server;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;
import com.nbnw.sft.network.PacketManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.config.Configuration;

public class ThresholdRequestHandler {
    public void sendConfigThresholdToClient(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            if (!message.getType().equals(MessageType.CLIENT_THRESHOLD_REQUEST_CODE)) {
                return;
            }
            String thresholdPercentage = "" + ModConfig.getInstance().getSpsThreshold() * 100;
            IMessage messagePacket = new CommonMessagePacket(MessageType.SERVER_THRESHOLD_PERCENTAGE_VALUE, thresholdPercentage);
            // 发送给所有在线的客户端
            ModEntry.network.sendToAll(messagePacket);
        }
    }
}
