package ninja.genuine.tooltips.client;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import ninja.genuine.tooltips.WorldTooltips;

public class TooltipsConfigGUI extends GuiConfig {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TooltipsConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(WorldTooltips.config.getCategory("Appearance")).getChildElements(), WorldTooltips.MODID, WorldTooltips.GUIID, false, false, "World Tooltips", "Appearance configuration");
	}
}
