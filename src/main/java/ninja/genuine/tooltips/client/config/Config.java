package ninja.genuine.tooltips.client.config;

import java.util.Objects;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;

public class Config {

	private static Config instance;
	public static final String category_general = "General";
	public static final String category_appearance = "Appearance";
	public static final String category_behavior = "Behavior";

	public static Config getInstance() {
		if (Objects.isNull(instance))
			instance = new Config();
		return instance;
	}

	public static void setConfiguration(Configuration config) {
		getInstance().base = config;
	}

	public static void save() {
		getInstance().base.save();
	}

	public static ConfigCategory getCategory(String name) {
		return getInstance().base.getCategory(name);
	}

	public static void populate() {
		getInstance().internalPopulation();
	}

	private Configuration base;

	private Config() {}

	public boolean isEnabled() {
		return base.getBoolean("Enable Mod", category_general, true, "Enable rendering the tooltips.");
	}

	public int getRenderDistance() {
		return base.getInt("Maximum Drawing Distance", category_behavior, 12, 2, 64, "Sets the maximum distance that tooltips will be displayed.");
	}

	public int getMaxTooltips() {
		return base.getInt("Max Tooltips", category_behavior, 4, 0, 999, "Sets the maximum number of tooltips shown on screenat once.");
	}

	public int getShowTime() {
		return base.getInt("Ticks to Show", category_behavior, 40, 0, 1000, "Sets the number of ticks to show the tooltips before they fade.");
	}

	public int getFadeTime() {
		return base.getInt("Fade Duration", category_behavior, 10, 0, 1000, "Sets the duration in ticks for the fading process.");
	}

	public boolean isOverridingOutline() {
		return base.getBoolean("Override Outline Color", category_behavior, false, "Use the custom outline color instead.");
	}

	public boolean isHidingModName() {
		return base.getBoolean("Hide Mod Name", category_behavior, false, "Hide mod names on tooltips. Enable this if you see two mod names.");
	}

	public Property getOpacity() {
		return base.get(category_appearance, "Tooltip Opacity", 0.75, "Sets the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.", 0.0, 1.0);
	}

	public Property getScale() {
		return base.get(category_appearance, "Tooltip Scale", 1.0, "Sets the scale for the tooltips; 0.1 being one thenth the size and 4 being four times the size.", 0.1, 4.0);
	}

	public Property getOutline() {
		return base.get(category_appearance, "Outline Color", "0x5000FF", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public Property getBackground() {
		return base.get(category_appearance, "Background Color", "0x100010", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public int getBackgroundColor() {
		return decodeProperty(getBackground()) & 0xFFFFFF;
	}

	public int getOutlineColor() {
		return decodeProperty(getOutline()) & 0xFFFFFF;
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

	private int decodeProperty(Property property) {
		try {
			return Integer.decode(property.getString());
		} catch (NumberFormatException e) {
			return Integer.decode(property.getDefault());
		}
	}
}
