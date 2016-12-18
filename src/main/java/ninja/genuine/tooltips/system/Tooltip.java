package ninja.genuine.tooltips.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import ninja.genuine.tooltips.WorldTooltips;
import ninja.genuine.tooltips.client.render.RenderHelper;

public class Tooltip {

	private static final Map<String, String> itemIdToModName = new HashMap<String, String>();
	private static final Map<TextFormatting, Integer> formattingToColorCode = new HashMap<>();

	public static void init() {
		Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
			String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
			String modName = modEntry.getValue().getName();
			itemIdToModName.put(lowercaseId, modName);
		}
		for (TextFormatting color : TextFormatting.values())
			formattingToColorCode.put(color, Minecraft.getMinecraft().fontRendererObj.getColorCode(color.toString().replace("\u00a7", "").charAt(0)));
	}

	int colorBackground, overrideOutlineColor, alpha;
	int width, height;
	boolean overrideOutline;
	EntityItem entity;
	List<String> text = new ArrayList<>();

	public Tooltip(EntityPlayer player, EntityItem entity) {
		this.entity = entity;
		syncSettings();
		generateTooltip(player, entity.getEntityItem());
	}

	public void syncSettings() {
		overrideOutline = WorldTooltips.overrideOutline;
		alpha = ((int) (WorldTooltips.alpha * 255) & 0xFF) << 24;
		colorBackground = WorldTooltips.colorBackground & 0xFFFFFF;
		overrideOutlineColor = WorldTooltips.overrideOutlineColor & 0xFFFFFF;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public EntityItem getEntity() {
		return entity;
	}

	public int size() {
		return text.size();
	}

	public String getLine(int line) {
		return text.get(line);
	}

	public TextFormatting getRarityColor() {
		return entity.getEntityItem().getRarity().rarityColor;
	}

	private void generateTooltip(EntityPlayer player, ItemStack item) {
		text = item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		if (!modsAreLoaded() && !WorldTooltips.hideModName)
			text.add(ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString() + getModName(item.getItem()) + ChatFormatting.RESET.toString());
		if (item.stackSize > 1)
			text.set(0, item.stackSize + " x " + text.get(0));
		int maxwidth = 0;
		for (int line = 0; line < text.size(); line++) {
			final int swidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(getLine(line));
			if (swidth > maxwidth)
				maxwidth = swidth;
		}
		width = maxwidth;
		height = 8;
		if (size() > 1)
			height += 2 + (size() - 1) * 10;
	}

	private boolean modsAreLoaded() {
		return Loader.isModLoaded("waila") | Loader.isModLoaded("nei") | Loader.isModLoaded("hwyla");
	}

	private String getModName(Item item) {
		ResourceLocation itemResourceLocation = Item.REGISTRY.getNameForObject(item);
		if (itemResourceLocation == null)
			return null;
		String modId = itemResourceLocation.getResourceDomain();
		String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
		String modName = itemIdToModName.get(lowercaseModId);
		if (modName == null) {
			modName = WordUtils.capitalize(modId);
			itemIdToModName.put(lowercaseModId, modName);
		}
		return modName;
	}

	public void renderTooltip3D(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		int outline1 = overrideOutline ? overrideOutlineColor : formattingToColorCode.getOrDefault(getRarityColor(), overrideOutlineColor);
		outline1 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		int outline2 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		double interpX = mc.getRenderManager().viewerPosX - (getEntity().posX - (getEntity().prevPosX - getEntity().posX) * partialTicks);
		double interpY = mc.getRenderManager().viewerPosY - (getEntity().posY - (getEntity().prevPosY - getEntity().posY) * partialTicks);
		double interpZ = mc.getRenderManager().viewerPosZ - (getEntity().posZ - (getEntity().prevPosZ - getEntity().posZ) * partialTicks);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		double scale = interpDistance;
		scale /= sr.getScaleFactor() * 160;
		if (scale <= 0.01)
			scale = 0.01;
		RenderHelper.start();
		GlStateManager.translate(-interpX, -(interpY - 0.65), -interpZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY + 180, 0, 1, 0);
		GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.scale(scale, -scale, scale);
		int x = -getWidth() / 2;
		int y = -getHeight();
		GlStateManager.disableDepth();
		RenderHelper.renderTooltipTile(x, y, getWidth(), getHeight(), colorBackground | alpha, outline1 | alpha, outline2 | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GlStateManager.enableDepth();
		GlStateManager.scale(1F / scale, 1F / -scale, 1F / scale);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
		GlStateManager.translate(interpX, interpY - 0.65, interpZ);
		RenderHelper.end();
	}

	public void renderTooltip2D(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		int outline1 = formattingToColorCode.getOrDefault(getRarityColor(), overrideOutlineColor);
		outline1 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		int outline2 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(50 * sr.getScaleFactor(), 0, 50 * sr.getScaleFactor());
		int x = getWidth() / 2;
		int y = getHeight();
		RenderHelper.renderTooltipTile(x, y, getWidth(), getHeight(), colorBackground | alpha, outline1 | alpha, outline2 | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
