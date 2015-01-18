package com.genuineminecraft.tooltips.system;

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

import com.genuineminecraft.tooltips.Tooltips;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class TooltipSystem {

	public static String nameFromStack(ItemStack stack) {
		try {
			UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(stack.getItem());
			ModContainer mod = GameData.findModOwner(GameData.getItemRegistry().getNameForObject(stack.getItem()));
			String modname = mod == null ? "Minecraft" : mod.getName();
			return modname;
		}
		catch (Exception e) {
			return "";
		}
	}

	private Class nei;
	private Method info;
	private boolean useNei = false;
	private int mainColor, outlineColor, secondaryColor;
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
		catch (Exception e) {}
		try {
			mainColor = Integer.decode(Tooltips.color1);
		}
		catch (NumberFormatException e) {
			mainColor = 0x100010;
		}
		try {
			outlineColor = Integer.decode(Tooltips.color2);
		}
		catch (NumberFormatException e) {
			outlineColor = 0x5000FF;
		}
		mainColor = mainColor & 0xFFFFFF | 0xD0000000;
		outlineColor = outlineColor & 0xFFFFFF | 0x90000000;
		secondaryColor = (outlineColor & 0xFEFEFE) >> 1 | outlineColor & 0xFF000000;
	}

	private void addInfo(List<String> list) {
		if (entityItem.getEntityItem().getItem() instanceof ItemArmor) {
			ItemArmor item = (ItemArmor) entityItem.getEntityItem().getItem();
			list.add("Armor Strength: " + item.damageReduceAmount);
		} else if (entityItem.getEntityItem().getItem() instanceof ItemTool) {
			ItemTool item = (ItemTool) entityItem.getEntityItem().getItem();
			list.add("Material: " + item.getToolMaterialName());
		} else if (entityItem.getEntityItem().getItem() instanceof ItemFood) {
			ItemFood item = (ItemFood) entityItem.getEntityItem().getItem();
			list.add("Hunger: " + item.func_150905_g(entityItem.getEntityItem()));
			list.add("Saturation: " + item.func_150906_h(entityItem.getEntityItem()));
		} else if (entityItem.getEntityItem().getItem() instanceof ItemPotion) {
			ItemPotion item = (ItemPotion) entityItem.getEntityItem().getItem();
			List<PotionEffect> effects = item.getEffects(entityItem.getEntityItem());
			if (effects != null)
				for (PotionEffect effect : effects)
					list.add("Potion Effect: " + I18n.format(effect.getEffectName()));
		}
	}

	private void addModInfo(List<String> list) {
		String modName = TooltipSystem.nameFromStack(entityItem.getEntityItem());
		if (!modName.isEmpty())
			list.add(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC.toString() + modName + EnumChatFormatting.RESET.toString());
	}

	protected void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		w += x;
		h += y;
		float alpha1 = (color1 >> 24 & 0xff) / 255F;
		float red1 = (color1 >> 16 & 0xff) / 255F;
		float green1 = (color1 >> 8 & 0xff) / 255F;
		float blue1 = (color1 & 0xff) / 255F;
		float alpha2 = (color2 >> 24 & 0xff) / 255F;
		float red2 = (color2 >> 16 & 0xff) / 255F;
		float green2 = (color2 >> 8 & 0xff) / 255F;
		float blue2 = (color2 & 0xff) / 255F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
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

	private void drawItemTip() {
		List<String> list = null;
		if (useNei)
			try {
				list = (List<String>) info.invoke(null, entityItem.getEntityItem(), null, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			}
			catch (Exception e) {}
		if (list == null)
			list = entityItem.getEntityItem().getTooltip(entityPlayer, false);
		if (list == null)
			return;
		// addInfo(list);
		addModInfo(list);
		if (list.size() > 0) {
			if (entityItem.getEntityItem().stackSize > 1)
				list.set(0, entityItem.getEntityItem().stackSize + " x " + list.get(0));
			int maxwidth = 0;
			for (int line = 0; line < list.size(); line++) {
				int swidth = fr.getStringWidth(list.get(line));
				if (swidth > maxwidth)
					maxwidth = swidth;
			}
			int w = maxwidth;
			int h = 8;
			if (list.size() > 1)
				h += 2 + (list.size() - 1) * 10;
			int drawx = -w / 2;
			int drawy = -h;
			drawGradientRect(drawx - 3, drawy - 4, w + 6, 1, mainColor, mainColor);
			drawGradientRect(drawx - 3, drawy + h + 3, w + 6, 1, mainColor, mainColor);
			drawGradientRect(drawx - 3, drawy - 3, w + 6, h + 6, mainColor, mainColor);
			drawGradientRect(drawx - 4, drawy - 3, 1, h + 6, mainColor, mainColor);
			drawGradientRect(drawx + w + 3, drawy - 3, 1, h + 6, mainColor, mainColor);
			drawGradientRect(drawx - 3, drawy - 2, 1, h + 4, outlineColor, secondaryColor);
			drawGradientRect(drawx + w + 2, drawy - 2, 1, h + 4, outlineColor, secondaryColor);
			drawGradientRect(drawx - 3, drawy - 3, w + 6, 1, outlineColor, outlineColor);
			drawGradientRect(drawx - 3, drawy + h + 2, w + 6, 1, secondaryColor, secondaryColor);
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

	public EntityItem getMouseOver() {
		double findDistance = 32;
		MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(findDistance, deltaTime);
		double findDistanceCap = findDistance;
		Vec3 positionVector = entityPlayer.getPosition(deltaTime);
		if (objectMouseOver != null)
			findDistanceCap = objectMouseOver.hitVec.distanceTo(positionVector);
		Vec3 lookVector = entityPlayer.getLook(deltaTime);
		Vec3 lookingAtVector = positionVector.addVector(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance);
		float viewDistanceExpansion = 5;
		List<EntityItem> entityList = entityPlayer.worldObj.getEntitiesWithinAABB(EntityItem.class, entityPlayer.boundingBox.addCoord(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance).expand(viewDistanceExpansion, viewDistanceExpansion, viewDistanceExpansion));
		double difference = 0;
		EntityItem target = null;
		for (int i = 0; i < entityList.size(); i++) {
			EntityItem entity = entityList.get(i);
			float boundSize = 0.2f;
			AxisAlignedBB entityCollisionBox = entity.boundingBox.expand(boundSize, boundSize, boundSize);
			MovingObjectPosition objectInVector = entityCollisionBox.calculateIntercept(positionVector, lookingAtVector);
			if (entityCollisionBox.isVecInside(positionVector)) {
				if (0.0D <= difference) {
					target = entity;
					difference = 0;
				}
			} else if (objectInVector != null) {
				double distance = positionVector.distanceTo(objectInVector.hitVec);
				if (distance < difference || difference == 0.0D) {
					target = entity;
					difference = distance;
				}
			}
		}
		return target;
	}

	@SubscribeEvent
	public void hook(RenderWorldLastEvent event) {
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
		double interpX = RenderManager.renderPosX - (entityItem.posX - (entityItem.prevPosX - entityItem.posX) * deltaTime);
		double interpY = RenderManager.renderPosY - (entityItem.posY - (entityItem.prevPosY - entityItem.posY) * deltaTime);
		double interpZ = RenderManager.renderPosZ - (entityItem.posZ - (entityItem.prevPosZ - entityItem.posZ) * deltaTime);
		double interpDistance = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
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
