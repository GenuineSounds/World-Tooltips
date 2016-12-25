package ninja.genuine.tooltips.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import ninja.genuine.tooltips.Config;
import ninja.genuine.tooltips.client.render.RenderHelper;
import ninja.genuine.utils.ModUtils;

public class Tooltip {

	private int width, height;
	private int alpha;
	private EntityItem entity;
	private List<String> text = new ArrayList<>();

	public Tooltip(EntityPlayer player, EntityItem entity) {
		this.entity = entity;
		sync();
		generateTooltip(player, entity.getEntityItem());
		calcDim();
	}

	public void sync() {
		alpha = ((int) (Config.getInstance().getOpacity() * 255) & 0xFF) << 24;
	}

	private void generateTooltip(EntityPlayer player, ItemStack item) {
		text = item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		if (!modsAreLoaded() && !Config.getInstance().isHidingMod())
			text.add(ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString() + ModUtils.getModName(item) + ChatFormatting.RESET.toString());
		if (item.getCount() > 1)
			text.set(0, item.getCount() + " x " + text.get(0));
	}

	private void calcDim() {
		int maxWidth = 0;
		for (int line = 0; line < text.size(); line++) {
			int tmp = Minecraft.getMinecraft().fontRendererObj.getStringWidth(getLine(line));
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

	public void renderTooltip3D(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		Tuple<Integer, Integer> outline = setupColors();
		double interpX = mc.getRenderManager().viewerPosX - (getEntity().posX - (getEntity().prevPosX - getEntity().posX) * partialTicks);
		double interpY = mc.getRenderManager().viewerPosY - (getEntity().posY - (getEntity().prevPosY - getEntity().posY) * partialTicks);
		double interpZ = mc.getRenderManager().viewerPosZ - (getEntity().posZ - (getEntity().prevPosZ - getEntity().posZ) * partialTicks);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		double scale = interpDistance;
		scale /= sr.getScaleFactor() * 160;
		if (scale <= 0.01)
			scale = 0.01;
		RenderHelper.start3D();
		GlStateManager.translate(-interpX, -(interpY - 0.65), -interpZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY + 180, 0, 1, 0);
		GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.scale(scale, -scale, scale);
		int x = -getWidth() / 2;
		int y = -getHeight();
		GlStateManager.disableDepth();
		RenderHelper.renderTooltipTile(x, y, getWidth(), getHeight(), Config.getInstance().getBackgroundColor() | alpha, outline.getFirst() | alpha, outline.getSecond() | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GlStateManager.enableDepth();
		GlStateManager.scale(1 / scale, 1 / -scale, 1 / scale);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
		GlStateManager.translate(interpX, interpY - 0.65, interpZ);
		RenderHelper.end3D();
	}

	public void renderTooltip2D(Minecraft mc, double partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		Tuple<Integer, Integer> outline = setupColors();
		int x = getWidth() / 2;
		int y = getHeight();
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(50 * sr.getScaleFactor(), 0, 50 * sr.getScaleFactor());
		RenderHelper.renderTooltipTile(x, y, getWidth(), getHeight(), Config.getInstance().getBackgroundColor() | alpha, outline.getFirst() | alpha, outline.getSecond() | alpha);
		RenderHelper.renderTooltipText(this, x, y, alpha);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	private Tuple<Integer, Integer> setupColors() {
		int i = Config.getInstance().isOverridingOutline() ? Config.getInstance().getOutlineColor() : (ModUtils.formatting_color.getOrDefault(getRarityColor(), Config.getInstance().getOutlineColor()) & 0xFEFEFE) >> 1;
		return new Tuple<>(i, (i & 0xFEFEFE) >> 1);
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
}
