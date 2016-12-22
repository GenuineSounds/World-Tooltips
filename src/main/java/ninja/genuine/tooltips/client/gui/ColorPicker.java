package ninja.genuine.tooltips.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import ninja.genuine.tooltips.WorldTooltips;

public class ColorPicker extends GuiScreen {

	static final ResourceLocation COLOR_STRIP = new ResourceLocation(WorldTooltips.MODID, "gui/color-strip.png");
	final GuiScreen parent;
	final Property prop;
	IntBuffer colorBuffer = BufferUtils.createIntBuffer(4);
	int selectedHue, selectedColor, pickerX = 20, pickerY = 50, hueWidth = 8, pickerWidth = 100, pickerHeight = 100;

	public ColorPicker(GuiScreen parent, Property prop) {
		this.parent = parent;
		this.prop = prop;
		try {
			selectedColor = Integer.decode(prop.getString());
		} catch (NumberFormatException e1) {
			try {
				selectedColor = Integer.decode(prop.getDefault());
			} catch (NumberFormatException e2) {
				selectedColor = 0x00FF00;
			}
		}
		float[] hsb = new float[3];
		hsb = Color.RGBtoHSB((selectedColor >> 16) & 0xFF, (selectedColor >> 8) & 0xFF, (selectedColor & 0xFF), hsb);
		hsb[1] = hsb[2] = 1F;
		selectedHue = Color.HSBtoRGB(hsb[0], 1F, 1F);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			mc.displayGuiScreen(parent);
		else if (button.id == 1) {
			prop.set("0x" + Integer.toHexString(selectedColor).toUpperCase());
			WorldTooltips.instance.sync();
			WorldTooltips.instance.events.sync();
			mc.displayGuiScreen(parent);
		}
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		addButton(new GuiButton(0, sr.getScaledWidth() / 2 - 104, sr.getScaledHeight() - 35, "Back"));
		addButton(new GuiButton(1, sr.getScaledWidth() / 2 - 104, sr.getScaledHeight() - 60, "Done"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		ScaledResolution sr = new ScaledResolution(mc);
		pickerX = sr.getScaledWidth() / 2 - 110;
		drawGradientRect(pickerX - 1, pickerY - 1, pickerX + pickerWidth * 2 + hueWidth + 3, pickerY + pickerHeight + 1, 0xFF404040, 0xFF404040);
		drawHueBar(pickerX, pickerY, hueWidth, pickerHeight);
		drawColorGradient(pickerX + hueWidth + 1, pickerY, pickerWidth, pickerHeight);
		drawGradientRect(pickerX + pickerWidth + hueWidth + 2, pickerY, pickerX + pickerWidth * 2 + hueWidth + 2, pickerY + pickerHeight, selectedColor | 0xFF << 24, selectedColor | 0xFF << 24);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, clickedMouseButton);
		if (clickedMouseButton == 0) {
			GL11.glFlush();
			GL11.glFinish();
			GL11.glReadBuffer(GL11.GL_BACK);
			GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_INT, colorBuffer);
			int[] cl = new int[4];
			colorBuffer.get(cl);
			int tmp = ((cl[2] / 128) & 0xFF << 0) | ((cl[1] / 128) & 0xFF << 8) | ((cl[0] / 128) & 0xFF << 16) | 0xFF000000;
			if (mouseX >= pickerX && mouseY >= pickerY && mouseX < pickerX + hueWidth && mouseY < pickerY + pickerHeight)
				selectedHue = tmp & 0xFFFFFF;
			else if (mouseX >= pickerX + hueWidth + 1 && mouseY >= pickerY && mouseX < pickerX + pickerWidth + hueWidth && mouseY < pickerY + pickerHeight)
				selectedColor = tmp & 0xFFFFFF;
			System.out.println(Integer.toHexString(selectedHue));
			colorBuffer.clear();
		}
	}

	public void drawHueBar(double x, double y, double width, double height) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(COLOR_STRIP);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + width, y + height, zLevel).tex(0, 1).endVertex();
		vertexbuffer.pos(x + width, y, zLevel).tex(0, 0).endVertex();
		vertexbuffer.pos(x, y, zLevel).tex(1, 0).endVertex();
		vertexbuffer.pos(x, y + height, zLevel).tex(1, 1).endVertex();
		tessellator.draw();
	}

	private void drawColorGradient(double x, double y, double width, double height) {
		int color = selectedHue;
		float red, green, blue;
		red = (color >> 16 & 0xFF) / 255F;
		green = (color >> 8 & 0xFF) / 255F;
		blue = (color & 0xFF) / 255F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		// Color Gradient
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x, y, zLevel).color(red, green, blue, 1F).endVertex();
		vertexbuffer.pos(x, y + height, zLevel).color(red, green, blue, 1F).endVertex();
		vertexbuffer.pos(x + width, y + height, zLevel).color(red, green, blue, 1F).endVertex();
		vertexbuffer.pos(x + width, y, zLevel).color(red, green, blue, 1F).endVertex();
		tessellator.draw();
		// White Gradient.
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x, y, zLevel).color(1F, 1F, 1F, 1F).endVertex();
		vertexbuffer.pos(x, y + height, zLevel).color(1F, 1F, 1F, 1F).endVertex();
		vertexbuffer.pos(x + width, y + height, zLevel).color(red, green, blue, 0F).endVertex();
		vertexbuffer.pos(x + width, y, zLevel).color(red, green, blue, 0F).endVertex();
		tessellator.draw();
		// Black Gradient
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x, y, zLevel).color(red, green, blue, 0F).endVertex();
		vertexbuffer.pos(x, y + height, zLevel).color(0F, 0F, 0F, 1F).endVertex();
		vertexbuffer.pos(x + width, y + height, zLevel).color(0F, 0F, 0F, 1F).endVertex();
		vertexbuffer.pos(x + width, y, zLevel).color(red, green, blue, 0F).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
