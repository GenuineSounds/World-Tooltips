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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import ninja.genuine.tooltips.client.Tooltip;

@SuppressWarnings("unused")
public class RenderHelper {

	public static void renderTooltip(Tooltip tooltip, double partialTicks) {
		final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		final EntityItem e = tooltip.getEntity();
		final double interpX = rm.viewerPosX - (e.posX - (e.prevPosX - e.posX) * partialTicks);
		final double interpY = rm.viewerPosY - 0.65 - (e.posY - (e.prevPosY - e.posY) * partialTicks);
		final double interpZ = rm.viewerPosZ - (e.posZ - (e.prevPosZ - e.posZ) * partialTicks);
		pushMatrix();
		pushAttrib();
		enableRescaleNormal();
		enableAlpha();
		alphaFunc(516, 0.1F);
		enableBlend();
		disableDepth();
		blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		color(1, 1, 1, 1);
		translate(-interpX, -interpY, -interpZ);
		rotate(rm.playerViewY + 180, 0, -1, 0);
		rotate(rm.playerViewX, -1, 0, 0);
		scale(tooltip.scale, -tooltip.scale, tooltip.scale);
		renderTooltipTile(tooltip);
		renderTooltipText(tooltip);
		scale(1 / tooltip.scale, 1 / -tooltip.scale, 1 / tooltip.scale);
		rotate(rm.playerViewX, 1, 0, 0);
		rotate(rm.playerViewY - 180, 0, 1, 0);
		translate(interpX, interpY, interpZ);
		enableDepth();
		disableAlpha();
		disableRescaleNormal();
		disableLighting();
		popAttrib();
		popMatrix();
	}

	private static void renderTooltipTile(Tooltip tooltip) {
		final int x = -tooltip.getWidth() / 2;
		final int y = -tooltip.getHeight() / 2;
		final int w = tooltip.getWidth();
		final int h = tooltip.getHeight();
		final int c1 = tooltip.colorBackground;
		final int c2 = tooltip.colorOutline;
		final int c3 = tooltip.colorOutlineShade;
		renderStyle1(x, y, w, h, c1, c2, c3);
	}

	private static void renderStyle1(int x, int y, int w, int h, int c1, int c2, int c3) {
		// Background
		drawRect(x - 3 + 0, y - 4 + 0, 0, w + 6, 1 + 0, c1);
		drawRect(x + w + 3, y - 3 + 0, 0, 1 + 0, h + 6, c1);
		drawRect(x - 3 + 0, y + h + 3, 0, w + 6, 1 + 0, c1);
		drawRect(x - 4 + 0, y - 3 + 0, 0, 1 + 0, h + 6, c1);
		drawRect(x - 2 + 0, y - 2 + 0, 0, w + 4, h + 4, c1);
		// Outline
		drawRect(x - 3 + 0, y - 3 + 0, 0, w + 6, 1 + 0, c2);
		drawGradientRect(x + w + 2, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
		drawRect(x - 3 + 0, y + h + 2, 0, w + 6, 1 + 0, c3);
		drawGradientRect(x - 3 + 0, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
	}

	private static void renderStyle2(int x, int y, int w, int h, int c1, int c2, int c3) {
		drawRect(x - 2 + 0, y - 2 + 0, 0, w + 4, h + 4, c1);
		drawRect(x - 2 + 0, y - 3 + 0, 0, w + 4, 1 + 0, c2);
		drawGradientRect(x + w + 2, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
		drawRect(x - 2 + 0, y + h + 2, 0, w + 4, 1 + 0, c3);
		drawGradientRect(x - 3 + 0, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
	}

	private static void renderStyle3(int x, int y, int w, int h, int c1, int c2, int c3) {
		drawRect(x - 2 + 0, y - 2 + 0, 0, w + 4, h + 4, c1);
		drawRect(x - 3 + 0, y - 3 + 0, 0, w + 6, 1 + 0, c2);
		drawGradientRect(x + w + 2, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
		drawRect(x - 3 + 0, y + h + 2, 0, w + 6, 1 + 0, c3);
		drawGradientRect(x - 3 + 0, y - 2 + 0, 0, 1 + 0, h + 4, c2, c3);
	}

	private static void renderStyle4(int x, int y, int w, int h, int c1, int c2, int c3) {
		drawRect(x - 2 + 0, y - 2 + 0, 0, w + 4, h + 4, c1);
	}

	private static void renderTooltipText(Tooltip tooltip) {
		if ((tooltip.alpha & 0xFC000000) == 0)
			return;
		int x = -tooltip.getWidth() / 2;
		int y = -tooltip.getHeight() / 2;
		pushMatrix();
		enableBlend();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		for (int i = 0; i < tooltip.getText().size(); i++) {
			String s = tooltip.getText().get(i);
			if (i == 0)
				s = tooltip.formattingColor() + s;
			Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, 0xFFFFFF | tooltip.alpha, true);
			if (i == 0)
				y += 2;
			y += 10;
		}
		disableBlend();
		popMatrix();
	}

	public static void drawHuePicker(double x, double y, double z, double width, double height) {
		height /= 5F;
		drawGradientRect(x - 1, y + 0 * height, z, width, height, 0xFFFF0000, 0xFFFFFF00);
		drawGradientRect(x - 1, y + 1 * height, z, width, height, 0xFFFFFF00, 0xFF00FF00);
		drawGradientRect(x - 1, y + 2 * height, z, width, height, 0xFF00FF00, 0xFF00FFFF);
		drawGradientRect(x - 1, y + 3 * height, z, width, height, 0xFF00FFFF, 0xFF0000FF);
		drawGradientRect(x - 1, y + 4 * height, z, width, height, 0xFF0000FF, 0xFFFF0000);
	}

	public static void drawColorPicker(double x, double y, double z, double w, double h, int hue) {
		final int r = hue >> 16 & 0xFF;
		final int g = hue >> 8 & 0xFF;
		final int b = hue >> 0 & 0xFF;
		disableTexture2D();
		enableBlend();
		disableAlpha();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		shadeModel(GL11.GL_SMOOTH);
		color(1, 1, 1, 1);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		// Color Gradient
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x + 0, y + 0, z).color(r, g, b, 0xFF).endVertex();
		bb.pos(x + 0, y + h, z).color(r, g, b, 0xFF).endVertex();
		bb.pos(x + w, y + h, z).color(r, g, b, 0xFF).endVertex();
		bb.pos(x + w, y + 0, z).color(r, g, b, 0xFF).endVertex();
		tess.draw();
		// White Gradient.
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x + 0, y + 0, z).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
		bb.pos(x + 0, y + h, z).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
		bb.pos(x + w, y + h, z).color(0xFF, 0xFF, 0xFF, 0).endVertex();
		bb.pos(x + w, y + 0, z).color(0xFF, 0xFF, 0xFF, 0).endVertex();
		tess.draw();
		// Black Gradient
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x + 0, y + 0, z).color(0, 0, 0, 0).endVertex();
		bb.pos(x + 0, y + h, z).color(0, 0, 0, 0xFF).endVertex();
		bb.pos(x + w, y + h, z).color(0, 0, 0, 0xFF).endVertex();
		bb.pos(x + w, y + 0, z).color(0, 0, 0, 0).endVertex();
		tess.draw();
		shadeModel(GL11.GL_FLAT);
		disableBlend();
		enableAlpha();
		enableTexture2D();
	}

	public static void drawRect(double x, double y, double z, double w, double h, int c1) {
		drawGradientRect(x, y, z, w, h, c1, c1, c1, c1);
	}

	public static void drawGradientRect(double x, double y, double z, double w, double h, int c1) {
		int alpha = c1 >> 24 & 0xFF;
		int c2 = ((c1 & 0xFEFEFE) >> 1) | alpha;
		drawGradientRect(x, y, z, w, h, c1, c2, c2, c1);
	}

	public static void drawGradientRect(double x, double y, double z, double w, double h, int c1, int c2) {
		drawGradientRect(x, y, z, w, h, c1, c2, c2, c1);
	}

	public static void drawGradientRect(double x, double y, double z, double w, double h, int c1, int c2, int c3, int c4) {
		final int a1 = c1 >> 24 & 0xFF;
		final int r1 = c1 >> 16 & 0xFF;
		final int g1 = c1 >> 8 & 0xFF;
		final int b1 = c1 >> 0 & 0xFF;
		final int a2 = c2 >> 24 & 0xFF;
		final int r2 = c2 >> 16 & 0xFF;
		final int g2 = c2 >> 8 & 0xFF;
		final int b2 = c2 >> 0 & 0xFF;
		final int a3 = c3 >> 24 & 0xFF;
		final int r3 = c3 >> 16 & 0xFF;
		final int g3 = c3 >> 8 & 0xFF;
		final int b3 = c3 >> 0 & 0xFF;
		final int a4 = c4 >> 24 & 0xFF;
		final int r4 = c4 >> 16 & 0xFF;
		final int g4 = c4 >> 8 & 0xFF;
		final int b4 = c4 >> 0 & 0xFF;
		disableTexture2D();
		enableBlend();
		disableAlpha();
		tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		shadeModel(GL11.GL_SMOOTH);
		color(1, 1, 1, 1);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x + 0, y + 0, z).color(r1, g1, b1, a1).endVertex();
		bb.pos(x + 0, y + h, z).color(r2, g2, b2, a2).endVertex();
		bb.pos(x + w, y + h, z).color(r3, g3, b3, a3).endVertex();
		bb.pos(x + w, y + 0, z).color(r4, g4, b4, a4).endVertex();
		tess.draw();
		shadeModel(GL11.GL_FLAT);
		disableBlend();
		enableAlpha();
		enableTexture2D();
	}
}
