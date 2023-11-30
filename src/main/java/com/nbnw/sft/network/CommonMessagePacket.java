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

    public CommonMessagePacket() {
        this.screenMessage = "default message";
        this.serverLangRequestCode = 1;
        this.duration = 5;
    }


    public CommonMessagePacket(MessageType type, String languageCode) {
        this(); // 调用无参构造
        this.type = type;
        switch (type){
            case CLIENT_LANGUAGE_CODE:
                this.clientLanguageCode = languageCode;
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
        this.type = MessageType.values()[buf.readInt()]; // 读取类型
        switch (type) {
            // 字符串类型
            case CLIENT_LANGUAGE_CODE:
                int length = buf.readInt();
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                this.clientLanguageCode = new String(bytes, StandardCharsets.UTF_8);// 从数据包中读取语言代码
                break;
            // 整数类型
            case SERVER_LANG_REQUEST_CODE:
                this.serverLangRequestCode = buf.readInt();
                break;
            case CLIENT_KEY_PRESSED_CODE:
                this.clientKeyPressedKeyCode = buf.readInt();
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
            // 整数类型
            case SERVER_LANG_REQUEST_CODE:
                buf.writeInt(serverLangRequestCode);
                break;
            case CLIENT_KEY_PRESSED_CODE:
                buf.writeInt(clientKeyPressedKeyCode);
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

    public enum MessageType {
        CLIENT_LANGUAGE_CODE, // 客户端发送自身本地化语言代码信息
        SERVER_LANG_REQUEST_CODE, // 服务端发送请求语言信息

        CLIENT_KEY_PRESSED_CODE, // 客户端发送按键被按下信息
        SERVER_SCREEN_MESSAGE, // 服务端发送客户端需要显示的信息

    }

}
