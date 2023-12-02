package com.nbnw.sft.network.client;

import com.nbnw.sft.common.LangManager;
import com.nbnw.sft.common.PlayerCountUtil;
import com.nbnw.sft.handler.ScreenMessageHandler;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ThresholdResultHandler {
    private static final int  rgbColor = 0xE367E9;
    public void showThresholdResultMessage(CommonMessagePacket message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            if (!message.getType().equals(MessageType.SERVER_THRESHOLD_PERCENTAGE_VALUE)) {
                return;
            }
            //World world = (World) Minecraft.getMinecraft().theWorld;
            //PlayerCountUtil playerCountUtil = new PlayerCountUtil(world);
            //int playersInBedCount = playerCountUtil.currentWorldSleepPlayerCount();
            //int playersInWorldCount = world.playerEntities.size();
//            String worldPercentage = String.format("%.2f%%", (double) playersInBedCount / (double) playersInWorldCount * 100);
            String thresholdResultMessage = message.getServerThresholdPercentageValue() + "%";
            ScreenMessageHandler.getInstance().showMessage("", //I18n.format(LangManager.sleepCountMessage) + " " +
                            //playersInBedCount + " / " + playersInWorldCount +
                            //I18n.format(LangManager.currentSleepPercentage) + worldPercentage +
                            I18n.format(LangManager.serverThresholdPercentage) + thresholdResultMessage,
                    1,
                    rgbColor);
        }
    }
}
