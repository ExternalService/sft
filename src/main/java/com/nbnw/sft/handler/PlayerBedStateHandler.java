package com.nbnw.sft.handler;

import com.nbnw.sft.common.PlayerCountUtil;
import com.nbnw.sft.config.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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
            ScreenMessageHandler.getInstance().showMessage("",
                    "Current World Sleeping data: " +
                            playersInBedCount + " / " + playersInWorldCount + ". Client percentage: " + worldPercentage + " Server threshold: " + percentage,
                    1,
                    0xE367E9);
        }
    }
}
