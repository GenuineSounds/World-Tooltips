package ninja.genuine.tooltips.client.render;

import static net.minecraft.client.renderer.GlStateManager.alphaFunc;
import static net.minecraft.client.renderer.GlStateManager.blendFunc;
import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.disableAlpha;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.disableDepth;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;
import static net.minecraft.client.renderer.GlStateManager.disableRescaleNormal;
import static net.minecraft.client.renderer.GlStateManager.disableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.enableAlpha;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableDepth;
import static net.minecraft.client.renderer.GlStateManager.enableRescaleNormal;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.popAttrib;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushAttrib;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.shadeModel;
import static net.minecraft.client.renderer.GlStateManager.translate;
import static net.minecraft.client.renderer.GlStateManager.tryBlendFuncSeparate;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

public class RenderHelper {

	public static void renderTooltip(Tooltip tooltip, double partialTicks) {
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		EntityItem entity = tooltip.getEntity();
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int x = -tooltip.getWidth() / 2;
		int y = -tooltip.getHeight() / 2;
		double interpX = rm.viewerPosX - (entity.posX - (entity.prevPosX - entity.posX) * partialTicks);
		double interpY = rm.viewerPosY - 0.65 - (entity.posY - (entity.prevPosY - entity.posY) * partialTicks);
		double interpZ = rm.viewerPosZ - (entity.posZ - (entity.prevPosZ - entity.posZ) * partialTicks);
		double interpD = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		double scale = interpD / ((6 - sr.getScaleFactor()) * 160);
		if (scale < 0.01)
			scale = 0.01;
		scale *= Config.getInstance().getScale().getDouble();
		pushMatrix();
		pushAttrib();
		enableRescaleNormal();
		enableAlpha();
		alphaFunc(516, 0.1F);
		enableBlend();
		blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		color(1.0F, 1.0F, 1.0F, 1.0F);
		translate(-interpX, -interpY, -interpZ);
		rotate(rm.playerViewY + 180, 0, -1, 0);
		rotate(rm.playerViewX, -1, 0, 0);
		scale(scale, -scale, scale);
		disableDepth();
		renderTooltipTile(tooltip, x, y, tooltip.getWidth(), tooltip.getHeight());
		renderTooltipText(tooltip, x, y);
		enableDepth();
		scale(1 / scale, 1 / -scale, 1 / scale);
		rotate(rm.playerViewX, 1, 0, 0);
		rotate(rm.playerViewY - 180, 0, 1, 0);
		translate(interpX, interpY, interpZ);
		disableAlpha();
		disableRescaleNormal();
		disableLighting();
		popAttrib();
		popMatrix();
	}

	private static void renderTooltipText(Tooltip tooltip, int x, int y) {
		double d = Math.abs(Math.pow(-1, 2) * (tooltip.getTickCount() / 60D));
		if (d > Config.getInstance().getOpacity().getDouble())
			d = Config.getInstance().getOpacity().getDouble();
		int alpha = ((int) (d * 0xFF) & 0xFF) << 24;
		if ((alpha & 0xFC000000) == 0)
			return;
		pushMatrix();
		enableBlend();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		for (int i = 0; i < tooltip.getText().size(); i++) {
			String s = tooltip.getText().get(i);
			if (i == 0)
				s = tooltip.formattingColor() + s;
			Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, 0xFFFFFF | alpha, true);
			if (i == 0)
				y += 2;
			y += 10;
		}
		disableBlend();
		popMatrix();
	}

	private static void renderTooltipTile(Tooltip tooltip, int x, int y, int width, int height) {
		Config cfg = Config.getInstance();
		double d = Math.abs(Math.pow(-1, 2) * (tooltip.getTickCount() / 60D));
		if (d > cfg.getOpacity().getDouble())
			d = cfg.getOpacity().getDouble();
		int alpha = ((int) (d * 0xFF) & 0xFF) << 24;
		int colorBackground = cfg.getBackgroundColor() | alpha;
		int colorOutline = ((cfg.isOverridingOutline() ? cfg.getOutlineColor() : ModUtils.getRarityColor(tooltip)) | alpha) & 0xFFE0E0E0;
		int colorOutlineShade = ((colorOutline & 0xFEFEFE) >> 1) | alpha;
		drawGradientRect(x - 3, y - 4, 0, width + 6, 1, colorBackground, colorBackground);
		drawGradientRect(x - 3, y + height + 3, 0, width + 6, 1, colorBackground, colorBackground);
		drawGradientRect(x - 3, y - 3, 0, width + 6, height + 6, colorBackground, colorBackground);
		drawGradientRect(x - 4, y - 3, 0, 1, height + 6, colorBackground, colorBackground);
		drawGradientRect(x + width + 3, y - 3, 0, 1, height + 6, colorBackground, colorBackground);
		drawGradientRect(x - 3, y - 2, 0, 1, height + 4, colorOutline, colorOutlineShade);
		drawGradientRect(x + width + 2, y - 2, 0, 1, height + 4, colorOutline, colorOutlineShade);
		drawGradientRect(x - 3, y - 3, 0, width + 6, 1, colorOutline, colorOutline);
		drawGradientRect(x - 3, y + height + 2, 0, width + 6, 1, colorOutlineShade, colorOutlineShade);
	}

	public static void drawHuePicker(double x, double y, double z, double width, double height) {
		height /= 5F;
		drawGradientRect(x - 1, y + 0 * height, z, width, height, 0xFFFF0000, 0xFFFFFF00);
		drawGradientRect(x - 1, y + 1 * height, z, width, height, 0xFFFFFF00, 0xFF00FF00);
		drawGradientRect(x - 1, y + 2 * height, z, width, height, 0xFF00FF00, 0xFF00FFFF);
		drawGradientRect(x - 1, y + 3 * height, z, width, height, 0xFF00FFFF, 0xFF0000FF);
		drawGradientRect(x - 1, y + 4 * height, z, width, height, 0xFF0000FF, 0xFFFF0000);
	}

	public static void drawColorPicker(double x, double y, double z, double width, double height, int hue) {
		float red = (hue >> 16 & 0xFF) / 255F;
		float green = (hue >> 8 & 0xFF) / 255F;
		float blue = (hue & 0xFF) / 255F;
		disableTexture2D();
		enableBlend();
		disableAlpha();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		shadeModel(GL11.GL_SMOOTH);
		color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		// Color Gradient
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(red, green, blue, 1F).endVertex();
		bb.pos(x, y + height, z).color(red, green, blue, 1F).endVertex();
		bb.pos(x + width, y + height, z).color(red, green, blue, 1F).endVertex();
		bb.pos(x + width, y, z).color(red, green, blue, 1F).endVertex();
		tess.draw();
		// White Gradient.
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(1F, 1F, 1F, 1F).endVertex();
		bb.pos(x, y + height, z).color(1F, 1F, 1F, 1F).endVertex();
		bb.pos(x + width, y + height, z).color(1, 1, 1, 0F).endVertex();
		bb.pos(x + width, y, z).color(1, 1, 1, 0F).endVertex();
		tess.draw();
		// Black Gradient
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(0, 0, 0, 0F).endVertex();
		bb.pos(x, y + height, z).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y + height, z).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y, z).color(0, 0, 0, 0F).endVertex();
		tess.draw();
		shadeModel(GL11.GL_FLAT);
		disableBlend();
		enableAlpha();
		enableTexture2D();
	}

	private static void drawGradientRect(double x, double y, double z, double width, double height, int color1, int color2) {
		int alpha1 = (color1 >> 24) & 0xFF;
		int red1 = (color1 >> 16) & 0xFF;
		int green1 = (color1 >> 8) & 0xFF;
		int blue1 = (color1 >> 0) & 0xFF;
		int alpha2 = (color2 >> 24) & 0xFF;
		int red2 = (color2 >> 16) & 0xFF;
		int green2 = (color2 >> 8) & 0xFF;
		int blue2 = (color2 >> 0) & 0xFF;
		disableTexture2D();
		enableBlend();
		disableAlpha();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		shadeModel(GL11.GL_SMOOTH);
		color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(red1, green1, blue1, alpha1).endVertex();
		bb.pos(x, y + height, z).color(red2, green2, blue2, alpha2).endVertex();
		bb.pos(x + width, y + height, z).color(red2, green2, blue2, alpha2).endVertex();
		bb.pos(x + width, y, z).color(red1, green1, blue1, alpha1).endVertex();
		tess.draw();
		shadeModel(GL11.GL_FLAT);
		disableBlend();
		enableAlpha();
		enableTexture2D();
	}
}
