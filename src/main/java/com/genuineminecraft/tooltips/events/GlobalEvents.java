package com.genuineminecraft.tooltips.events;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.genuineminecraft.tooltips.Tooltips;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GlobalEvents {

	private Class nei;
	private Method info;
	private boolean useNei = false;
	private int mainColor, outlineColor, secondaryColor;
	private EntityItem item;
	private EntityPlayer entity;
	private FontRenderer fr;
	private float deltaTime;

	public GlobalEvents() {
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
		mainColor = (mainColor & 0xFFFFFF) | 0xC0000000;
		outlineColor = (outlineColor & 0xFFFFFF) | 0x50000000;
		secondaryColor = (outlineColor & 0xFEFEFE) >> 1 | outlineColor & 0xFF000000;
	}

	@SubscribeEvent
	public void hook(RenderWorldLastEvent event) {
		entity = Minecraft.getMinecraft().thePlayer;
		deltaTime = event.partialTicks;
		item = getMouseOver();
		if (item == null)
			return;
		fr = item.getEntityItem().getItem().getFontRenderer(item.getEntityItem());
		if (fr == null)
			fr = Minecraft.getMinecraft().fontRenderer;
		renderEntityItem();
	}

	public void renderEntityItem() {
		glPushMatrix();
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		double x = RenderManager.instance.viewerPosX - (item.posX - ((item.prevPosX - item.posX) * deltaTime));
		double y = RenderManager.instance.viewerPosY - (item.posY - ((item.prevPosY - item.posY) * deltaTime)) - item.height - 0.5;
		double z = RenderManager.instance.viewerPosZ - (item.posZ - ((item.prevPosZ - item.posZ) * deltaTime));
		glTranslated(-x, -y, -z);
		glRotatef(-RenderManager.instance.playerViewY + 180, 0.0F, 1.0F, 0.0F);
		glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		float scale = 0.02F;
		glScalef(scale, -scale, scale);
		drawItemTip();
		glScalef(1F / scale, 1F / -scale, 1F / scale);
		glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		glRotatef(RenderManager.instance.playerViewY - 180, 0.0F, 1.0F, 0.0F);
		glTranslated(x, y, z);
		glColor4f(1, 1, 1, 1);
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glPopMatrix();
	}

	private void drawItemTip() {
		List<String> list = null;
		if (useNei) {
			try {
				list = (List<String>) info.invoke(null, item.getEntityItem(), null, false);
			}
			catch (Exception e) {}
		}
		if (list == null)
			list = item.getEntityItem().getTooltip(entity, false);
		if (list == null)
			return;
		if (item.getEntityItem().getItem() instanceof ItemArmor) {
			ItemArmor armor = (ItemArmor) item.getEntityItem().getItem();
			list.add("Armor Strength: " + armor.damageReduceAmount);
		}
		if (list.size() > 0) {
			if (item.getEntityItem().stackSize > 1)
				list.set(0, item.getEntityItem().stackSize + " x " + list.get(0));
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
			glTranslated(0, 0, 1);
			for (int i = 0; i < list.size(); i++) {
				String s = (String) list.get(i);
				if (i == 0)
					s = item.getEntityItem().getRarity().rarityColor.toString() + s;
				fr.drawStringWithShadow(s, drawx, drawy, -1);
				if (i == 0)
					drawy += 2;
				drawy += 10;
			}
			glTranslated(0, 0, -1);
		}
	}

	protected void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		w += x;
		h += y;
		float alpha1 = (float) (color1 >> 24 & 0xff) / 255F;
		float red1 = (float) (color1 >> 16 & 0xff) / 255F;
		float green1 = (float) (color1 >> 8 & 0xff) / 255F;
		float blue1 = (float) (color1 & 0xff) / 255F;
		float alpha2 = (float) (color2 >> 24 & 0xff) / 255F;
		float red2 = (float) (color2 >> 16 & 0xff) / 255F;
		float green2 = (float) (color2 >> 8 & 0xff) / 255F;
		float blue2 = (float) (color2 & 0xff) / 255F;
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glShadeModel(GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
		tessellator.addVertex(w, y, 0);
		tessellator.addVertex(x, y, 0);
		tessellator.setColorRGBA_F(red2, green2, blue2, alpha2);
		tessellator.addVertex(x, h, 0);
		tessellator.addVertex(w, h, 0);
		tessellator.draw();
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
	}

	public EntityItem getMouseOver() {
		double findDistance = 16.0D;
		MovingObjectPosition objectMouseOver = entity.rayTrace(findDistance, deltaTime);
		double findDistanceCap = findDistance;
		Vec3 positionVector = entity.getPosition(deltaTime);
		if (objectMouseOver != null)
			findDistanceCap = objectMouseOver.hitVec.distanceTo(positionVector);
		Vec3 lookVector = entity.getLook(deltaTime);
		Vec3 lookingAtVector = positionVector.addVector(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance);
		float viewDistanceExpansion = 5.0F;
		List<EntityItem> entityList = (List<EntityItem>) entity.worldObj.getEntitiesWithinAABB(EntityItem.class, entity.boundingBox.addCoord(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance).expand(viewDistanceExpansion, viewDistanceExpansion, viewDistanceExpansion));
		double difference = 0.0D;
		EntityItem target = null;
		for (int i = 0; i < entityList.size(); i++) {
			EntityItem entity = entityList.get(i);
			float boundSize = 0.2f;
			AxisAlignedBB entityCollisionBox = entity.boundingBox.expand(boundSize, boundSize, boundSize);
			MovingObjectPosition objectInVector = entityCollisionBox.calculateIntercept(positionVector, lookingAtVector);
			if (entityCollisionBox.isVecInside(positionVector)) {
				if (0.0D <= difference) {
					target = entity;
					difference = 0.0D;
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
}
