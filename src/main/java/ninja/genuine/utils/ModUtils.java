package ninja.genuine.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import ninja.genuine.tooltips.Constants;
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

	public static EntityItem getMouseOver(Minecraft mc, float partialTicks) {
		mc.mcProfiler.startSection(Constants.MODID);
		if (mc.getRenderViewEntity() == null)
			return null;
		Entity viewer = mc.getRenderViewEntity();
		int range = Config.getInstance().getMaxDistance();
		Vec3d eyes = viewer.getPositionEyes(partialTicks);
		Vec3d look = viewer.getLook(partialTicks);
		Vec3d view = eyes.addVector(look.x * range, look.y * range, look.z * range);
		double distance = 0;
		EntityItem out = null;
		List<EntityItem> list = mc.world.getEntitiesWithinAABB(EntityItem.class, viewer.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).grow(1F, 1F, 1F));
		for (int i = 0; i < list.size(); i++) {
			EntityItem entity = list.get(i);
			AxisAlignedBB aabb = entity.getEntityBoundingBox().offset(0, 0.25, 0).grow(entity.getCollisionBorderSize() + 0.1);
			RayTraceResult ray = aabb.calculateIntercept(eyes, view);
			if (aabb.contains(eyes)) {
				if (distance > 0) {
					out = entity;
					distance = 0;
				}
			} else if (ray != null) {
				double d = eyes.distanceTo(ray.hitVec);
				if (d < distance || distance == 0) {
					out = entity;
					distance = d;
				}
			}
		}
		mc.mcProfiler.endSection();
		return out;
	}
}
