package com.genuineminecraft.tooltips.system;

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
			this.nei = Class.forName("codechicken.nei.guihook.GuiContainerManager");
			if (this.nei != null) {
				this.info = this.nei.getDeclaredMethod("itemDisplayNameMultiline", ItemStack.class, GuiContainer.class, boolean.class);
				this.useNei = true;
			}
		}
		catch (Exception e) {}
		try {
			this.mainColor = Integer.decode(Tooltips.color1);
		}
		catch (NumberFormatException e) {
			this.mainColor = 0x100010;
		}
		try {
			this.outlineColor = Integer.decode(Tooltips.color2);
		}
		catch (NumberFormatException e) {
			this.outlineColor = 0x5000FF;
		}
		this.mainColor = (this.mainColor & 0xFFFFFF) | 0xD0000000;
		this.outlineColor = (this.outlineColor & 0xFFFFFF) | 0x90000000;
		this.secondaryColor = (this.outlineColor & 0xFEFEFE) >> 1 | this.outlineColor & 0xFF000000;
	}

	private void addInfo(List<String> list) {
		if (this.entityItem.getEntityItem().getItem() instanceof ItemArmor) {
			ItemArmor item = (ItemArmor) this.entityItem.getEntityItem().getItem();
			list.add("Armor Strength: " + item.damageReduceAmount);
		} else if (this.entityItem.getEntityItem().getItem() instanceof ItemTool) {
			ItemTool item = (ItemTool) this.entityItem.getEntityItem().getItem();
			list.add("Material: " + item.getToolMaterialName());
		} else if (this.entityItem.getEntityItem().getItem() instanceof ItemFood) {
			ItemFood item = (ItemFood) this.entityItem.getEntityItem().getItem();
			list.add("Hunger: " + item.func_150905_g(this.entityItem.getEntityItem()));
			list.add("Saturation: " + item.func_150906_h(this.entityItem.getEntityItem()));
		} else if (this.entityItem.getEntityItem().getItem() instanceof ItemPotion) {
			ItemPotion item = (ItemPotion) this.entityItem.getEntityItem().getItem();
			List<PotionEffect> effects = item.getEffects(this.entityItem.getEntityItem());
			if (effects != null)
				for (PotionEffect effect : effects)
					list.add("Potion Effect: " + I18n.format(effect.getEffectName()));
		}
	}

	private void addModInfo(List<String> list) {
		String modName = nameFromStack(this.entityItem.getEntityItem());
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

	private void drawItemTip() {
		List<String> list = null;
		if (this.useNei) {
			try {
				list = (List<String>) this.info.invoke(null, this.entityItem.getEntityItem(), null, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
			}
			catch (Exception e) {}
		}
		if (list == null)
			list = this.entityItem.getEntityItem().getTooltip(this.entityPlayer, false);
		if (list == null)
			return;
		// addInfo(list);
		this.addModInfo(list);
		if (list.size() > 0) {
			if (this.entityItem.getEntityItem().stackSize > 1)
				list.set(0, this.entityItem.getEntityItem().stackSize + " x " + list.get(0));
			int maxwidth = 0;
			for (int line = 0; line < list.size(); line++) {
				int swidth = this.fr.getStringWidth(list.get(line));
				if (swidth > maxwidth)
					maxwidth = swidth;
			}
			int w = maxwidth;
			int h = 8;
			if (list.size() > 1)
				h += 2 + (list.size() - 1) * 10;
			int drawx = -w / 2;
			int drawy = -h;
			this.drawGradientRect(drawx - 3, drawy - 4, w + 6, 1, this.mainColor, this.mainColor);
			this.drawGradientRect(drawx - 3, drawy + h + 3, w + 6, 1, this.mainColor, this.mainColor);
			this.drawGradientRect(drawx - 3, drawy - 3, w + 6, h + 6, this.mainColor, this.mainColor);
			this.drawGradientRect(drawx - 4, drawy - 3, 1, h + 6, this.mainColor, this.mainColor);
			this.drawGradientRect(drawx + w + 3, drawy - 3, 1, h + 6, this.mainColor, this.mainColor);
			this.drawGradientRect(drawx - 3, drawy - 2, 1, h + 4, this.outlineColor, this.secondaryColor);
			this.drawGradientRect(drawx + w + 2, drawy - 2, 1, h + 4, this.outlineColor, this.secondaryColor);
			this.drawGradientRect(drawx - 3, drawy - 3, w + 6, 1, this.outlineColor, this.outlineColor);
			this.drawGradientRect(drawx - 3, drawy + h + 2, w + 6, 1, this.secondaryColor, this.secondaryColor);
			glTranslated(0, 0, 1);
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);
				if (i == 0)
					s = this.entityItem.getEntityItem().getRarity().rarityColor.toString() + s;
				this.fr.drawStringWithShadow(s, drawx, drawy, -1);
				if (i == 0)
					drawy += 2;
				drawy += 10;
			}
			glTranslated(0, 0, -1);
		}
	}

	public EntityItem getMouseOver() {
		double findDistance = 16.0D;
		MovingObjectPosition objectMouseOver = this.entityPlayer.rayTrace(findDistance, this.deltaTime);
		double findDistanceCap = findDistance;
		Vec3 positionVector = this.entityPlayer.getPosition(this.deltaTime);
		if (objectMouseOver != null)
			findDistanceCap = objectMouseOver.hitVec.distanceTo(positionVector);
		Vec3 lookVector = this.entityPlayer.getLook(this.deltaTime);
		Vec3 lookingAtVector = positionVector.addVector(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance);
		float viewDistanceExpansion = 5.0F;
		List<EntityItem> entityList = this.entityPlayer.worldObj.getEntitiesWithinAABB(EntityItem.class, this.entityPlayer.boundingBox.addCoord(lookVector.xCoord * findDistance, lookVector.yCoord * findDistance, lookVector.zCoord * findDistance).expand(viewDistanceExpansion, viewDistanceExpansion, viewDistanceExpansion));
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

	@SubscribeEvent
	public void hook(RenderWorldLastEvent event) {
		this.entityPlayer = Minecraft.getMinecraft().thePlayer;
		this.deltaTime = event.partialTicks;
		this.entityItem = this.getMouseOver();
		if (this.entityItem == null)
			return;
		this.fr = this.entityItem.getEntityItem().getItem().getFontRenderer(this.entityItem.getEntityItem());
		if (this.fr == null)
			this.fr = Minecraft.getMinecraft().fontRenderer;
		this.renderEntityItem();
	}

	public void renderEntityItem() {
		glPushMatrix();
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		double x = RenderManager.instance.viewerPosX - (this.entityItem.posX - ((this.entityItem.prevPosX - this.entityItem.posX) * this.deltaTime));
		double y = RenderManager.instance.viewerPosY - (this.entityItem.posY - ((this.entityItem.prevPosY - this.entityItem.posY) * this.deltaTime)) - this.entityItem.height - 0.5;
		double z = RenderManager.instance.viewerPosZ - (this.entityItem.posZ - ((this.entityItem.prevPosZ - this.entityItem.posZ) * this.deltaTime));
		glTranslated(-x, -y, -z);
		glRotatef(-RenderManager.instance.playerViewY + 180, 0.0F, 1.0F, 0.0F);
		glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		float scale = 0.02F;
		glScalef(scale, -scale, scale);
		this.drawItemTip();
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
}
