package ninja.genuine.utils;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.Config;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ModUtils {

	private static final Map<String, String> itemId_modName = new HashMap<>();
	private static final Map<TextFormatting, Integer> formatting_color = new HashMap<>();

	public static void post() {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		for (TextFormatting color : TextFormatting.values())
			formatting_color.put(color, fr.getColorCode(color.toString().replace("\u00a7", "").charAt(0)));
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

	public static int getRarityColor(Tooltip tooltip) {
		return formatting_color.getOrDefault(tooltip.formattingColor(), Config.getInstance().getOutlineColor());
	}

	public static String getModName(Tooltip tooltip) {
		return getModName(tooltip.getEntity());
	}

	public static String getModName(EntityItem entity) {
		return getModName(entity.getItem());
	}

	public static String getModName(ItemStack stack) {
		return getModName(stack.getItem());
	}

	public static String getModName(Item item) {
		ResourceLocation resource = Item.REGISTRY.getNameForObject(item);
		if (resource == null)
			return "";
		String modId = resource.getResourceDomain();
		String modIdLC = modId.toLowerCase(Locale.ENGLISH);
		String modName = itemId_modName.get(modIdLC);
		if (modName == null) {
			modName = WordUtils.capitalize(modId);
			itemId_modName.put(modIdLC, modName);
		}
		return modName;
	}

	public static Optional<EntityItem> getMouseOver(World world, Entity player, float partialTicks) throws ConcurrentModificationException {
		if (world == null || player == null)
			return Optional.empty();
		final Entity viewer = player;
		final int range = Config.getInstance().getRenderDistance();
		final Vec3d eyes = viewer.getPositionEyes(partialTicks);
		final Vec3d look = viewer.getLook(partialTicks);
		final Vec3d view = eyes.addVector(look.x * range, look.y * range, look.z * range);
		double distance = 0;
		EntityItem out = null;
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class,
				viewer.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).grow(1F, 1F, 1F));
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
		return out == null ? Optional.empty() : Optional.of(out);
	}
}
