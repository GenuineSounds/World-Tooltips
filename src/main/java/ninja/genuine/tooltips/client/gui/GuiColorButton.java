package ninja.genuine.tooltips.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.config.Property;

public class GuiColorButton extends GuiButton {

	public String value;
	public String defaultV;

	public GuiColorButton(int buttonId, int x, int y, Property property) {
		this(buttonId, x, y, property.getName(), property.getDefault());
	}

	public GuiColorButton(int buttonId, int x, int y, String value, String defaultV) {
		super(buttonId, x, y, 20, 20, value);
		setValues(value, defaultV);
	}

	public void setValues(String value, String defaultV) {
		this.value = value;
		this.defaultV = defaultV;
	}

	public void update(String value) {
		this.value = value;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!this.visible)
			return;
		int color = 0;
		try {
			color = Integer.decode(value);
		} catch (NumberFormatException e) {
			color = Integer.decode(defaultV);
		}
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		int i = this.getHoverState(this.hovered);
		mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.drawGradientRect(this.xPosition + 2, this.yPosition + 2, this.xPosition + 20 - 2, this.yPosition + 20 - 2, color | 0xFF000000, color | 0xFF000000);
		this.mouseDragged(mc, mouseX, mouseY);
	}
}
