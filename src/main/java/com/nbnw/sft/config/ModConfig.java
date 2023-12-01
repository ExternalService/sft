package com.nbnw.sft.config;

import com.nbnw.sft.network.ConfigSyncPacket;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

import com.nbnw.sft.ModEntry;

public class ModConfig {
    private static final String LANGUAGE = "language";
    private static final String  SEVERAL_PLAYER_SLEEP = "several_player_sleep";
    private static final String CONFIG_VERSION = "config_version";

    private static final String SPS_THRESHOLD = "sps_threshold";
    private static final String LOGIN_MESSAGE = "login_message";

    private Configuration config;
    // 保存单例实例
    private static ModConfig instance;
    // 私有构造器
    private ModConfig() {
    }
    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }
    public Configuration getConfig() {
        return this.config;
    }
    public void init(FMLPreInitializationEvent event) {
        File modConfigDir = new File(event.getModConfigurationDirectory(), ModEntry.metadata.modId);
        modConfigDir.mkdirs();
        File configFile = new File(modConfigDir, "sft_config.cfg");
        // create mod config file
        this.config = new Configuration(configFile);
        loadAndSyncConfig();
    }

    // 加载和同步配置
    public void loadAndSyncConfig() {
        // 加载配置
        loadConfiguration();

        // 同步到所有客户端
        syncConfigToClients();
    }

    // TODO 将配置数据同步到所有客户端
    private void syncConfigToClients() {
        // 封装配置数据并发送到所有客户端
        // 使用Forge网络包系统进行通信
    }

    // TODO 客户端接收配置数据后，调用此方法同步配置
    public void onConfigSyncPacketReceived(ConfigSyncPacket packet) {
        // 更新客户端的配置实例
    }

    private void loadConfiguration() {
        // read config file.if not exist,then create it with the default settings
        String language = this.config.get(Configuration.CATEGORY_GENERAL,
                LANGUAGE, "english", "Language setting").getString();
        // 用于配置文件版本控制
        String configVersion = this.config.get(Configuration.CATEGORY_GENERAL,
                CONFIG_VERSION, ModEntry.metadata.version, "Mod config file version").getString();
        boolean enableSeveralPlayerSleep = this.config.get(Configuration.CATEGORY_GENERAL,
                SEVERAL_PLAYER_SLEEP, true, "Enable whether several players sleep warp night or not").getBoolean(true);
        // several player sleep feature threshold. default 0.5
        double spsThreshold = (long) this.config.get(Configuration.CATEGORY_GENERAL,
                SPS_THRESHOLD, 0.5, "Several players sleep warp night sleeping player percentage(0.0-1.0), default 0.5").getDouble(0.5);
        boolean enablePlayerLoginMessage = this.config.get(Configuration.CATEGORY_GENERAL,
                LOGIN_MESSAGE, true, "Enable whether show mod message to players when they login or not(Use server side config)").getBoolean(true);
        // if the configs has changed by players, save the changes
        if (this.config.hasChanged()) {
            this.config.save();
        }
    }
    /**
     * After player change configs, call this function to reload them.
     */
    public void reloadConfig() {
        if (this.config.hasChanged()) {
            this.config.save();
        }
        this.config.load(); // reload the config file
    }
    public String getLanguage() {
        return this.config.get(Configuration.CATEGORY_GENERAL, LANGUAGE, "english").getString();
    }
    public String getConfigVersion(){
        return this.config.get(Configuration.CATEGORY_GENERAL, CONFIG_VERSION, "").getString();
    }
    public boolean isSinglePlayerSleepEnabled() {
        return this.config.get(Configuration.CATEGORY_GENERAL, SEVERAL_PLAYER_SLEEP, true).getBoolean();
    }
    public double getSpsThreshold() {
        return this.config.get(Configuration.CATEGORY_GENERAL, SPS_THRESHOLD, 0.5).getDouble(0.5);
    }
    public boolean isPlayerLoginMessageEnabled() {
        return this.config.get(Configuration.CATEGORY_GENERAL, LOGIN_MESSAGE, true).getBoolean();
    }

    /**
     * 检查配置文件存储的版本号信息是否和模组一致
     * @return true: 一致, false: 不一致
     */
    public boolean versionCheck() {
        if(getConfigVersion().equals(ModEntry.metadata.version)){ // 版本号一致
            return true;
        }
        return false; // 不一致
    }
    /**
     * 如果不一致则重置配置文件
     */
    public void checkAndResetConfigIfNeeded() {
        this.config.load(); // 首先加载配置文件
        // 检查配置文件的版本
        String configVersion = this.config.get(Configuration.CATEGORY_GENERAL, CONFIG_VERSION, ModEntry.metadata.version).getString();
        // 如果配置文件的版本与模组版本不一致，则重置配置
        if (!versionCheck()) {
            resetConfiguration();
            // TODO：目前检测到版本号不一致会直接重新生成配置文件 但是对玩家不友好，玩家需要重新改配置，可以考虑删除以前的配置文件中无效的项并新增以前的配置文件没有的项.为了实现这个功能，需要一个类来记录当前版本的配置项列表
        } else {
            // 如果版本匹配，则正常加载配置
            loadConfiguration();
        }
    }

    /**
     * 重置配置文件
     */
    private void resetConfiguration() {
        // Reset all the configuration values to their defaults
        this.config.get(Configuration.CATEGORY_GENERAL, SEVERAL_PLAYER_SLEEP, true).set(true);
        this.config.get(Configuration.CATEGORY_GENERAL, SPS_THRESHOLD, 0.5).set(0.5);
        this.config.get(Configuration.CATEGORY_GENERAL, LOGIN_MESSAGE, true).set(true);
        this.config.get(Configuration.CATEGORY_GENERAL, LANGUAGE, "english").set("english");
        this.config.get(Configuration.CATEGORY_GENERAL, CONFIG_VERSION, ModEntry.metadata.version).set(ModEntry.metadata.version);

        // Save the reset configuration
        if (this.config.hasChanged()) {
            this.config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModEntry.MODID)) {
            reloadConfig();
        }
    }
}
