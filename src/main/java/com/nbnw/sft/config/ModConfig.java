package com.nbnw.sft.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

import com.nbnw.sft.ModEntry;

public class ModConfig {
    private static String singlePlayerSleepName = "several_player_sleep";
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
    public String getSinglePlayerSleepName(){
        return this.singlePlayerSleepName;
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
        loadConfiguration();
    }
    private void loadConfiguration() {
        // read config file.if not exist,then create it with the default settings
        String language = this.config.get(Configuration.CATEGORY_GENERAL,
                "language", "english", "Language setting").getString();
        // 用于配置文件版本控制  TODO：如果检测到配置文件版本不一致 重新生成配置文件 或者删除以前的配置文件中无效的项并新增以前的配置文件没有的项.为了实现这个功能，需要一个类来记录当前版本的配置项列表
        String configVersion = this.config.get(Configuration.CATEGORY_GENERAL,
                "config_version", ModEntry.metadata.version, "Mod config file version").getString();
        boolean enableSeveralPlayerSleep = this.config.get(Configuration.CATEGORY_GENERAL,
                "several_player_sleep", true, "Enable whether several players sleep warp night or not").getBoolean(true);
        // several player sleep feature threshold. default 0.5
        double spsThreshold = (long) this.config.get(Configuration.CATEGORY_GENERAL,
                "sps_threshold", 0.5, "Several players sleep warp night sleeping player percentage(0.0-1.0), default 0.5").getDouble(0.5);
        boolean enablePlayerLoginMessage = this.config.get(Configuration.CATEGORY_GENERAL,
                "login_message", true, "Enable whether show mod message to players when they login or not(Use server side config)").getBoolean(true);
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
        return this.config.get(Configuration.CATEGORY_GENERAL, "language", "english").getString();
    }
    public boolean isSinglePlayerSleepEnabled() {
        return this.config.get(Configuration.CATEGORY_GENERAL, "several_player_sleep", true).getBoolean();
    }

    public double getSpsThreshold() {
        return this.config.get(Configuration.CATEGORY_GENERAL, "sps_threshold", 0.5).getDouble(0.5);
    }

    public boolean isPlayerLoginMessageEnabled() {
        return this.config.get(Configuration.CATEGORY_GENERAL, "login_message", true).getBoolean();
    }
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModEntry.MODID)) {
            reloadConfig();
        }
    }
}
