package com.genuineflix.tooltip.system;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.genuineflix.tooltip.WorldTooltip;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class TooltipSystem {

	private static void drawGradientRect(final int x, final int y, int w, int h, final int color1, final int color2) {
		w += x;
		h += y;
		final float alpha1 = (color1 >> 24 & 0xff) / 255F;
		final float red1 = (color1 >> 16 & 0xff) / 255F;
		final float green1 = (color1 >> 8 & 0xff) / 255F;
		final float blue1 = (color1 & 0xff) / 255F;
		final float alpha2 = (color2 >> 24 & 0xff) / 255F;
		final float red2 = (color2 >> 16 & 0xff) / 255F;
		final float green2 = (color2 >> 8 & 0xff) / 255F;
		final float blue2 = (color2 & 0xff) / 255F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
		tessellator.addVertex(w, y, 0);
		tessellator.addVertex(x, y, 0);
		tessellator.setColorRGBA_F(red2, green2, blue2, alpha2);
		tessellator.addVertex(x, h, 0);
		tessellator.addVertex(w, h, 0);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static String modNameFromStack(final ItemStack stack) {
		try {
			return Loader.instance().getIndexedModList().get(GameRegistry.findUniqueIdentifierFor(stack.getItem()).modId).getName();
		}
		catch (final Exception e) {
			return "Minecraft";
		}
	}

	private Class<?> nei;
	private Method info;
	private boolean useNei = false;
	private int mainColor, outlineColor;
	private final int secondaryColor;
	private EntityItem entityItem;
	private EntityPlayer entityPlayer;
	private FontRenderer fr;
	private float deltaTime;

	public TooltipSystem() {
		try {
			nei = Class.forName("codechicken.nei.guihook.GuiContainerManager");
			if (nei != null) {
				info = nei.getDeclaredMethod("itemDisplayNameMultiline", ItemStack.class, GuiContainer.class, boolean.class);
				useNei = true;
			}
		}
		catch (final Exception e) {}
		try {
			mainColor = Integer.decode(WorldTooltip.color1);
		}
		catch (final NumberFormatException e) {
			mainColor = 0x100010;
		}
		try {
			outlineColor = Integer.decode(WorldTooltip.color2);
		}
		catch (final NumberFormatException e) {
			outlineColor = 0x5000FF;
		}
		mainColor = mainColor & 0xFFFFFF | 0xD0000000;
		outlineColor = outlineColor & 0xFFFFFF | 0x90000000;
		secondaryColor = (outlineColor & 0xFEFEFE) >> 1 | outlineColor & 0xFF000000;
	}

	@SuppressWarnings({
			"unused", "unchecked"
	})
	private void addInfo(final List<String> list) {
		if (entityItem.getEntityItem().getItem() instanceof ItemArmor) {
			final ItemArmor item = (ItemArmor) entityItem.getEntityItem().getItem();
			list.add("Armor Strength: " + item.damageReduceAmount);
		} else if (entityItem.getEntityItem().getItem() instanceof ItemTool) {
			final ItemTool item = (ItemTool) entityItem.getEntityItem().getItem();
			list.add("Material: " + item.getToolMaterialName());
		} else if (entityItem.getEntityItem().getItem() instanceof ItemFood) {
			final ItemFood item = (ItemFood) entityItem.getEntityItem().getItem();
			list.add("Hunger: " + item.func_150905_g(entityItem.getEntityItem()));
			list.add("Saturation: " + item.func_150906_h(entityItem.getEntityItem()));
		} else if (entityItem.getEntityItem().getItem() instanceof ItemPotion) {
			final ItemPotion item = (ItemPotion) entityItem.getEntityItem().getItem();
			final List<PotionEffect> effects = item.getEffects(entityItem.getEntityItem());
			if (effects != null)
				for (final PotionEffect effect : effects)
					list.add("Potion Effect: " + I18n.format(effect.getEffectName()));
		}
	}

	@SuppressWarnings("unchecked")
	private void drawItemTip() {
		List<String> list = null;
		if (useNei)
			try {
				list = (List<String>) info.invoke(null, entityItem.getEntityItem(), null, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			}
			catch (final Exception e) {}
		if (list == null)
			list = entityItem.getEntityItem().getTooltip(entityPlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		if (list == null)
			return;
		// addInfo(list);
		list.add(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC.toString() + TooltipSystem.modNameFromStack(entityItem.getEntityItem()) + EnumChatFormatting.RESET.toString());
		if (list.size() > 0) {
			if (entityItem.getEntityItem().stackSize > 1)
				list.set(0, entityItem.getEntityItem().stackSize + " x " + list.get(0));
			int maxwidth = 0;
			for (int line = 0; line < list.size(); line++) {
				final int swidth = fr.getStringWidth(list.get(line));
				if (swidth > maxwidth)
					maxwidth = swidth;
			}
			final int w = maxwidth;
			int h = 8;
			if (list.size() > 1)
				h += 2 + (list.size() - 1) * 10;
			final int drawx = -w / 2;
			int drawy = -h;
			TooltipSystem.drawGradientRect(drawx - 3, drawy - 4, w + 6, 1, mainColor, mainColor);
			TooltipSystem.drawGradientRect(drawx - 3, drawy + h + 3, w + 6, 1, mainColor, mainColor);
			TooltipSystem.drawGradientRect(drawx - 3, drawy - 3, w + 6, h + 6, mainColor, mainColor);
			TooltipSystem.drawGradientRect(drawx - 4, drawy - 3, 1, h + 6, mainColor, mainColor);
			TooltipSystem.drawGradientRect(drawx + w + 3, drawy - 3, 1, h + 6, mainColor, mainColor);
			TooltipSystem.drawGradientRect(drawx - 3, drawy - 2, 1, h + 4, outlineColor, secondaryColor);
			TooltipSystem.drawGradientRect(drawx + w + 2, drawy - 2, 1, h + 4, outlineColor, secondaryColor);
			TooltipSystem.drawGradientRect(drawx - 3, drawy - 3, w + 6, 1, outlineColor, outlineColor);
			TooltipSystem.drawGradientRect(drawx - 3, drawy + h + 2, w + 6, 1, secondaryColor, secondaryColor);
			GL11.glTranslated(0, 0, 1);
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);
				if (i == 0)
					s = entityItem.getEntityItem().getRarity().rarityColor.toString() + s;
				fr.drawStringWithShadow(s, drawx, drawy, -1);
				if (i == 0)
					drawy += 2;
				drawy += 10;
			}
			GL11.glTranslated(0, 0, -1);
		}
	}

	@SuppressWarnings("unchecked")
	public EntityItem getMouseOver() {
		final double findDistance = 16;
		final Vec3 positionVector = entityPlayer.getPosition(deltaTime);
		final Vec3 lookVector = entityPlayer.getLook(deltaTime);
		final Vec3 lookingAtVector = positionVector.addVector(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance);
		final float viewDistanceExpansion = 5;
		final List<EntityItem> entityList = entityPlayer.worldObj.getEntitiesWithinAABB(EntityItem.class, entityPlayer.boundingBox.addCoord(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance).expand(viewDistanceExpansion, viewDistanceExpansion, viewDistanceExpansion));
		double difference = 0;
		EntityItem target = null;
		for (int i = 0; i < entityList.size(); i++) {
			final EntityItem entity = entityList.get(i);
			final float boundSize = 0.2f;
			final AxisAlignedBB entityCollisionBox = entity.boundingBox.expand(boundSize, boundSize, boundSize);
			final MovingObjectPosition objectInVector = entityCollisionBox.calculateIntercept(positionVector, lookingAtVector);
			if (entityCollisionBox.isVecInside(positionVector)) {
				if (0.0D <= difference) {
					target = entity;
					difference = 0;
				}
			} else if (objectInVector != null) {
				final double distance = positionVector.distanceTo(objectInVector.hitVec);
				if (distance < difference || difference == 0.0D) {
					target = entity;
					difference = distance;
				}
			}
		}
		return target;
	}

	@SubscribeEvent
	public void hook(final RenderWorldLastEvent event) {
		entityPlayer = Minecraft.getMinecraft().thePlayer;
		deltaTime = event.partialTicks;
		entityItem = getMouseOver();
		if (entityItem == null)
			return;
		fr = entityItem.getEntityItem().getItem().getFontRenderer(entityItem.getEntityItem());
		if (fr == null)
			fr = Minecraft.getMinecraft().fontRenderer;
		renderEntityItem();
	}

	public void renderEntityItem() {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		final double interpX = RenderManager.renderPosX - (entityItem.posX - (entityItem.prevPosX - entityItem.posX) * deltaTime);
		final double interpY = RenderManager.renderPosY - (entityItem.posY - (entityItem.prevPosY - entityItem.posY) * deltaTime);
		final double interpZ = RenderManager.renderPosZ - (entityItem.posZ - (entityItem.prevPosZ - entityItem.posZ) * deltaTime);
		final double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
		GL11.glTranslated(-interpX, -(interpY - entityItem.height - 0.5), -interpZ);
		GL11.glRotatef(-RenderManager.instance.playerViewY + 180, 0, 1, 0);
		GL11.glRotatef(-RenderManager.instance.playerViewX, 1, 0, 0);
		double scale = interpDistance / 256;
		if (scale <= 0.01)
			scale = 0.01;
		GL11.glScaled(scale, -scale, scale);
		drawItemTip();
		GL11.glScaled(1F / scale, 1F / -scale, 1F / scale);
		GL11.glRotatef(RenderManager.instance.playerViewX, 1, 0, 0);
		GL11.glRotatef(RenderManager.instance.playerViewY - 180, 0, 1, 0);
		GL11.glTranslated(interpX, interpY - entityItem.height - 0.5, interpZ);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
