package com.nbnw.sft.network.server;

import com.nbnw.sft.network.CommonMessagePacket;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

public class LoginLangResultHandler {
    public void sendChatMessageToServer(CommonMessagePacket message, MessageContext ctx){
        if (ctx.side.isServer()) {
            System.out.println("服务端收到来自客户端的信息");
            // 将每行消息存储在列表中
            List<String> messages_en = Arrays.asList(
                    "Message from sleep for tomorrow mod:",
                    "1:you can find the config file in config folder.",
                    "  minecraft\\config\\sft folder(Client) or config\\sft folder(Server)",
                    "2:Press p(Default) to enable or disable the feature.",
                    "  Enable not all players sleep in bed but also can warp the night.",
                    "3:You can disable this login message by change the config file,too"
            );
            List<String> messages_zh = Arrays.asList(
                    "来自sleep for tomorrow模组的信息:",
                    "1:你可以在这些配置文件夹中找到模组的配置文件.",
                    "  minecraft\\config\\sft(客户端) or config\\sft(服务端)",
                    "2:默认按P开启/关闭本模组的功能.",
                    "  启动不用所有玩家睡觉就可以跳过夜晚功能.",
                    "3:你也可以在配置文件中关闭本模组的登录信息显示"
            );

            // 获取发送者的玩家实体
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            // 根据语言代码选择消息
            List<String> finalMessage = (message.getClientLanguageCode().equals("zh_CN")) ? messages_zh : messages_en;
            // 发送消息给玩家
            for (String msg : finalMessage) {
                ChatComponentText chatMessage = new ChatComponentText(EnumChatFormatting.BOLD + "" +
                        EnumChatFormatting.GOLD +
                        msg +
                        EnumChatFormatting.RESET);
                player.addChatMessage(chatMessage);
            }
        }
    }
}
