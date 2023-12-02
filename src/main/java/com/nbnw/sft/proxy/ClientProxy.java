//package com.nbnw.sft.proxy;
//
//import com.nbnw.sft.common.LangManager;
//import cpw.mods.fml.client.registry.ClientRegistry;
//import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//import cpw.mods.fml.common.gameevent.InputEvent;
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.client.settings.KeyBinding;
//import org.lwjgl.input.Keyboard;
//
//import com.nbnw.sft.ModEntry;
//import com.nbnw.sft.network.CommonMessagePacket;
//import com.nbnw.sft.network.PacketManager;
//import com.nbnw.sft.network.CommonMessagePacket.MessageType;
//
//@SideOnly(Side.CLIENT)
//public class ClientProxy extends CommonProxy {
//
//    private KeyBinding toggleSleepKeyBinding;
//
//    public ClientProxy() {
//    }
//
//    @Override
//    public void init() {
//        super.init(); // 调用公共代理的初始化代码
//        // 注册快捷键
//        toggleSleepKeyBinding = new KeyBinding(I18n.format(LangManager.sleepToggle), Keyboard.KEY_P, I18n.format(LangManager.keyCategories));
//        ClientRegistry.registerKeyBinding(toggleSleepKeyBinding); // 注册按键绑定
//        // register the key bending and event listener on client side.
//        FMLCommonHandler.instance().bus().register(this);
//    }
//
//    @SubscribeEvent
//    public void onKeyInput(InputEvent.KeyInputEvent event) {
//        if (toggleSleepKeyBinding.isPressed()) { // 检查绑定的按键是否按下，并处理它
//            // 因为按键代码会随玩家设置改变，所以不能发绑定的按键的代码，需要为不同的按键手动给数据包分配不同的keyCode
//            int singleSleepKeyCode = PacketManager.getInstance().getSingleSleepKeyCode();
//            ModEntry.network.sendToServer(new CommonMessagePacket(MessageType.CLIENT_KEY_PRESSED_CODE, singleSleepKeyCode));
//        }
//    }
//
//}
//
