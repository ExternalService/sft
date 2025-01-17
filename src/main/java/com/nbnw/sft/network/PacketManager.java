package com.nbnw.sft.network;

/**
 * 统一管理客户端和服务端之间的通信
 */

public class PacketManager {
//    private static int singleSleepKeyCode = 0; // 用于客户端按下按键切换少数人睡觉功能

    private static int serverLanguageRequestKeyCode = 1; // 用于服务端向客户端请求客户端选择的本地化语言类型功能 目前没有实际的作用 可以删除这部分相关的代码

    private static PacketManager instance;

    private PacketManager(){}

    public static PacketManager getInstance() {
        if (instance == null) {
            instance = new PacketManager();
        }
        return instance;
    }

//    public int getSingleSleepKeyCode(){
//        return this.singleSleepKeyCode;
//    }

    public int getServerLanguageRequestKeyCode(){
        return this.serverLanguageRequestKeyCode;
    }


}
