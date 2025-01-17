package com.nbnw.sft.handler;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 屏幕展示提示信息单例
 * 这个方法显示的信息会对所有玩家可见
 * TODO 大多数bug由这个类引起
 */
public class ScreenMessageHandler {
    private static ScreenMessageHandler instance = null;
    private String serverMessage = "";
    // 百分比信息
    private String thresholdMessage = "";
    // 显示时长
    private long displayTime = 0;
    private long startTime = 0;
    private int rgbColor = 0xFFFFFF;
    private ScreenMessageHandler() {
    }
    public static ScreenMessageHandler getInstance() {
        if (instance == null) {
            instance = new ScreenMessageHandler();
        }
        return instance;
    }
    public void showMessage(String serverMessage, String thresholdMessage,int displaySeconds) {
        if(!serverMessage.equals("")){
            this.serverMessage = serverMessage;
        }
        if(!thresholdMessage.equals("")){
            this.thresholdMessage = thresholdMessage;
        }
        this.displayTime = (long) displaySeconds * 1000; // 转换为毫秒时间
        this.startTime = System.currentTimeMillis();
    }
    public void showMessage(String serverMessage, String thresholdMessage, int displaySeconds, int color) {
        showMessage(serverMessage, thresholdMessage, displaySeconds);
        this.rgbColor = color;
    }

    @SubscribeEvent
    public void onRenderGameTipsOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (System.currentTimeMillis() - startTime < displayTime && (!serverMessage.isEmpty() || !thresholdMessage.isEmpty())) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int serverMessageWidth = fontRenderer.getStringWidth(serverMessage);
            int sleepMessageWidth = fontRenderer.getStringWidth(thresholdMessage);
            // 指定消息显示位置
            int x = (screenWidth - serverMessageWidth) / 2;
            int y = screenHeight / 2;
            // 字体背景框 第五个参数是颜色(ARGB类型)
            // Gui.drawRect(x - 2, y - 2, x + serverMessageWidth + 2, y + fontRenderer.FONT_HEIGHT + 2, 0xAA000000); // Optional: draw a background rectangle
            fontRenderer.drawString(serverMessage, x, y, this.rgbColor);
            y = y / 7;
            fontRenderer.drawString(thresholdMessage, x, y, this.rgbColor);
        }
    }
}
