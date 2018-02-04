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
import net.minecraft.client.resources.I18n;
import ninja.genuine.tooltips.client.render.RenderHelper;

public class GuiColorPicker extends GuiScreen {

	private GuiScreen parent;
	private GuiTextField text;
	private int hue, color;
	@SuppressWarnings("unused")
	private int hueSliderPos;
	private int pickerX, pickerY, hueWidth = 10, pickerWidth = 128, pickerHeight = 128;
	private IntBuffer colorBuffer = BufferUtils.createIntBuffer(4);

	public GuiColorPicker(GuiScreen parent, GuiTextField text, String defaultText) {
		this.parent = parent;
		this.text = text;
		try {
			color = Integer.decode(text.getText());
		} catch (NumberFormatException e) {
			try {
				color = Integer.decode(defaultText);
			} catch (NumberFormatException e1) {
				color = 0x00FF00;
			}
		}
		float[] hsb = new float[3];
		hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color & 0xFF), hsb);
		hsb[1] = hsb[2] = 1F;
		hue = Color.HSBtoRGB(hsb[0], 1F, 1F);
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		addButton(new GuiButton(0, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 30, I18n.format("gui.cancel")));
		addButton(new GuiButton(1, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 55, I18n.format("gui.done")));
		pickerX = sr.getScaledWidth() / 2 - (pickerWidth + hueWidth);
		pickerY = sr.getScaledHeight() / 2 - pickerHeight / 2 - 20;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(mc);
		drawDefaultBackground();
		drawGradientRect(pickerX - 2, pickerY - 1, pickerX + pickerWidth * 2 + hueWidth + 2, pickerY + pickerHeight + 1, 0xFF404040, 0xFF404040);
		RenderHelper.drawHuePicker(pickerX, pickerY, zLevel, hueWidth, pickerHeight);
		RenderHelper.drawColorPicker(pickerX + hueWidth, pickerY, zLevel, pickerWidth, pickerHeight, hue);
		drawGradientRect(pickerX + pickerWidth + hueWidth + 1, pickerY, pickerX + pickerWidth * 2 + hueWidth + 1, pickerY + pickerHeight, color | 0xFF << 24, color | 0xFF << 24);
		fontRenderer.drawString("Pick a color", sr.getScaledWidth() / 2 - fontRenderer.getStringWidth("Pick a color") / 2, pickerY - 26, 0xFFFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, clickedMouseButton);
		mouseClick(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		mouseClick(mouseX, mouseY, clickedMouseButton);
	}

	private void mouseClick(int mouseX, int mouseY, int clickedMouseButton) {
		if (clickedMouseButton != 0)
			return;
		GL11.glFlush();
		GL11.glFinish();
		GL11.glReadBuffer(GL11.GL_BACK);
		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_INT, colorBuffer);
		int[] cl = new int[4];
		colorBuffer.get(cl);
		int tmp = ((cl[2] / 128) & 0xFF << 0) | ((cl[1] / 128) & 0xFF << 8) | ((cl[0] / 128) & 0xFF << 16) | 0xFF000000;
		if (mouseX >= pickerX && mouseY >= pickerY && mouseX < pickerX + hueWidth && mouseY < pickerY + pickerHeight) {
			hue = tmp & 0xFFFFFF;
			hueSliderPos = mouseY;
		} else if (mouseX >= pickerX + hueWidth + 1 && mouseY >= pickerY && mouseX < pickerX + pickerWidth + hueWidth && mouseY < pickerY + pickerHeight)
			color = tmp & 0xFFFFFF;
		colorBuffer.clear();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			mc.displayGuiScreen(parent);
		else if (button.id == 1) {
			text.setText("0x" + Integer.toHexString(color).toUpperCase());
			mc.displayGuiScreen(parent);
		}
	}
}
