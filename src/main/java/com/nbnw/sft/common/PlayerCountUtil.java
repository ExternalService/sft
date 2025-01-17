package com.nbnw.sft.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 统计指定世界中的玩家总数和正在睡觉的玩家人数
 */
public class PlayerCountUtil {
    private World world;
    public PlayerCountUtil(World world){
        this.world = world;
    }

    /**
     * 返回当前世界睡觉玩家人数
     */
    public int currentWorldSleepPlayerCount(){
        int sleepPlayerCount = 0;
        for (Object obj : world.playerEntities) { // 改为检查这个实体是否是玩家实例，避免不安全的显示类型转换
            if (obj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) obj;
                if (player.isPlayerSleeping()) { // 不能直接在PlayerSleepInBedEvent中调用该方法检测睡觉玩家数量 睡眠玩家人数会在下一个tick才更新 所以在PlayerSleepInBedEvent中使用会计数不准
                    sleepPlayerCount++;
                }
            }
        }
        return sleepPlayerCount;
    }

    /**
     * 返回当前世界玩家人数
     */
    public int currentWorldPlayerCount() {
        return world.playerEntities.size();
    }

    /**
     * 返回当前世界睡觉玩家百分比
     */
    public double getSleepPercentage(){
        return (double) currentWorldSleepPlayerCount() / (double) currentWorldPlayerCount();
    }

    /**
     * 计算百分比
     */
    public double getSleepPercentage(int x, int y){
        return (double) x / (double) y;
    }

}
