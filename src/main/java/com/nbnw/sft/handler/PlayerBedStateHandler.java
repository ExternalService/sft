package com.nbnw.sft.handler;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.common.LangManager;
import com.nbnw.sft.common.PlayerCountUtil;
import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerBedStateHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player.isPlayerSleeping()) {
            // 从服务端获取配置文件中的百分比阈值 TODO 要正确显示百分比需要客户端从服务端读取配置文件数据，而PlayerTickEvent触发频率很高，会导致服务端和客户端进行大量的数据交互，增加服务器压力
            ModEntry.network.sendToServer(new CommonMessagePacket(MessageType.CLIENT_THRESHOLD_REQUEST_CODE, 0));

            World world = (World) Minecraft.getMinecraft().theWorld;
            PlayerCountUtil playerCountUtil = new PlayerCountUtil(world);
            int playersInBedCount = playerCountUtil.currentWorldSleepPlayerCount();
            int playersInWorldCount = world.playerEntities.size();
            String worldPercentage = String.format("%.2f%%", (double) playersInBedCount / (double) playersInWorldCount * 100);
            // 显示睡眠统计
            ShowSleepPlayerCount.getInstance().showMessage(I18n.format(LangManager.sleepCountMessage) + " " +
                    playersInBedCount + " / " + playersInWorldCount +
                    I18n.format(LangManager.currentSleepPercentage) + worldPercentage,
                    1,
                    ModConfig.getInstance().getMessageColor());
        }
    }
}
