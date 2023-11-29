package com.nbnw.sft.handler;

import com.nbnw.sft.common.PlayerCountUtil;
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
        if (player.isPlayerSleeping()) {
            ScreenMessageHandler.getInstance().showMessage("","Current World Sleeping Players : " + playersInBedCount + " / " + playersInWorldCount, 1, 0xE367E9);
        }
    }
}
