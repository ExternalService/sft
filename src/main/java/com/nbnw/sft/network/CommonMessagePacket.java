package com.nbnw.sft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

/**
 * 统一管理消息包
 * 在使用时不需要用到所有的属性 根据消息类型初始化不同数据来使用
 * 在需要添加新的消息类型时，尝试看是否可以使用已有的属性
 * 不能通用则添加新的属性
 */
public class CommonMessagePacket implements IMessage {
    private MessageType type; // 消息类型

    private String clientLanguageCode; // 客户端向服务端发送语言代码 用于服务端向客户端发送要求返回客户端语言代码时

    private int serverLangRequestCode; // 服务端向客户端请求本地化语言信息代码

    private int clientKeyPressedKeyCode; // 客户端向服务端发送按键代码

    // 下面三个属性用于服务端想客户端发送要显示的消息
    private String screenMessage; // 消息内容
    private int duration; // 消息时长
    private boolean sleepToggle; // 睡眠特性是否开启

    private int clientThresholdRequestCode; // 客户端请求返回睡眠百分比代码

    private String serverThresholdPercentageValue; // 服务端返回的睡眠百分比数据

    public CommonMessagePacket() {
        this.screenMessage = "default message";
        this.serverLangRequestCode = 1;
        this.duration = 5;
    }


    public CommonMessagePacket(MessageType type, String message) {
        this(); // 调用无参构造
        this.type = type;
        switch (type){
            case CLIENT_LANGUAGE_CODE:
                this.clientLanguageCode = message;
                break;
            case SERVER_THRESHOLD_PERCENTAGE_VALUE:
                this.serverThresholdPercentageValue = message;
                break;
        }
    }
    public CommonMessagePacket(MessageType type, int code) {
        this();
        this.type = type;
        switch (type){
            case SERVER_LANG_REQUEST_CODE:
                this.serverLangRequestCode = code;
                break;
            case CLIENT_KEY_PRESSED_CODE:
                this.clientKeyPressedKeyCode = code;
                break;
            case CLIENT_THRESHOLD_REQUEST_CODE:
                this.clientThresholdRequestCode = code;
                break;
        }
    }
    public CommonMessagePacket(MessageType type, String message, int numCode, boolean flag) {
        this();
        this.type = type;
        switch (type){
            case SERVER_SCREEN_MESSAGE:
                this.screenMessage = message;
                this.duration = numCode;
                this.sleepToggle = flag;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // 确保ByteBuf中有足够的字节可供读取
        if (buf.readableBytes() < 4) {
            throw new RuntimeException("ByteBuf does not contain enough data");
        }
        this.type = MessageType.values()[buf.readInt()]; // 读取类型
        switch (type) {
            // 字符串类型
            case CLIENT_LANGUAGE_CODE:
                int length = buf.readInt();
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                this.clientLanguageCode = new String(bytes, StandardCharsets.UTF_8);// 从数据包中读取语言代码
                break;
            case SERVER_THRESHOLD_PERCENTAGE_VALUE:
                int valueLength = buf.readInt();
                byte[] valueBytes = new byte[valueLength];
                buf.readBytes(valueBytes);
                this.serverThresholdPercentageValue = new String(valueBytes, StandardCharsets.UTF_8);// 从数据包中读取百分比
                break;
            // 整数类型
            case SERVER_LANG_REQUEST_CODE:
                this.serverLangRequestCode = buf.readInt();
                break;
            case CLIENT_KEY_PRESSED_CODE:
                this.clientKeyPressedKeyCode = buf.readInt();
                break;
            case CLIENT_THRESHOLD_REQUEST_CODE:
                this.clientThresholdRequestCode = buf.readInt();
                break;
            // 组合类型
            case SERVER_SCREEN_MESSAGE:
                int msgLength = buf.readInt();
                byte[] msgBytes = new byte[msgLength];
                buf.readBytes(msgBytes);
                this.screenMessage = new String(msgBytes, StandardCharsets.UTF_8);
                this.duration = buf.readInt();
                this.sleepToggle = buf.readBoolean();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal()); // 写入类型
        switch (type) {
            // 字符串类型
            case CLIENT_LANGUAGE_CODE:
                byte[] bytes = clientLanguageCode.getBytes(StandardCharsets.UTF_8); // 将语言代码写入数据包
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                break;
            case SERVER_THRESHOLD_PERCENTAGE_VALUE:
                byte[] valueBytes = serverThresholdPercentageValue.getBytes(StandardCharsets.UTF_8); // 将语言代码写入数据包
                buf.writeInt(valueBytes.length);
                buf.writeBytes(valueBytes);
                break;
            // 整数类型
            case SERVER_LANG_REQUEST_CODE:
                buf.writeInt(serverLangRequestCode);
                break;
            case CLIENT_KEY_PRESSED_CODE:
                buf.writeInt(clientKeyPressedKeyCode);
                break;
            case CLIENT_THRESHOLD_REQUEST_CODE:
                buf.writeInt(clientThresholdRequestCode);
                break;
            // 组合类型
            case SERVER_SCREEN_MESSAGE:
                byte[] messageBytes = screenMessage.getBytes(StandardCharsets.UTF_8);
                buf.writeInt(messageBytes.length);
                buf.writeBytes(messageBytes);
                buf.writeInt(duration);
                buf.writeBoolean(sleepToggle);
                break;
        }
    }

    public MessageType getType(){
        return type;
    }

    public String getClientLanguageCode(){
        return clientLanguageCode;
    }
    public int getServerLangRequestCode() {
        return serverLangRequestCode;
    }

    public int getClientKeyPressedCode() {
        return clientKeyPressedKeyCode;
    }
    public String getScreenMessage() {
        return screenMessage;
    }

    public int getDuration() {
        return duration;
    }

    public boolean getSleepToggle(){
        return sleepToggle;
    }

    public int getClientThresholdRequestCode(){
        return clientThresholdRequestCode;
    }

    public String getServerThresholdPercentageValue() {
        return serverThresholdPercentageValue;
    }

    public enum MessageType {
        CLIENT_LANGUAGE_CODE, // 客户端发送自身本地化语言代码信息
        SERVER_LANG_REQUEST_CODE, // 服务端发送请求语言信息
        CLIENT_KEY_PRESSED_CODE, // 客户端发送按键被按下信息
        SERVER_SCREEN_MESSAGE, // 服务端发送客户端需要显示的信息

        CLIENT_THRESHOLD_REQUEST_CODE, // 客户端请求返回百分比阈值信息
        SERVER_THRESHOLD_PERCENTAGE_VALUE // 服务端配置文件睡眠百分比
    }

}
