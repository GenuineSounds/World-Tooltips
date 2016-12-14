package ninja.genuine.tooltips.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class RenderHelper {

	public static void start() {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void end() {
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	public static void renderName(FontRenderer fr, ItemStack stack, int cols, int col, int rows, int row, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.4f * (cols / 2.0 - col) - 0.2f, 0.4f * (rows / 2.0 - row) - 0.15f, 0);
		GlStateManager.pushAttrib();
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.translate(0.2, 0, -0.1);
		GlStateManager.scale(0.01, 0.01, 0.01);
		String size = Integer.toString(stack.func_190916_E());
		int w = fr.getStringWidth(size);
		fr.drawStringWithShadow(size, -w, 0, color);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	public static void renderStack(RenderItem ri, ItemStack stack, int cols, int col, int rows, int row) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(0.4f * (cols / 2.0 - col) - 0.2f, 0.4f * (rows / 2.0 - row), 0);
		GlStateManager.pushMatrix();
		GlStateManager.rotate((float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL), 0, 1, 0);
		GlStateManager.scale(0.45, 0.45, 0.45);
		ri.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		if (stack.hasEffect()) {
			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
		}
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	public static void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		w += x;
		h += y;
		float a1 = (float) (color1 >> 24 & 255) / 255.0F;
		float r1 = (float) (color1 >> 16 & 255) / 255.0F;
		float g1 = (float) (color1 >> 8 & 255) / 255.0F;
		float b1 = (float) (color1 >> 0 & 255) / 255.0F;
		float a2 = (float) (color2 >> 24 & 255) / 255.0F;
		float r2 = (float) (color2 >> 16 & 255) / 255.0F;
		float g2 = (float) (color2 >> 8 & 255) / 255.0F;
		float b2 = (float) (color2 >> 0 & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer VertexBuffer = tessellator.getBuffer();
		VertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		VertexBuffer.pos(w, y, 0.0D).color(r1, g1, b1, a1).endVertex();
		VertexBuffer.pos(x, y, 0.0D).color(r1, g1, b1, a1).endVertex();
		VertexBuffer.pos(x, h, 0.0D).color(r2, g2, b2, a2).endVertex();
		VertexBuffer.pos(w, h, 0.0D).color(r2, g2, b2, a2).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void renderTooltipText(List<String> tooltip, EntityItem item, int drawx, int drawy) {
		GlStateManager.translate(0, 0, 1);
		for (int i = 0; i < tooltip.size(); i++) {
			String s = tooltip.get(i);
			if (i == 0)
				s = item.getEntityItem().getRarity().rarityColor.toString() + s;
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, drawx, drawy, -1);
			if (i == 0)
				drawy += 2;
			drawy += 10;
		}
		GlStateManager.translate(0, 0, -1);
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
}
