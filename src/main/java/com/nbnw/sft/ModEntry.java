package com.nbnw.sft;

import com.nbnw.sft.config.ModConfig;
import com.nbnw.sft.handler.*;
import com.nbnw.sft.handler.command.CommandSFT;
import com.nbnw.sft.network.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ModEntry.MODID)
public class ModEntry
{
    public static final String MODID = "sft";
    public static final String VERSION = "1.4.0";
    @Mod.Metadata
    public static ModMetadata metadata;

    public static SimpleNetworkWrapper network;

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSFT()); // 注册玩家指令
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModConfig.getInstance().init(event);
        MinecraftForge.EVENT_BUS.register(ModConfig.getInstance());
        // 初始化网络通道
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        // 注册数据包和处理器 0和1是该数据包通道的唯一标识 每个不同类型的数据包都应该有它唯一的标识
        // 四个参数：1 消息处理器类 2 数据包类 3 数据包通道 4 该数据包该在哪一边处理
        network.registerMessage(NetworkMessageDispatcher.class, CommonMessagePacket.class, 0, Side.CLIENT);
        network.registerMessage(NetworkMessageDispatcher.class, CommonMessagePacket.class, 1, Side.SERVER);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ModConfig.getInstance().checkAndResetConfigIfNeeded(); // 检查配置文件版本号并根据需要重置配置文件
        MinecraftForge.EVENT_BUS.register(ScreenMessageHandler.getInstance()); // 注册屏幕显示信息事件实例
        MinecraftForge.EVENT_BUS.register(ShowSleepPlayerCount.getInstance()); // 注册睡眠统计显示信息事件实例
        FMLCommonHandler.instance().bus().register(new PlayerLoginEventHandler()); // 玩家登陆事件
        MinecraftForge.EVENT_BUS.register(new PlayerLoginEventHandler()); // 玩家登陆事件
        MinecraftForge.EVENT_BUS.register(new PlayerSleepEventHandler()); // 玩家睡觉事件
        FMLCommonHandler.instance().bus().register(new PlayerBedStateHandler()); // 显示睡觉玩家比例
    }
}
