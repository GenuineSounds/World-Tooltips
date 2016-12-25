package ninja.genuine.tooltips;

import java.util.Objects;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import ninja.genuine.tooltips.client.gui.GuiConfigTooltips.ColorEntry;

public class Config {

	private static Config instance;
	public static final String category_general = "General";
	public static final String category_appearance = "Appearance";
	public static final String category_behavior = "Behavior";
	public static final String name_enable = "Enable Mod";
	public static final String name_distance = "Maximum Drawing Distance";
	public static final String name_override_outline = "Should Override Outline";
	public static final String name_hide_mod = "Hide Mod Name";
	public static final String name_opacity = "Tooltip Opacity";
	public static final String name_outline = "Outline Color";
	public static final String name_background = "Background Color";

	public static Config getInstance() {
		if (Objects.isNull(instance))
			instance = new Config();
		return instance;
	}

	static void setConfiguration(Configuration config) {
		getInstance().base = config;
	}

	static void save() {
		getInstance().base.save();
	}

	public static ConfigCategory getCategory(String name) {
		return getInstance().base.getCategory(name);
	}

	private Configuration base;

	private Config() {}

	static void populate() {
		getInstance().internalPopulation();
	}

	public boolean isEnabled() {
		return base.getBoolean(name_enable, category_general, true, "Enable rendering the tooltips.");
	}

	public int getMaxDistance() {
		return base.getInt(name_distance, category_behavior, 8, 2, 64, "Set the maximum distance that tooltips should be displayed from.");
	}

	public boolean isOverridingOutline() {
		return base.getBoolean(name_override_outline, category_behavior, false, "If enabled outline color will be manually set instead of default behavior.");
	}

	public boolean isHidingMod() {
		return base.getBoolean(name_hide_mod, category_behavior, false, "Hide mod names on tooltips.");
	}

	public float getOpacity() {
		return base.getFloat(name_opacity, category_appearance, 0.75F, 0.0F, 1.0F, "Set the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.");
	}

	public Property getOutline() {
		return base.get(category_appearance, name_outline, "0x5000FF", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public Property getBackground() {
		return base.get(category_appearance, name_background, "0x100010", "Choose a color using the gui by clicking the color button or type in a color manually.", Type.COLOR);
	}

	public int getBackgroundColor() {
		return decodeProperty(getBackground()) & 0xFFFFFF;
	}

	public int getOutlineColor() {
		return decodeProperty(getOutline()) & 0xFFFFFF;
	}

	private int decodeProperty(Property property) {
		try {
			return Integer.decode(property.getString());
		} catch (NumberFormatException e) {
			return Integer.decode(property.getDefault());
		}
	}

	private void internalPopulation() {
		getBackground();
		getOpacity();
		getOutline();
		getMaxDistance();
		isEnabled();
		isHidingMod();
		isOverridingOutline();
		getOutline().setConfigEntryClass(ColorEntry.class);
		getBackground().setConfigEntryClass(ColorEntry.class);
	}
}
