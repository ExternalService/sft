package com.nbnw.sft.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.nbnw.sft.ModEntry;
import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.network.CommonMessagePacket;
import com.nbnw.sft.network.PacketManager;
import com.nbnw.sft.network.CommonMessagePacket.MessageType;

public class PlayerLoginEventHandler {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!ModConfig.getInstance().isPlayerLoginMessageEnabled()){
            return;
        }
        EntityPlayer player = event.player;
        ModEntry.network.sendTo(new CommonMessagePacket(MessageType.SERVER_LANG_REQUEST_CODE, PacketManager.getInstance().getServerLanguageRequestKeyCode()), (EntityPlayerMP)player);
    }

}
