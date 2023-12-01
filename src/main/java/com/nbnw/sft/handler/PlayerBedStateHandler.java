package com.nbnw.sft.handler;

import com.nbnw.sft.common.LangManager;
import com.nbnw.sft.common.PlayerCountUtil;
import com.nbnw.sft.config.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerBedStateHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.worldObj;
        PlayerCountUtil playerCountUtil = new PlayerCountUtil(world);
        int playersInBedCount = playerCountUtil.currentWorldSleepPlayerCount();
        int playersInWorldCount = playerCountUtil.currentWorldPlayerCount();
        String worldPercentage = String.format("%.2f%%", (double) playersInBedCount / (double) playersInWorldCount * 100);
        if (player.isPlayerSleeping()) {
            String percentage = String.format("%.2f%%", ModConfig.getInstance().getSpsThreshold() * 100);
            ScreenMessageHandler.getInstance().showMessage("",I18n.format(LangManager.sleepCountMessage) + " " +
                            playersInBedCount + " / " + playersInWorldCount +
                            I18n.format(LangManager.currentSleepPercentage) + worldPercentage ,
                            //+ I18n.format(LangManager.serverThresholdPercentage) + percentage,
                    // TODO 要正确显示百分比需要客户端从服务端读取配置文件数据，而PlayerTickEvent触发频率很高，会导致服务端和客户端进行大量的数据交互，增加服务器压力
                    // TODO 并且每当客户端和服务端修改配置都需要做同步才能保证显示的值是正确的，处理起来太麻烦，所以暂时取消这个显示的功能
                    1,
                    0xE367E9);
        }
    }
}
