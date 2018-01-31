package ninja.genuine.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import ninja.genuine.tooltips.client.config.Config;

public class ModUtils {

	private static final Map<String, String> itemId_modName = new HashMap<>();
	private static final Map<TextFormatting, Integer> formatting_color = new HashMap<>();

	public static void post() {
		for (TextFormatting color : TextFormatting.values())
			formatting_color.put(color, Minecraft.getMinecraft().fontRenderer.getColorCode(color.toString().replace("\u00a7", "").charAt(0)));
		Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
			String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
			String modName = modEntry.getValue().getName();
			itemId_modName.put(lowercaseId, modName);
		}
	}

	public static int getRarityColor(TextFormatting format) {
		return formatting_color.getOrDefault(format, Config.getInstance().getOutlineColor());
	}

	public static String getModName(ItemStack stack) {
		return getModName(stack.getItem());
	}

	public static String getModName(Item item) {
		ResourceLocation itemResourceLocation = Item.REGISTRY.getNameForObject(item);
		if (itemResourceLocation == null)
			return "";
		String modId = itemResourceLocation.getResourceDomain();
		String modIdLC = modId.toLowerCase(Locale.ENGLISH);
		String modName = itemId_modName.get(modIdLC);
		if (modName == null) {
			modName = WordUtils.capitalize(modId);
			itemId_modName.put(modIdLC, modName);
		}
		return modName;
	}
}
