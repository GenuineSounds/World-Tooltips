package ninja.genuine.tooltips.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import ninja.genuine.tooltips.Constants;

public class GuiColorPicker extends GuiScreen {

	private static final ResourceLocation COLOR_STRIP = new ResourceLocation(Constants.MODID, "gui/color-strip.png");
	private GuiScreen parent;
	private GuiTextField text;
	private IntBuffer colorBuffer = BufferUtils.createIntBuffer(4);
	private int selectedHue, selectedColor;
	private int pickerX = 20, pickerY = 50, hueWidth = 8, pickerWidth = 100, pickerHeight = 100;

	public GuiColorPicker(GuiScreen parent, GuiTextField text, String defaultText) {
		this.parent = parent;
		this.text = text;
		try {
			selectedColor = Integer.decode(text.getText());
		} catch (NumberFormatException e) {
			try {
				selectedColor = Integer.decode(defaultText);
			} catch (NumberFormatException e1) {
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
			text.setText("0x" + Integer.toHexString(selectedColor).toUpperCase());
			mc.displayGuiScreen(parent);
		}
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		addButton(new GuiButton(0, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 30, "Back"));
		addButton(new GuiButton(1, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 55, "Done"));
		pickerX = sr.getScaledWidth() / 2 - 110;
		pickerY = sr.getScaledHeight() / 2 - 80;
		System.out.println(parent.getClass());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawGradientRect(pickerX - 1, pickerY - 1, pickerX + pickerWidth * 2 + hueWidth + 3, pickerY + pickerHeight + 1, 0xFF404040, 0xFF404040);
		drawHueBar(pickerX, pickerY, hueWidth, pickerHeight);
		drawColorGradient(pickerX + hueWidth + 1, pickerY, pickerWidth, pickerHeight);
		drawGradientRect(pickerX + pickerWidth + hueWidth + 2, pickerY, pickerX + pickerWidth * 2 + hueWidth + 2, pickerY + pickerHeight, selectedColor | 0xFF << 24, selectedColor | 0xFF << 24);
		fontRenderer.drawString("Pick a color", pickerX, pickerY - 20, 0xFFFFFFFF);
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
			colorBuffer.clear();
		}
	}

	public void drawHueBar(double x, double y, double width, double height) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(COLOR_STRIP);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bb = tessellator.getBuffer();
		bb.begin(7, DefaultVertexFormats.POSITION_TEX);
		bb.pos(x + width, y + height, zLevel).tex(0, 1).endVertex();
		bb.pos(x + width, y, zLevel).tex(0, 0).endVertex();
		bb.pos(x, y, zLevel).tex(1, 0).endVertex();
		bb.pos(x, y + height, zLevel).tex(1, 1).endVertex();
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
		BufferBuilder bb = tessellator.getBuffer();
		// Color Gradient
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, zLevel).color(red, green, blue, 1F).endVertex();
		bb.pos(x, y + height, zLevel).color(red, green, blue, 1F).endVertex();
		bb.pos(x + width, y + height, zLevel).color(red, green, blue, 1F).endVertex();
		bb.pos(x + width, y, zLevel).color(red, green, blue, 1F).endVertex();
		tessellator.draw();
		// White Gradient.
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, zLevel).color(1F, 1F, 1F, 1F).endVertex();
		bb.pos(x, y + height, zLevel).color(1F, 1F, 1F, 1F).endVertex();
		bb.pos(x + width, y + height, zLevel).color(red, green, blue, 0F).endVertex();
		bb.pos(x + width, y, zLevel).color(red, green, blue, 0F).endVertex();
		tessellator.draw();
		// Black Gradient
		bb.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(x, y, zLevel).color(red, green, blue, 0F).endVertex();
		bb.pos(x, y + height, zLevel).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y + height, zLevel).color(0F, 0F, 0F, 1F).endVertex();
		bb.pos(x + width, y, zLevel).color(red, green, blue, 0F).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
