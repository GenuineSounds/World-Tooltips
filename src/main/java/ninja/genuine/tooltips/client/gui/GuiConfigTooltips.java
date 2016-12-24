package ninja.genuine.tooltips.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import ninja.genuine.tooltips.Config;
import ninja.genuine.tooltips.Constants;

public class GuiConfigTooltips extends GuiConfig {

	public GuiConfigTooltips(GuiScreen parent) {
		super(parent, new ConfigElement(Config.getCategory("")).getChildElements(), Constants.MODID, Constants.GUIID, false, false, "World-Tooltips", "Appearance configuration");
	}
}
