package com.nbnw.sft.handler.command;

import com.nbnw.sft.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CommandSFT extends CommandBase {

    @Override
    public String getCommandName() {
        return "sft"; // 主命令名
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sft <subcommand> [options]";
    }

//    @Override
//    public int getRequiredPermissionLevel() {
//        return 2; // 指令权限等级 普通玩家权限为0. OP玩家可以是1到4级
//    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        switch (args[0].toLowerCase()) {
            case "playerssleepingpercentage":
                handlePlayersSleepingPercentage(sender, args);
                break;
            case "showmessageonplayerlogin":
                handleShowMessageOnPlayerLogin(sender, args);
                break;
            case "help":
                handleHelp(sender, args);
                break;
            default:
                throw new WrongUsageException("Invalid command");
        }
    }
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            // 当玩家输入 '/sft ' 后按 Tab 时的自动补全选项
            List<String> subCommands = new ArrayList<>();
            subCommands.add("help");
            subCommands.add("playersSleepingPercentage");
            subCommands.add("showMessageOnPlayerLogin");
            return subCommands;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("playersSleepingPercentage")) {
            // 添加常用的百分比值
            List<String> percentages = new ArrayList<>();
            percentages.add("10");
            percentages.add("50");
            percentages.add("100");
            return percentages;
        }else if(args.length == 2 && args[0].equalsIgnoreCase("showMessageOnPlayerLogin")){
            List<String> values = new ArrayList<>();
            values.add("true");
            values.add("false");
            return values;
        }
        return null; // 没有适用的自动补全选项
    }

    private void handleHelp(ICommandSender sender, String[] args){
        if (args.length < 1) {
            throw new WrongUsageException("Usage: /sft help");
        }
        sender.addChatMessage(new ChatComponentText("1./sft playersSleepingPercentage <percentage> to set playersSleepingPercentage." +
                "such as you use the /gamerule playersSleepingPercentage <percentage> command in original Minecraft(1.16+)"));
        sender.addChatMessage(new ChatComponentText("2./sft showMessageOnPlayerLogin set to true or false to disable the sft mod's login message"));
    }


    private void handlePlayersSleepingPercentage(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("Usage: /sft playersSleepingPercentage <percentage(0~100)>");
        }
        try {
            double percentage = Double.parseDouble(args[1]) / 100.0;
            if (percentage < 0.0 || percentage > 1.0) {
                throw new NumberFormatException("Percentage must be between 0 and 100.");
            }

            ModConfig.getInstance().getConfig().get(Configuration.CATEGORY_GENERAL, "sps_threshold", 0.5).set(percentage);
            ModConfig.getInstance().reloadConfig(); // 更新并重新加载配置

            sender.addChatMessage(new ChatComponentText("Sleeping percentage set to " + percentage));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Invalid percentage: " + args[1]));
        }
    }

    private void handleShowMessageOnPlayerLogin(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("Usage: /sft showMessageOnPlayerLogin <true/0/false/1>");
        }
        String flag = args[1];
        boolean newSetting;
        if(flag.equals("true") || flag.equals("0")){
            newSetting = true;
            flag = "true";
        }else if(flag.equals("false") || flag.equals("1")){
            newSetting = false;
            flag = "false";
        }else{
            sender.addChatMessage(new ChatComponentText("Invalid value: " + args[1]));
            return;
        }
        ModConfig.getInstance().getConfig().get(Configuration.CATEGORY_GENERAL, "login_message", true).set(newSetting);
        ModConfig.getInstance().reloadConfig(); // 更新并重新加载配置
        sender.addChatMessage(new ChatComponentText("login_message set to " + flag));
    }
}
