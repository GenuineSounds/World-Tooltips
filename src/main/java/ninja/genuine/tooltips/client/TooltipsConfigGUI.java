package ninja.genuine.tooltips.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import ninja.genuine.tooltips.WorldTooltips;

public class TooltipsConfigGUI extends GuiConfig {

	public TooltipsConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(WorldTooltips.config.getCategory("Appearance")).getChildElements(), WorldTooltips.MODID, WorldTooltips.GUIID, false, false, "World Tooltips", "Appearance configuration");
	}
}
