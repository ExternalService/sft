package com.nbnw.sft.handler;

import com.nbnw.sft.common.PlayerCountUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nbnw.sft.config.ModConfig;

/**
 * 不需要全部玩家睡觉即可跳过黑夜功能
 * 已添加多世界处理
 */
public class PlayerSleepEventHandler {

    private static final double SLEEP_PERCENTAGE_THRESHOLD = 0.5; // 50% 触发阈值 睡眠玩家百分比超过此值时才执行功能
    private Map<World, Map<EntityPlayer, Long>> sleepingPlayers = new HashMap<>(); // 存储每个世界的每一个正在睡觉的玩家和该玩家的睡觉时间点

    /**
     * 获取sleepingPlayers中的一个world中的最大睡眠时间
     * 实际上现有的逻辑每个玩家的最大睡眠时间都是一致的，都会是达到百分比阈值时间点时的世界时间
     */
    private Long getMaxSleepTime(World world) {
        Map<EntityPlayer, Long> worldSleepingPlayers = sleepingPlayers.get(world); // get方法在没有查找到对应world时则返回null
        if (worldSleepingPlayers == null || worldSleepingPlayers.isEmpty()) {
            return null;
        }
        Long maxSleepTime = 0L; // 初始化最大睡眠时间
        for (Long sleepTime : worldSleepingPlayers.values()) {
            if (sleepTime != null && sleepTime > maxSleepTime) {
                maxSleepTime = sleepTime; // 更新最大睡眠时间
            }
        }
        return maxSleepTime;
    }

    /**
     * 重置一个world的最大睡眠时间为null
     */
    private void resetMaxSleepTime(World world) {
        Map<EntityPlayer, Long> worldSleepingPlayers = sleepingPlayers.get(world);
        if (worldSleepingPlayers != null) {
            for (EntityPlayer player : worldSleepingPlayers.keySet()) {
                worldSleepingPlayers.put(player, null); // 将每个玩家的睡眠时间重置为 null
            }
        }
    }

    /**
     * 当玩家右键床且入睡时，如果是黑夜，则将玩家添加进入睡玩家列表
     * 没有开启功能和人数不足时，都需要重置记录的睡眠时长为空
     */
    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;
        // 功能被关闭时 将每个世界的最长睡觉时间重置为null并返回
        if(!ModConfig.getInstance().isSinglePlayerSleepEnabled()){
            resetMaxSleepTime(world);
            return;
        }

        // 确保事件发生在服务器端和夜晚
        if (!world.isRemote && !world.isDaytime()) { // 可能黑夜判断多余，因为白天玩家躺不到床上，就没办法触发该事件，但是也许有mod有白天睡觉功能
            Map<EntityPlayer, Long> worldSleepingPlayers = sleepingPlayers.computeIfAbsent(world, k -> new HashMap<>()); // 取出当前世界睡眠玩家和睡眠时长数据
            // 将进入睡眠状态的玩家添加到列表中，但暂时不记录开始睡眠的时间
            // worldSleepingPlayers.putIfAbsent(player, null);
            worldSleepingPlayers.putIfAbsent(player, null);

//            System.out.println("Added player to sleep list, current sleep count: " + worldSleepingPlayers.size());

            for (Map.Entry<EntityPlayer, Long> entry : worldSleepingPlayers.entrySet()) {
                EntityPlayer sleepingPlayer = entry.getKey();
                Long sleepTime = entry.getValue();
//                System.out.println("Player: " + sleepingPlayer.getDisplayName() + ", Sleep Time: " + sleepTime);
//                System.out.println("Player trying to sleep: " + player.getDisplayName() + ", Hash: " + player.hashCode());


            }

            int sleepPlayerCount =  worldSleepingPlayers.size();
            PlayerCountUtil playerCountUtil = new PlayerCountUtil(world);
            double sleepPercentage = playerCountUtil.getSleepPercentage(sleepPlayerCount, world.playerEntities.size()); // 计算睡眠玩家的百分比
//            System.out.println("Sleeping percentage:" + sleepPercentage);
//            System.out.println("Sleeping players count:" + sleepPlayerCount);
//            System.out.println("Players in current world count:" + playerCountUtil.currentWorldPlayerCount());

            if (sleepPercentage >= SLEEP_PERCENTAGE_THRESHOLD) {
                // 睡眠玩家百分比超过阈值，为所有尚未记录开始睡眠时间的玩家记录当前时间
                for (Map.Entry<EntityPlayer, Long> entry : worldSleepingPlayers.entrySet()) { // 遍历记录的这个世界中正在睡觉的玩家
                    if (entry.getValue() == null) {
                        entry.setValue(world.getWorldTime()); // 这个事件只会在玩家睡觉时触发，然后去获取到该玩家所处的世界再执行到这里，所以不用担心这里设置这个世界的时间会影响到其它世界
                    }
                }
            }else{
                // 睡眠玩家百分比未达到阈值，重置已记录的睡眠时间
                for (Map.Entry<EntityPlayer, Long> entry : worldSleepingPlayers.entrySet()) {
                    if (entry.getValue() != null) {
                        entry.setValue(null);  // 重置睡眠时间
                    }
                }
            }
            /**
             * 为什么要在else里清空记录的睡眠时间
             * 这个地方遍历了该世界的其它玩家的睡眠开始时间点，这个数据会被应用到其它地方进行睡眠时间差的计算。
             * 而这个计算出来的差值会用来判断是否达到一定时长，用于决定是否跳过黑夜。这会导致一个现象，
             * 那就是一个玩家睡下去，达成百分比条件，然后所有之前数据为null的玩家睡眠时间都变为了此刻的当前世界时间，
             * 但是如果这时又有人起床了，那么就会导致虽然不能执行到之前代码中的跳过黑夜逻辑，但是计算出的最大睡眠时长一直在变大，
             * 这会导致在这之后，某个玩家睡觉的一瞬间就达成了睡眠时长条件，即一躺下去就起床跳到白天了，
             * 而如果没有经历这一过程，就会是玩家躺下去睡了几秒再跳过到白天，
             * 我希望我的模组在各种情况下表现一致，所以这里需要将时间值清空为null
             */
        }
    }

    /**
     * 当玩家退出服务器时，将其从睡眠玩家列表中移除
     * 这样可以阻止一种特殊情况，那就是玩家睡觉之后点击关闭游戏，会导致没有人睡觉还是会继续继续计时从而跳过黑夜
     * PlayerLoggedOutEvent会在服务端处理
     * @param event 玩家退出游戏事件
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // 玩家退出时的逻辑
        EntityPlayer player = event.player;
        World world = player.worldObj;
        if(world.isRemote){ // 客户端直接返回 只在服务端处理睡觉玩家统计数据
            return;
        }
        Map<EntityPlayer, Long> worldSleepingPlayers = sleepingPlayers.get(world);
        if (worldSleepingPlayers != null && worldSleepingPlayers.containsKey(player)) { // 如果玩家正在睡眠时退出游戏，则减少该世界的睡眠玩家计数
            worldSleepingPlayers.remove(player); // 如果睡眠玩家列表包含这个玩家，将其移除
            if (worldSleepingPlayers.isEmpty()) {
                sleepingPlayers.remove(world); // 如果这个世界的睡眠玩家列表为空，则移除这个世界的检测
            }
        }
    }

    /**
     *
     */
    @SubscribeEvent
    public void onWorldTick(WorldEvent event) {
        World world = event.world;
        Map<EntityPlayer, Long> worldSleepingPlayers = sleepingPlayers.get(world);
        Long worldMaxSleepTime = getMaxSleepTime(world); //获取对应值 如果为空则赋值为0并返回0
        if (worldSleepingPlayers != null) {
            // 检查是否到了夜晚
            if (!world.isRemote && !world.isDaytime() && worldSleepingPlayers != null) {
                int playersInWorldCount = world.playerEntities.size();
                PlayerCountUtil playerCountUtil = new PlayerCountUtil(world);
                double sleepPercentage = playerCountUtil.getSleepPercentage();
                Iterator<Map.Entry<EntityPlayer, Long>> iterator = worldSleepingPlayers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<EntityPlayer, Long> entry = iterator.next();
                    EntityPlayer player = entry.getKey();
                    Long sleepStartTime = entry.getValue();// long 改为 Long 以避免空指针
                    // 如果玩家不在床上或死亡，跳过并移除这个玩家
                    if (player.isDead || !player.isPlayerSleeping()) {
                        // System.out.println("玩家没有在睡觉了，移除该玩家：" + player.getDisplayName() + "hash:" + player.hashCode());
                        iterator.remove();
                        continue;
                    }

                    // 检查玩家已经睡了多久，使用当前世界时间减去开始睡觉的时间
                    long timeSlept = 0;
                    // 如果这个功能被关闭了，则设置睡觉时长为-1，从而阻止功能实现
                    // 不能直接通过清理睡觉玩家列表阻止整个事件逻辑，否则在关闭后又开启的情况下，仍然有玩家睡觉却无法继续正常计时，因为睡觉的玩家已经被移除了列表
                    if(!ModConfig.getInstance().isSinglePlayerSleepEnabled()){
                        resetMaxSleepTime(world);
                        // timeSlept = -1; // 简单的做法 在完成下面重新记录时间的功能后，注释掉timeSlept = -1;
                    }else{
                        if(sleepStartTime == null){
                            // 功能被重新启用，为尚未记录开始睡眠时间的玩家设置当前时间
                            entry.setValue(world.getWorldTime());
                            sleepStartTime = world.getWorldTime();  // 更新局部变量，以便后续逻辑可以使用
                        }
                        if(sleepPercentage >= SLEEP_PERCENTAGE_THRESHOLD && sleepStartTime != null) { // 保险起见检测sleepStartTime是否为空
                            timeSlept = world.getWorldTime() - sleepStartTime;
                        }
                    }

                    if(timeSlept > worldMaxSleepTime){
                        worldSleepingPlayers.put(player, world.getWorldTime() - 100); // 更新该玩家的睡眠开始时间
                        // 虽然worldSleepingPlayers是局部变量，但是在Map中它是引用的方式而不是赋值，所以直接修改worldSleepingPlayers也可以影响到类属性sleepingPlayers本身
                        worldMaxSleepTime = timeSlept;
                        // System.out.println("On World Tick 赋值 worldMaxSleepTime:" + worldMaxSleepTime);
                    }

                    // 如果满足跳过夜晚的条件（例如，超过100）
                    if (timeSlept >= 100) {
                        world.setWorldTime(1000);
                        // 唤醒所有睡眠的玩家
                        for (EntityPlayer sleepingPlayer : worldSleepingPlayers.keySet()) {
                            sleepingPlayer.wakeUpPlayer(false, true, true);
                        }
                        sleepingPlayers.remove(world);// 清除该世界中记录的睡眠玩家
                        break; // 满足条件，不再检测
                    }
                }
            }
        }
    }
}
