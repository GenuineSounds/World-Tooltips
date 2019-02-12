package ninja.genuine.tooltips.client.config;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.WHITE;

import ninja.genuine.tooltips.client.gui.GuiColorButton;
import ninja.genuine.tooltips.client.gui.GuiColorPicker;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.StringEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ColorEntry extends StringEntry {

	private GuiColorButton button;

	public ColorEntry(GuiConfig parent, GuiConfigEntries entries, IConfigElement element) {
		super(parent, entries, element);
		button = new GuiColorButton(11, entries.controlX + 2, entries.top - 1, element.get().toString(), element.getDefault().toString());
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
		if (drawLabel) {
			// String label = (!isValidValue ? RED.toString() : (isChanged ?
			// WHITE.toString() : GRAY.toString())) + (isChanged ? ITALIC.toString() : "") +
			// name;
			String label = name;
			if (isChanged())
				label = ITALIC.toString() + label;
			if (!isValidValue)
				label = RED.toString() + label;
			else if (isChanged())
				label = WHITE.toString() + label;
			else
				label = GRAY.toString() + label;
			mc.fontRenderer.drawString(label, owningScreen.entryList.labelX, y + slotHeight / 2 - mc.fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);
		}
		btnUndoChanges.x = owningEntryList.scrollBarX - 44;
		btnUndoChanges.y = y;
		btnUndoChanges.enabled = enabled() && isChanged();
		btnUndoChanges.drawButton(mc, mouseX, mouseY, partial);
		btnDefault.x = owningEntryList.scrollBarX - 22;
		btnDefault.y = y;
		btnDefault.enabled = enabled() && !isDefault();
		btnDefault.drawButton(mc, mouseX, mouseY, partial);
		if (tooltipHoverChecker == null)
			tooltipHoverChecker = new HoverChecker(y, y + slotHeight, x, owningScreen.entryList.controlX - 8, 800);
		else
			tooltipHoverChecker.updateBounds(y, y + slotHeight, x, owningScreen.entryList.controlX - 8);
		textFieldValue.x = owningEntryList.controlX + 2;
		textFieldValue.y = y + 1;
		textFieldValue.width = owningEntryList.controlWidth - 24;
		textFieldValue.setEnabled(enabled());
		button.update(textFieldValue.getText());
		button.x = owningEntryList.controlX + textFieldValue.width + 4;
		button.y = y - 1;
		textFieldValue.drawTextBox();
		button.drawButton(mc, mouseX, mouseY, partial);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (button.mousePressed(mc, mouseX, mouseY))
			mc.displayGuiScreen(new GuiColorPicker(owningScreen, textFieldValue, beforeValue));
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void keyTyped(char eventChar, int eventKey) {
		super.keyTyped(eventChar, eventKey);
		TooltipConfig.getInstance().save();
	}
}
