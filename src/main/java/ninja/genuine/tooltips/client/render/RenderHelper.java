package ninja.genuine.tooltips.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import ninja.genuine.tooltips.system.Tooltip;

public class RenderHelper {

	public static void renderTooltipText(Tooltip tooltip, int drawx, int drawy, int alpha) {
		for (int i = 0; i < tooltip.size(); i++) {
			String s = tooltip.getLine(i);
			if (i == 0)
				s = tooltip.getRarityColor() + s;
			Minecraft.getMinecraft().fontRenderer.drawString(s, drawx, drawy, 0xFFFFFF | alpha, true);
			if (i == 0)
				drawy += 2;
			drawy += 10;
		}
	}

	public static void renderTooltipTile(int x, int y, int w, int h, int colorPrimary, int colorOutline, int colorSecondary) {
		RenderHelper.drawGradientRect(x - 3, y - 4, w + 6, 1, colorPrimary, colorPrimary);
		RenderHelper.drawGradientRect(x - 3, y + h + 3, w + 6, 1, colorPrimary, colorPrimary);
		RenderHelper.drawGradientRect(x - 3, y - 3, w + 6, h + 6, colorPrimary, colorPrimary);
		RenderHelper.drawGradientRect(x - 4, y - 3, 1, h + 6, colorPrimary, colorPrimary);
		RenderHelper.drawGradientRect(x + w + 3, y - 3, 1, h + 6, colorPrimary, colorPrimary);
		RenderHelper.drawGradientRect(x - 3, y - 2, 1, h + 4, colorOutline, colorSecondary);
		RenderHelper.drawGradientRect(x + w + 2, y - 2, 1, h + 4, colorOutline, colorSecondary);
		RenderHelper.drawGradientRect(x - 3, y - 3, w + 6, 1, colorOutline, colorOutline);
		RenderHelper.drawGradientRect(x - 3, y + h + 2, w + 6, 1, colorSecondary, colorSecondary);
	}

	public static void start() {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void end() {
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	public static void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		w += x;
		h += y;
		final float alpha1 = (color1 >> 24 & 0xff) / 255F;
		final float red1 = (color1 >> 16 & 0xff) / 255F;
		final float green1 = (color1 >> 8 & 0xff) / 255F;
		final float blue1 = (color1 & 0xff) / 255F;
		final float alpha2 = (color2 >> 24 & 0xff) / 255F;
		final float red2 = (color2 >> 16 & 0xff) / 255F;
		final float green2 = (color2 >> 8 & 0xff) / 255F;
		final float blue2 = (color2 & 0xff) / 255F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
		tessellator.addVertex(w, y, 0);
		tessellator.addVertex(x, y, 0);
		tessellator.setColorRGBA_F(red2, green2, blue2, alpha2);
		tessellator.addVertex(x, h, 0);
		tessellator.addVertex(w, h, 0);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
