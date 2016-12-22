package ninja.genuine.tooltips.client.gui;

import java.io.IOException;
import java.util.regex.Pattern;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import ninja.genuine.tooltips.WorldTooltips;

public class ColorConfigGui extends GuiScreen {

	public GuiScreen parent;
	GuiPageButtonList list;
	GuiTextField textOutline;
	GuiTextField textBackground;

	public ColorConfigGui(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		GuiButton back = new GuiButton(0, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 55, 200, 20, "Done");
		textOutline = new GuiTextField(10, mc.fontRendererObj, sr.getScaledWidth() - 180, 50, 100, 20);
		textBackground = new GuiTextField(10, mc.fontRendererObj, sr.getScaledWidth() - 180, 80, 100, 20);
		ColorButton outline = new ColorButton(5, sr.getScaledWidth() - 100, 50, WorldTooltips.instance.overrideOutlineColor);
		ColorButton background = new ColorButton(6, sr.getScaledWidth() - 100, 80, WorldTooltips.instance.colorBackground);
		textOutline.setText(WorldTooltips.instance.overrideOutlineColor.getString());
		textBackground.setText(WorldTooltips.instance.colorBackground.getString());
		textOutline.setMaxStringLength(10);
		textBackground.setMaxStringLength(10);
		textOutline.setValidator((input) -> Pattern.compile("(0[x#])?[0-9a-fA-F]{0,8}").asPredicate().test(input));
		textBackground.setValidator((input) -> Pattern.compile("(0[x#])?[0-9a-fA-F]{0,8}").asPredicate().test(input));
		this.addButton(background);
		this.addButton(outline);
		this.addButton(back);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		this.fontRendererObj.drawString("Outline Color", 100, 55, 0xFFFFFFFF);
		this.fontRendererObj.drawString("Background Color", 100, 85, 0xFFFFFFFF);
		textOutline.drawTextBox();
		textBackground.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		textOutline.textboxKeyTyped(typedChar, keyCode);
		textBackground.textboxKeyTyped(typedChar, keyCode);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.textOutline.mouseClicked(mouseX, mouseY, mouseButton);
		this.textBackground.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id == 0)
			this.mc.displayGuiScreen(this.parent);
		else if (button.id == 5)
			this.mc.displayGuiScreen(new ColorPicker(this, WorldTooltips.instance.overrideOutlineColor));
		else if (button.id == 6)
			this.mc.displayGuiScreen(new ColorPicker(this, WorldTooltips.instance.colorBackground));
	}
}
