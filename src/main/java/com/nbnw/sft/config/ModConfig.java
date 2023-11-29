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
        boolean enableSeveralPlayerSleep = this.config.get(Configuration.CATEGORY_GENERAL,
                "several_player_sleep", true, "Enable whether several players sleep warp night or not").getBoolean(true);
        boolean enablePlayerLoginMessage = this.config.get(Configuration.CATEGORY_GENERAL,
                "player_login_message", true, "Enable whether show mod message to players when they login or not").getBoolean(true);
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
    public boolean isPlayerLoginMessageEnabled() {
        return this.config.get(Configuration.CATEGORY_GENERAL, "player_login_message", true).getBoolean();
    }
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ModEntry.MODID)) {
            reloadConfig();
        }
    }
}
