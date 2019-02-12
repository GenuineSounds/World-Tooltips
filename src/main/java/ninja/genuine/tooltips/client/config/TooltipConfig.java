package ninja.genuine.tooltips.client.config;

import java.util.Objects;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;

public class TooltipConfig {

	private static TooltipConfig instance;
	public static final String category_general = "General";
	public static final String category_appearance = "Appearance";
	public static final String category_behavior = "Behavior";

	public static TooltipConfig getInstance() {
		if (Objects.isNull(instance))
			instance = new TooltipConfig();
		return instance;
	}

	public void setConfiguration(Configuration config) {
		base = config;
	}

	public void save() {
		base.save();
	}

	public ConfigCategory getCategory(String name) {
		return getInstance().base.getCategory(name);
	}

	public void populate() {
		internalPopulation();
	}

	private Configuration base;

	private TooltipConfig() {}

	public static boolean isEnabled() {
		return getInstance().base.getBoolean("Enable Mod", category_general, true, "Enable rendering the tooltips.");
	}

	public static int getRenderDistance() {
		return getInstance().base.getInt("Maximum Drawing Distance", category_behavior, 12, 2, 64, "Sets the maximum distance that tooltips will be displayed.");
	}

	public static int getMaxTooltips() {
		return getInstance().base.getInt("Max Tooltips", category_behavior, 4, 0, 999, "Sets the maximum number of tooltips shown on screenat once.");
	}

	public static int getShowTime() {
		return getInstance().base.getInt("Ticks to Show", category_behavior, 40, 0, 1000, "Sets the number of ticks to show the tooltips before they fade.");
	}

	public static int getFadeTime() {
		return getInstance().base.getInt("Fade Duration", category_behavior, 10, 0, 1000, "Sets the duration in ticks for the fading process.");
	}

	public static boolean isOverridingOutline() {
		return getInstance().base.getBoolean("Override Outline Color", category_behavior, false, "Use the custom outline color instead.");
	}

	public static boolean isHidingModName() {
		return getInstance().base.getBoolean("Hide Mod Name", category_behavior, false, "Hide mod names on tooltips. Enable this if you see two mod names.");
	}

	public static Property getOpacity() {
		return getInstance().base.get(category_appearance, "Tooltip Opacity", 0.75, "Sets the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.", 0.0, 1.0);
	}

	public static Property getScale() {
		return getInstance().base.get(category_appearance, "Tooltip Scale", 1.0, "Sets the scale for the tooltips; 0.1 being one thenth the size and 4 being four times the size.", 0.1, 4.0);
	}

	public static Property getOutline() {
		return getInstance().base.get(category_appearance, "Outline Color", "0x5000FF", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public static Property getBackground() {
		return getInstance().base.get(category_appearance, "Background Color", "0x100010", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public static int getBackgroundColor() {
		try {
			return Integer.decode(getBackground().getString()) & 0xFFFFFF;
		} catch (Exception e) {
			return 0x100010;
		}
	}

	public static int getOutlineColor() {
		try {
			return Integer.decode(getOutline().getString()) & 0xFFFFFF;
		} catch (Exception e) {
			return 0x5000FF;
		}
	}

	private void internalPopulation() {
		isEnabled();
		getOpacity().setConfigEntryClass(NumberSliderEntry.class);
		getScale().setConfigEntryClass(NumberSliderEntry.class);
		getOutline().setConfigEntryClass(ColorEntry.class);
		getBackground().setConfigEntryClass(ColorEntry.class);
		getRenderDistance();
		getMaxTooltips();
		getShowTime();
		getFadeTime();
		isHidingModName();
		isOverridingOutline();
	}
}
