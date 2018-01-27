package ninja.genuine.tooltips.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.StringEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.client.config.IConfigElement;
import ninja.genuine.tooltips.Config;
import ninja.genuine.tooltips.Constants;
import ninja.genuine.tooltips.WorldTooltips;

public class GuiConfigTooltips extends GuiConfig {

	public GuiConfigTooltips(GuiScreen parent) {
		super(parent, new ArrayList<>(), Constants.MODID, Constants.GUIID, false, false, Constants.NAME, "Appearance configuration");
		ConfigElement elementGeneral = new ConfigElement(Config.getCategory(Config.category_general));
		ConfigElement elementAppearance = new ConfigElement(Config.getCategory(Config.category_appearance));
		ConfigElement elementBehavior = new ConfigElement(Config.getCategory(Config.category_behavior));
		configElements.addAll(elementGeneral.getChildElements());
		configElements.addAll(elementAppearance.getChildElements());
		configElements.addAll(elementBehavior.getChildElements());
	}

	public static class ColorEntry extends StringEntry {

		private GuiColorButton button;

		public ColorEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			button = new GuiColorButton(11, owningEntryList.controlX + 2, owningEntryList.top - 1, configElement.get().toString(), configElement.getDefault().toString());
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			boolean isChanged = isChanged();
			if (drawLabel) {
				String label = (!isValidValue ? TextFormatting.RED.toString() : (isChanged ? TextFormatting.WHITE.toString() : TextFormatting.GRAY.toString())) + (isChanged ? TextFormatting.ITALIC.toString() : "") + name;
				mc.fontRenderer.drawString(label, owningScreen.entryList.labelX, y + slotHeight / 2 - mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
			}
			btnUndoChanges.x = owningEntryList.scrollBarX - 44;
			btnUndoChanges.y = y;
			btnUndoChanges.enabled = enabled() && isChanged;
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
			System.out.println(button.mousePressed(mc, mouseX, mouseY));
			if (button.mousePressed(mc, mouseX, mouseY))
				mc.displayGuiScreen(new GuiColorPicker(owningScreen, textFieldValue, beforeValue));
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) {
			super.keyTyped(eventChar, eventKey);
			WorldTooltips.instance.sync();
		}

		@Override
		public void updateCursorCounter() {
			super.updateCursorCounter();
		}
	}
}