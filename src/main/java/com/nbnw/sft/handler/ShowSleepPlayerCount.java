package com.nbnw.sft.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
public class ShowSleepPlayerCount {
    private static ShowSleepPlayerCount instance = null;
    private String sleepCountMessage = "";
    private long displayTime = 0;
    private long startTime = 0;
    private int rgbColor = 0xFFFFFF;
    private ShowSleepPlayerCount() {
    }
    public static ShowSleepPlayerCount getInstance() {
        if (instance == null) {
            instance = new ShowSleepPlayerCount();
        }
        return instance;
    }
    public void showMessage(String message, int displaySeconds) {
        if(!message.equals("")){
            this.sleepCountMessage = message;
        }
        this.displayTime = (long) displaySeconds * 1000; // 转换为毫秒时间
        this.startTime = System.currentTimeMillis();
    }
    public void showMessage(String message, int displaySeconds, int color) {
        showMessage(message, displaySeconds);
        this.rgbColor = color;
    }

    @SubscribeEvent
    public void onRenderGameTipsOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (System.currentTimeMillis() - startTime < displayTime && !sleepCountMessage.isEmpty()) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int serverMessageWidth = fontRenderer.getStringWidth(sleepCountMessage);
            int x = (screenWidth - serverMessageWidth) / 2;
            int y = screenHeight / 8;
            fontRenderer.drawString(sleepCountMessage, x, y, this.rgbColor);
        }
    }
}
