package ninja.genuine.tooltips;

import java.util.Objects;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import ninja.genuine.tooltips.client.RenderEvent;
import ninja.genuine.tooltips.client.gui.GuiColorConfig;
import ninja.genuine.utils.ModUtils;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, canBeDeactivated = true, clientSideOnly = true, updateJSON = Constants.URL + "update.json", useMetadata = true, guiFactory = "ninja.genuine.tooltips.client.TooltipsGuiFactory")
public class WorldTooltips {

	@Instance(Constants.MODID)
	public static WorldTooltips instance;
	private RenderEvent events = new RenderEvent();
	private KeyBinding configKey = new KeyBinding("World-Tooltips Configuration", Keyboard.KEY_SUBTRACT, "World-Tooltips");

	public WorldTooltips() {
		instance = this;
	}

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		Config.setConfiguration(new Configuration(event.getSuggestedConfigurationFile(), Constants.VERSION));
		Config.populate();
		Config.save();
		ClientRegistry.registerKeyBinding(configKey);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(events);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event) {
		ModUtils.post();
		events.post();
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		MinecraftForge.EVENT_BUS.unregister(events);
	}

	@SubscribeEvent
	public void keypress(KeyInputEvent event) {
		if (configKey.isPressed())
			Minecraft.getMinecraft().displayGuiScreen(new GuiColorConfig(Minecraft.getMinecraft().currentScreen));
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		sync();
	}

	public void sync() {
		Config.save();
		if (!Objects.isNull(events))
			events.sync();
	}
}
