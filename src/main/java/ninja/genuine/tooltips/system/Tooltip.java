package ninja.genuine.tooltips.system;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import ninja.genuine.tooltips.WorldTooltips;
import ninja.genuine.tooltips.client.render.RenderHelper;

public class Tooltip {

	private static final Map<String, String> itemIdToModName = new HashMap<String, String>();
	private static final Map<EnumChatFormatting, Integer> formattingToColorCode = new HashMap<>();
	private static Class<?> nei;
	private static Method info;
	private static boolean useNei = false;

	public static void init() {
		Map<String, ModContainer> modMap = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> modEntry : modMap.entrySet()) {
			String lowercaseId = modEntry.getKey().toLowerCase(Locale.ENGLISH);
			String modName = modEntry.getValue().getName();
			itemIdToModName.put(lowercaseId, modName);
		}
		formattingToColorCode.put(EnumChatFormatting.BLACK, 0x000000);
		formattingToColorCode.put(EnumChatFormatting.DARK_BLUE, 0x0000AA);
		formattingToColorCode.put(EnumChatFormatting.DARK_GREEN, 0x00AA00);
		formattingToColorCode.put(EnumChatFormatting.DARK_AQUA, 0x00AAAA);
		formattingToColorCode.put(EnumChatFormatting.DARK_RED, 0xAA0000);
		formattingToColorCode.put(EnumChatFormatting.DARK_PURPLE, 0xAA00AA);
		formattingToColorCode.put(EnumChatFormatting.GOLD, 0xFFAA00);
		formattingToColorCode.put(EnumChatFormatting.GRAY, 0xAAAAAA);
		formattingToColorCode.put(EnumChatFormatting.DARK_GRAY, 0x555555);
		formattingToColorCode.put(EnumChatFormatting.BLUE, 0x5555FF);
		formattingToColorCode.put(EnumChatFormatting.GREEN, 0x55FF55);
		formattingToColorCode.put(EnumChatFormatting.AQUA, 0x55FFFF);
		formattingToColorCode.put(EnumChatFormatting.RED, 0xFF5555);
		formattingToColorCode.put(EnumChatFormatting.LIGHT_PURPLE, 0xFF55FF);
		formattingToColorCode.put(EnumChatFormatting.YELLOW, 0xFFFF55);
		formattingToColorCode.put(EnumChatFormatting.WHITE, 0xFFFFFF);
		try {
			nei = Class.forName("codechicken.nei.guihook.GuiContainerManager");
			if (nei != null) {
				info = nei.getDeclaredMethod("itemDisplayNameMultiline", ItemStack.class, GuiContainer.class, boolean.class);
				useNei = true;
			}
		} catch (final Exception e) {}
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

	public EnumChatFormatting getRarityColor() {
		return entity.getEntityItem().getRarity().rarityColor;
	}

	@SuppressWarnings("unchecked")
	private void generateTooltip(EntityPlayer player, ItemStack item) {
		if (useNei)
			try {
				text = (List<String>) info.invoke(null, entity.getEntityItem(), null, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			} catch (final Exception e) {}
		if (Objects.isNull(text) || text.isEmpty())
			text = (List<String>) item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		if (!modsAreLoaded() && !WorldTooltips.hideModName)
			text.add(ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString() + getModName(item.getItem()) + ChatFormatting.RESET.toString());
		if (item.stackSize > 1)
			text.set(0, item.stackSize + " x " + text.get(0));
		int maxwidth = 0;
		for (int line = 0; line < text.size(); line++) {
			final int swidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(getLine(line));
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
		String fullName = Item.itemRegistry.getNameForObject(item);
		String modId = fullName.substring(0, fullName.indexOf(":"));
		String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
		String modName = itemIdToModName.get(lowercaseModId);
		if (modName == null) {
			modName = WordUtils.capitalize(modId);
			itemIdToModName.put(lowercaseModId, modName);
		}
		return modName;
	}

	public void renderTooltip3D(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int outline1 = overrideOutline ? overrideOutlineColor : formattingToColorCode.getOrDefault(getRarityColor(), overrideOutlineColor);
		outline1 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		int outline2 = ((outline1 & 0xFEFEFE) >> 1) | alpha;
		final double interpX = RenderManager.renderPosX - (entity.posX - (entity.prevPosX - entity.posX) * partialTicks);
		final double interpY = RenderManager.renderPosY - (entity.posY - (entity.prevPosY - entity.posY) * partialTicks);
		final double interpZ = RenderManager.renderPosZ - (entity.posZ - (entity.prevPosZ - entity.posZ) * partialTicks);
		final double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		RenderHelper.start();
		GL11.glTranslated(-interpX, -(interpY - 0.55), -interpZ);
		GL11.glRotatef(-RenderManager.instance.playerViewY + 180, 0, 1, 0);
		GL11.glRotatef(-RenderManager.instance.playerViewX, 1, 0, 0);
		double scale = interpDistance;
		scale /= sr.getScaleFactor() * 160;
		if (scale <= 0.01)
			scale = 0.01;
		GL11.glScaled(scale, -scale, scale);
		int x = -getWidth() / 2;
		int y = -getHeight();
		RenderHelper.renderTooltipTile(x, y, getWidth(), getHeight(), colorBackground | alpha, outline1 | alpha, outline2 | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GL11.glScaled(1F / scale, 1F / -scale, 1F / scale);
		GL11.glRotatef(RenderManager.instance.playerViewX, 1, 0, 0);
		GL11.glRotatef(RenderManager.instance.playerViewY - 180, 0, 1, 0);
		GL11.glTranslated(interpX, interpY - entity.height - 0.5, interpZ);
		RenderHelper.end();
	}
}
