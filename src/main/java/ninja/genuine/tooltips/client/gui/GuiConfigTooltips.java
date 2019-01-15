package ninja.genuine.tooltips.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import ninja.genuine.tooltips.Constants;
import ninja.genuine.tooltips.client.config.Config;

public class GuiConfigTooltips extends GuiConfig {

	public GuiConfigTooltips(GuiScreen parent) {
		super(parent, new ArrayList<>(), Constants.MODID, Constants.GUIID, false, false, Constants.NAME, "Appearance configuration");
		ConfigElement elementGeneral = new ConfigElement(Config.getCategory(Config.category_general));
		ConfigElement elementAppearance = new ConfigElement(Config.getCategory(Config.category_appearance));
		ConfigElement elementBehavior = new ConfigElement(Config.getCategory(Config.category_behavior));
		configElements.addAll(elementGeneral.getChildElements());
		configElements.addAll(elementAppearance.getChildElements());
		configElements.addAll(elementBehavior.getChildElements());
	}
}
