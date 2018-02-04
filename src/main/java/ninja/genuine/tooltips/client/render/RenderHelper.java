package ninja.genuine.tooltips.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

public class RenderHelper {

	public static void start3D() {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void end3D() {
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	public static void renderTooltipText(Tooltip tooltip, int x, int y, int alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		for (int i = 0; i < tooltip.size(); i++) {
			String s = tooltip.getLine(i);
			if (i == 0)
				s = tooltip.getRarityColor() + s;
			Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, 0xFFFFFF | alpha, true);
			if (i == 0)
				y += 2;
			y += 10;
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public static void renderTooltipTile(EntityItem item, int x, int y, int width, int height, int color1, int color2) {
		final int alpha = color1 & 0xFF000000;
		color2 = (Config.getInstance().isOverridingOutline() ? color2 : ModUtils.getRarityColor(item.getItem().getRarity().rarityColor)) | alpha;
		color2 &= 0xFFE0E0E0;
		int color3 = ((color2 & 0xFEFEFE) >> 1) | alpha;
		drawGradientRect(x - 3        , y - 4         , 0, width + 6, 1         , color1, color1);
		drawGradientRect(x - 3        , y + height + 3, 0, width + 6, 1         , color1, color1);
		drawGradientRect(x - 3        , y - 3         , 0, width + 6, height + 6, color1, color1);
		drawGradientRect(x - 4        , y - 3         , 0, 1        , height + 6, color1, color1);
		drawGradientRect(x + width + 3, y - 3         , 0, 1        , height + 6, color1, color1);
		drawGradientRect(x - 3        , y - 2         , 0, 1        , height + 4, color2, color3);
		drawGradientRect(x + width + 2, y - 2         , 0, 1        , height + 4, color2, color3);
		drawGradientRect(x - 3        , y - 3         , 0, width + 6, 1         , color2, color2);
		drawGradientRect(x - 3        , y + height + 2, 0, width + 6, 1         , color3, color3);
	}

	public static void drawHuePicker(double x, double y, double z, double width, double height) {
		final double sHeight = height / 5F;
		drawGradientRect(x - 1, y + 0 * sHeight, z, width, sHeight, 0xFFFF0000, 0xFFFFFF00);
		drawGradientRect(x - 1, y + 1 * sHeight, z, width, sHeight, 0xFFFFFF00, 0xFF00FF00);
		drawGradientRect(x - 1, y + 2 * sHeight, z, width, sHeight, 0xFF00FF00, 0xFF00FFFF);
		drawGradientRect(x - 1, y + 3 * sHeight, z, width, sHeight, 0xFF00FFFF, 0xFF0000FF);
		drawGradientRect(x - 1, y + 4 * sHeight, z, width, sHeight, 0xFF0000FF, 0xFFFF0000);
	}

	public static void drawColorPicker(double x, double y, double z, double width, double height, int hue) {
		float red, green, blue;
		red = (hue >> 16 & 0xFF) / 255F;
		green = (hue >> 8 & 0xFF) / 255F;
		blue = (hue & 0xFF) / 255F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	private static void drawGradientRect(double x, double y, double z, double width, double height, int color1, int color2) {
		final int alpha1 = (color1 >> 24) & 0xFF;
		final int red1 = (color1 >> 16) & 0xFF;
		final int green1 = (color1 >> 8) & 0xFF;
		final int blue1 = (color1 >> 0) & 0xFF;
		final int alpha2 = (color2 >> 24) & 0xFF;
		final int red2 = (color2 >> 16) & 0xFF;
		final int green2 = (color2 >> 8) & 0xFF;
		final int blue2 = (color2 >> 0) & 0xFF;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(red1, green1, blue1, alpha1).endVertex();
		bb.pos(x, y + height, z).color(red2, green2, blue2, alpha2).endVertex();
		bb.pos(x + width, y + height, z).color(red2, green2, blue2, alpha2).endVertex();
		bb.pos(x + width, y, z).color(red1, green1, blue1, alpha1).endVertex();
		tess.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
