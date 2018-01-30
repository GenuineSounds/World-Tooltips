package ninja.genuine.tooltips.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import ninja.genuine.tooltips.Constants;
import ninja.genuine.tooltips.client.Tooltip;

public class RenderHelper {

	private static final ResourceLocation COLOR_STRIP = new ResourceLocation(Constants.MODID, "gui/color-strip.png");

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

	public static void renderTooltipTile(int x, int y, int width, int height, int colorPrimary, int colorOutline, int colorBackground) {
		drawGradientRect(x - 3, y - 4, width + 6, 1, colorPrimary, colorPrimary);
		drawGradientRect(x - 3, y + height + 3, width + 6, 1, colorPrimary, colorPrimary);
		drawGradientRect(x - 3, y - 3, width + 6, height + 6, colorPrimary, colorPrimary);
		drawGradientRect(x - 4, y - 3, 1, height + 6, colorPrimary, colorPrimary);
		drawGradientRect(x + width + 3, y - 3, 1, height + 6, colorPrimary, colorPrimary);
		drawGradientRect(x - 3, y - 2, 1, height + 4, colorOutline, colorBackground);
		drawGradientRect(x + width + 2, y - 2, 1, height + 4, colorOutline, colorBackground);
		drawGradientRect(x - 3, y - 3, width + 6, 1, colorOutline, colorOutline);
		drawGradientRect(x - 3, y + height + 2, width + 6, 1, colorBackground, colorBackground);
	}

	public static void drawGradientRect(int x, int y, int width, int height, int colorOutline, int colorBackground) {
		int[] color1 = new int[4], color2 = new int[4];
		for (int i = 0; i < 4; i++) {
			color1[i] = colorOutline >> (i * 8) & 0xFF;
			color2[i] = colorBackground >> (i * 8) & 0xFF;
		}
		width += x;
		height += y;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(width, y, 0.0D).color(color1[2], color1[1], color1[0], color1[3]).endVertex();
		bb.pos(x, y, 0.0D).color(color1[2], color1[1], color1[0], color1[3]).endVertex();
		bb.pos(x, height, 0.0D).color(color2[2], color2[1], color2[0], color2[3]).endVertex();
		bb.pos(width, height, 0.0D).color(color2[2], color2[1], color2[0], color2[3]).endVertex();
		bb.endVertex();
		tess.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawHueBar(double x, double y, double z, double width, double height) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_STRIP);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(7, DefaultVertexFormats.POSITION_TEX);
		bb.pos(x + width, y + height, z).tex(0, 1).endVertex();
		bb.pos(x + width, y, z).tex(0, 0).endVertex();
		bb.pos(x, y, z).tex(1, 0).endVertex();
		bb.pos(x, y + height, z).tex(1, 1).endVertex();
		tess.draw();
	}

	public static void drawColorGradient(double x, double y, double z, double width, double height, int hue) {
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
		bb.pos(x + width, y + height, z).color(red, green, blue, 0F).endVertex();
		bb.pos(x + width, y, z).color(red, green, blue, 0F).endVertex();
		tess.draw();
		// Black Gradient
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, z).color(red, green, blue, 0F).endVertex();
		bb.pos(x, y + height, z).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y + height, z).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y, z).color(red, green, blue, 0F).endVertex();
		tess.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
