package ninja.genuine.tooltips.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.tooltips.client.render.RenderHelper;
import ninja.genuine.utils.ModUtils;

public class Tooltip {

	private Config config = Config.getInstance();
	private int width, height, alpha;
	private EntityItem entity;
	private List<String> text = new ArrayList<>();

	public Tooltip(EntityPlayer player, EntityItem entity) {
		this.entity = entity;
		sync();
		generateTooltip(player, entity.getItem());
		calcDim();
	}

	public void sync() {
		alpha = ((int) (config.getOpacity().getDouble() * 255) & 0xFF) << 24;
	}

	private void generateTooltip(EntityPlayer player, ItemStack item) {
		text = item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		if (!modsAreLoaded() && !config.isHidingMod())
			text.add(ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString() + ModUtils.getModName(item) + ChatFormatting.RESET.toString());
		if (item.getCount() > 1)
			text.set(0, item.getCount() + " x " + text.get(0));
	}

	private void calcDim() {
		int maxWidth = 0;
		for (int line = 0; line < text.size(); line++) {
			int tmp = Minecraft.getMinecraft().fontRenderer.getStringWidth(getLine(line));
			if (tmp > maxWidth)
				maxWidth = tmp;
		}
		width = maxWidth;
		height = 8;
		if (size() > 1)
			height += 2 + (size() - 1) * 10;
	}

	private boolean modsAreLoaded() {
		return Loader.isModLoaded("waila") | Loader.isModLoaded("nei") | Loader.isModLoaded("hwyla");
	}

	public void render(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		double interpX = mc.getRenderManager().viewerPosX - (getEntity().posX - (getEntity().prevPosX - getEntity().posX) * partialTicks);
		double interpY = mc.getRenderManager().viewerPosY - 0.65 - (getEntity().posY - (getEntity().prevPosY - getEntity().posY) * partialTicks);
		double interpZ = mc.getRenderManager().viewerPosZ - (getEntity().posZ - (getEntity().prevPosZ - getEntity().posZ) * partialTicks);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		double scale = interpDistance; // -(0.5 / interpDistance) * 0.02 + 0.02;
		scale /= (6 - sr.getScaleFactor()) * 160;
		if (scale <= 0.01)
			scale = 0.01;
		RenderHelper.start3D();
		GlStateManager.translate(-interpX, -(interpY), -interpZ);
		GlStateManager.rotate(mc.getRenderManager().playerViewY + 180, 0, -1, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, -1, 0, 0);
		GlStateManager.scale(scale, -scale, scale);
		int x = -getWidth() / 2;
		int y = -getHeight() / 2;
		GlStateManager.disableDepth();
		RenderHelper.renderTooltipTile(entity, x, y, getWidth(), getHeight(), config.getBackgroundColor() | alpha, config.getOutlineColor() | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GlStateManager.enableDepth();
		GlStateManager.scale(1 / scale, 1 / -scale, 1 / scale);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
		GlStateManager.translate(interpX, interpY, interpZ);
		RenderHelper.end3D();
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
		return entity.getItem().getRarity().rarityColor;
	}
}
