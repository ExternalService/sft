package com.nbnw.sft.handler;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 屏幕展示提示信息单例
 * 这个方法显示的信息会对所有玩家可见
 * TODO 对这个类需要根本上的修改，现在大多数bug都由这个类引起
 */
public class ScreenMessageHandler {
    private static ScreenMessageHandler instance = null;
    private String serverMessage = "";

    private String sleepMessage = "";
    private long displayTime = 0;
    private long startTime = 0;
    private int rgbColor = 0xFFFFFF;
    private ScreenMessageHandler() {
        // TODO 临时解决不切换功能开启和关闭就不会在玩家睡觉时显示功能是否开启的bug 这种方式不能保证客户端和服务端真实的信息一致
        // this.serverMessage = LangManager.getFinalMessage(ModConfig.getInstance().isPlayerLoginMessageEnabled()); // 不能使用这个方法,因为I18n类是客户端独有的
    }
    public static ScreenMessageHandler getInstance() {
        if (instance == null) {
            instance = new ScreenMessageHandler();
        }
        return instance;
    }
    public void showMessage(String serverMessage, String sleepMessage,int displaySeconds) {
        if(!serverMessage.equals("")){
            this.serverMessage = serverMessage;
        }
        if(!sleepMessage.equals("")){
            this.sleepMessage = sleepMessage;
        }
        this.displayTime = (long) displaySeconds * 1000; // 转换为毫秒时间
        this.startTime = System.currentTimeMillis();
    }
    public void showMessage(String serverMessage, String sleepMessage, int displaySeconds, int color) {
        showMessage(serverMessage, sleepMessage, displaySeconds);
        this.rgbColor = color;
    }

    @SubscribeEvent
    public void onRenderGameTipsOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (System.currentTimeMillis() - startTime < displayTime && (!serverMessage.isEmpty() || !sleepMessage.isEmpty())) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int serverMessageWidth = fontRenderer.getStringWidth(serverMessage);
            int sleepMessageWidth = fontRenderer.getStringWidth(sleepMessage);

            int x = (screenWidth - serverMessageWidth) / 2;
            int y = screenHeight / 2;
            // 字体背景框 第五个参数是颜色(ARGB类型)
            // Gui.drawRect(x - 2, y - 2, x + serverMessageWidth + 2, y + fontRenderer.FONT_HEIGHT + 2, 0xAA000000); // Optional: draw a background rectangle
            fontRenderer.drawString(serverMessage, x, y, this.rgbColor);
            x = (screenWidth - sleepMessageWidth) / 2;
            fontRenderer.drawString(sleepMessage, x, y / 7, this.rgbColor);
        }
    }
}
