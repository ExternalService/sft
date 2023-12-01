package com.nbnw.sft.common;

import net.minecraft.client.resources.I18n;

public class LangManager {
    public static final String keyCategories = "key.categories.sft";
    public static final String sleepToggle = "key.toggleSleepKey";
    // 功能开启、关闭时的公共前缀信息
    public static final String toggleCommonMessage = "toggle.common.message";
    // 功能开启时的后续信息
    public static final String toggleEnabledMessage = "toggle.enabled.message";
    // 功能关闭时的后续信息
    public static final String toggleDisabledMessage = "toggle.disabled.message";

    public static final String sleepCountMessage = "sleep.count.message";
    public static final String currentSleepPercentage = "current.sleep.percentage";
    public static final String serverThresholdPercentage = "server.threshold.percentage";

    public static String getFinalMessage(boolean newSetting) {
        //I18n.format方法将根据玩家客户端的当前语言设置自动选择正确的本地化字符串
        return I18n.format(LangManager.toggleCommonMessage) + (newSetting ? I18n.format(LangManager.toggleEnabledMessage) : I18n.format(LangManager.toggleDisabledMessage));
    }

}
