package ninja.genuine.tooltips.client.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import ninja.genuine.tooltips.WorldTooltips;

public class TooltipRenderer {

	private final int colorPrimary, colorOutline, colorSecondary, alpha;

	public TooltipRenderer() {
		alpha = ((int) (WorldTooltips.alpha * 255) & 0xFF) << 24;
		colorPrimary = WorldTooltips.colorPrimary & 0xFFFFFF;
		colorOutline = WorldTooltips.colorOutline & 0xFFFFFF;
		colorSecondary = (colorOutline & 0xFEFEFE) >> 1 | colorOutline & 0xFF000000;
	}

	public void renderTooltip(Minecraft mc, EntityItem item, List<String> tooltip, double partialTicks) {
		int maxwidth = 0;
		for (int line = 0; line < tooltip.size(); line++) {
			final int swidth = mc.fontRendererObj.getStringWidth(tooltip.get(line));
			if (swidth > maxwidth)
				maxwidth = swidth;
		}
		int w = maxwidth;
		int h = 8;
		if (tooltip.size() > 1)
			h += 2 + (tooltip.size() - 1) * 10;
		int x = -w / 2;
		int y = -h;
		double interpX = mc.getRenderManager().viewerPosX - (item.posX - (item.prevPosX - item.posX) * partialTicks);
		double interpY = mc.getRenderManager().viewerPosY - (item.posY - (item.prevPosY - item.posY) * partialTicks);
		double interpZ = mc.getRenderManager().viewerPosZ - (item.posZ - (item.prevPosZ - item.posZ) * partialTicks);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		double scale = interpDistance / 512;
		if (scale <= 0.01)
			scale = 0.01;
		RenderHelper.start();
		GlStateManager.translate(-interpX, -(interpY - 0.65), -interpZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY + 180, 0, 1, 0);
		GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.scale(scale, -scale, scale);
		GlStateManager.disableDepth();
		RenderHelper.renderTooltipTile(x, y, w, h, colorPrimary | alpha, colorOutline | alpha, colorSecondary | alpha);
		RenderHelper.renderTooltipText(tooltip, item, x, y, alpha);
		GlStateManager.enableDepth();
		GlStateManager.scale(1F / scale, 1F / -scale, 1F / scale);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
		GlStateManager.translate(interpX, interpY - 0.65, interpZ);
		RenderHelper.end();
	}
}
