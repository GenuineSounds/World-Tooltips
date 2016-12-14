package ninja.genuine.tooltips.client.render;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ninja.genuine.tooltips.WorldTooltips;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mojang.realmsclient.gui.ChatFormatting;

public class TooltipRenderer {

	private final int colorPrimary, colorOutline, colorSecondary;

	public TooltipRenderer() {
		colorPrimary = WorldTooltips.colorPrimary & 0xFFFFFF | 0xD0000000;
		colorOutline = WorldTooltips.colorOutline & 0xFFFFFF | 0x90000000;
		colorSecondary = (colorOutline & 0xFEFEFE) >> 1 | colorOutline & 0xFF000000;
	}

	public void renderTooltip(Minecraft mc, EntityItem item, List<String> tooltip, double partialTicks) {
		int maxwidth = 0;
		for (int line = 0; line < tooltip.size(); line++) {
			final int swidth = mc.fontRendererObj.getStringWidth(tooltip.get(line));
			if (swidth > maxwidth)
				maxwidth = swidth;
		}
		int w = maxwidth;
		int h = 8;
		if (tooltip.size() > 1)
			h += 2 + (tooltip.size() - 1) * 10;
		int x = -w / 2;
		int y = -h;
		RenderHelper.start();
		double interpX = mc.getRenderManager().viewerPosX - (item.posX - (item.prevPosX - item.posX) * partialTicks);
		double interpY = mc.getRenderManager().viewerPosY - (item.posY - (item.prevPosY - item.posY) * partialTicks);
		double interpZ = mc.getRenderManager().viewerPosZ - (item.posZ - (item.prevPosZ - item.posZ) * partialTicks);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		GlStateManager.translate(-interpX, -(interpY - 0.65), -interpZ);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY + 180, 0, 1, 0);
		GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
		double scale = interpDistance / 512;
		if (scale <= 0.01)
			scale = 0.01;
		GlStateManager.scale(scale, -scale, scale);
		GlStateManager.disableDepth();
		RenderHelper.renderTooltipTile(x, y, w, h, colorPrimary, colorOutline, colorSecondary);
		RenderHelper.renderTooltipText(tooltip, item, x, y);
		GlStateManager.enableDepth();
		GlStateManager.scale(1F / scale, 1F / -scale, 1F / scale);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);
		GlStateManager.rotate(mc.getRenderManager().playerViewY - 180, 0, 1, 0);
		GlStateManager.translate(interpX, interpY - 0.65, interpZ);
		RenderHelper.end();
	}
}
