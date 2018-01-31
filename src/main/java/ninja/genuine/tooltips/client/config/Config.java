package ninja.genuine.tooltips.client.config;

import java.util.Objects;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

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

	public int getMaxDistance() {
		return base.getInt("Maximum Drawing Distance", category_behavior, 8, 2, 64, "Set the maximum distance that tooltips should be displayed from.");
	}

	public boolean isOverridingOutline() {
		return base.getBoolean("Override Outline Color", category_behavior, false, "If enabled outline color will be manually set instead of default behavior.");
	}

	public boolean isHidingMod() {
		return base.getBoolean("Hide Mod Name", category_behavior, false, "Hide mod names on tooltips. Enable if you see two mod names.");
	}

	public float getOpacity() {
		return base.getFloat("Tooltip Opacity", category_appearance, 0.75F, 0.0F, 1.0F, "Set the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.");
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
