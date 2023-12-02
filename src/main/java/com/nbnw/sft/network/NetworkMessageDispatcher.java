package com.nbnw.sft.network;


import com.nbnw.sft.network.client.ThresholdResultHandler;
import com.nbnw.sft.network.server.ThresholdRequestHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import com.nbnw.sft.network.client.LoginLangRequestHandler;
import com.nbnw.sft.network.client.SleepFeatureToggleHandler;
import com.nbnw.sft.network.server.LoginLangResultHandler;
import com.nbnw.sft.network.server.SleepFeatureMessageHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/**
 * 消息分发.根据消息类型，发送给不同的消息处理类进行处理
 */
public class NetworkMessageDispatcher implements IMessageHandler<CommonMessagePacket, IMessage> {

    private final LoginLangRequestHandler loginLangRequestHandler = new LoginLangRequestHandler();
    private final LoginLangResultHandler loginLangResultHandler = new LoginLangResultHandler();
    private final SleepFeatureToggleHandler sleepFeatureToggleHandler = new SleepFeatureToggleHandler();
    private final SleepFeatureMessageHandler sleepFeatureMessageHandler = new SleepFeatureMessageHandler();
    private final ThresholdRequestHandler thresholdRequestHandler = new ThresholdRequestHandler();
    private final ThresholdResultHandler thresholdResultHandler = new ThresholdResultHandler();
    @Override
    public IMessage onMessage(CommonMessagePacket message, MessageContext ctx) {
        switch (message.getType()){
            case CLIENT_LANGUAGE_CODE:
                loginLangResultHandler.sendChatMessageToServer(message, ctx);
                break;
            case SERVER_LANG_REQUEST_CODE:
                loginLangRequestHandler.sendLangCodeToServer(message, ctx);
                break;
            case CLIENT_KEY_PRESSED_CODE:
                sleepFeatureMessageHandler.sendScreenMessageToClient(message, ctx);
                break;
            case SERVER_SCREEN_MESSAGE:
                sleepFeatureToggleHandler.showServerResultMessage(message, ctx);
                break;
            case CLIENT_THRESHOLD_REQUEST_CODE:
                thresholdRequestHandler.sendConfigThresholdToClient(message, ctx);
                break;
            case SERVER_THRESHOLD_PERCENTAGE_VALUE:
                thresholdResultHandler.showThresholdResultMessage(message,ctx);
                break;
        }
        return null;
    }
}
