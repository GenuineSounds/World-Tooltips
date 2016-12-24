package ninja.genuine.tooltips.client.gui;

import java.io.IOException;
import java.util.regex.Pattern;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import ninja.genuine.tooltips.Config;
import ninja.genuine.tooltips.WorldTooltips;

public class ColorConfigGui extends GuiScreen {

	private GuiScreen parent;
	private GuiTextField textboxOutline, textboxBackground;
	private String strOutlineOrig, strBackgroundOrig;

	public ColorConfigGui(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(mc);
		strOutlineOrig = Config.getInstance().getOutline().getString();
		textboxOutline = new GuiTextField(10, mc.fontRendererObj, sr.getScaledWidth() / 2 + 50, sr.getScaledHeight() / 2 - 80, 100, 20);
		textboxOutline.setText(Config.getInstance().getOutline().getString());
		textboxOutline.setMaxStringLength(10);
		textboxOutline.setValidator((input) -> Pattern.compile("(0[x#])?[0-9a-fA-F]{0,8}").asPredicate().test(input));
		strBackgroundOrig = Config.getInstance().getBackground().getString();
		textboxBackground = new GuiTextField(10, mc.fontRendererObj, sr.getScaledWidth() / 2 + 50, sr.getScaledHeight() / 2 - 50, 100, 20);
		textboxBackground.setText(Config.getInstance().getBackground().getString());
		textboxBackground.setMaxStringLength(10);
		textboxBackground.setValidator((input) -> Pattern.compile("(0[x#])?[0-9a-fA-F]{0,8}").asPredicate().test(input));
		GuiButton back = new GuiButton(0, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 30, 200, 20, "Back");
		GuiButton done = new GuiButton(1, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() - 55, 200, 20, "Done");
		ColorButton outlineButton = new ColorButton(5, sr.getScaledWidth() / 2 + 130, sr.getScaledHeight() / 2 - 80, Config.getInstance().getOutline());
		ColorButton backgroundButton = new ColorButton(6, sr.getScaledWidth() / 2 + 130, sr.getScaledHeight() / 2 - 50, Config.getInstance().getBackground());
		addButton(outlineButton);
		addButton(backgroundButton);
		addButton(done);
		addButton(back);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		ScaledResolution sr = new ScaledResolution(mc);
		fontRendererObj.drawString(Config.getInstance().getOutline().getName(), sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 - 75, 0xFFFFFFFF);
		fontRendererObj.drawString(Config.getInstance().getBackground().getName(), sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 - 45, 0xFFFFFFFF);
		fontRendererObj.drawSplitString("Remember, you must select 'Override Outline Color' to display this outline color instead of rarity color.", sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 + 0, 300, 0xFF808080);
		textboxOutline.drawTextBox();
		textboxBackground.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		textboxOutline.textboxKeyTyped(typedChar, keyCode);
		textboxBackground.textboxKeyTyped(typedChar, keyCode);
		try {
			Config.getInstance().getOutline().set(textboxOutline.getText());
		} catch (Exception e) {}
		try {
			Config.getInstance().getBackground().set(textboxBackground.getText());
		} catch (Exception e) {}
		WorldTooltips.instance.sync();
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		textboxOutline.mouseClicked(mouseX, mouseY, mouseButton);
		textboxBackground.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.id <= 1) {
			mc.displayGuiScreen(this.parent);
			if (button.id == 0) {
				try {
					Config.getInstance().getOutline().set(strOutlineOrig);
				} catch (Exception e) {}
				try {
					Config.getInstance().getBackground().set(strBackgroundOrig);
				} catch (Exception e) {}
			}
			WorldTooltips.instance.sync();
		} else if (button.id == 5)
			this.mc.displayGuiScreen(new ColorPicker(this, Config.getInstance().getOutline()));
		else if (button.id == 6)
			this.mc.displayGuiScreen(new ColorPicker(this, Config.getInstance().getBackground()));
	}
}
