package ninja.genuine.tooltips.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.config.Property;

public class ColorButton extends GuiButton {

	public Property prop;

	public ColorButton(int buttonId, int x, int y, Property prop) {
		super(buttonId, x, y, 20, 20, prop.getString());
		this.prop = prop;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		int color = 0;
		try {
			color = Integer.decode(prop.getString());
		} catch (NumberFormatException e) {
			color = Integer.decode(prop.getDefault());
		}
		if (this.visible) {
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
			this.drawGradientRect(this.xPosition + 2, this.yPosition + 2, this.xPosition + 20 - 2, this.yPosition + 20 - 2, color | 0xFF000000, color | 0xFF000000);
			this.mouseDragged(mc, mouseX, mouseY);
		}
	}
}
