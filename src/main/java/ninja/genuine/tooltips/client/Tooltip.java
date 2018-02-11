package ninja.genuine.tooltips.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

public class Tooltip implements Comparable<Tooltip> {

	private EntityItem entityItem;
	private TextFormatting textFormatting;
	private List<String> text = new ArrayList<>();
	private int width, height;
	private double distanceToPlayer;
	private int tickCount = 240;

	public Tooltip(EntityPlayer player, EntityItem entity) {
		entityItem = entity;
		textFormatting = entity.getItem().getRarity().rarityColor;
		generateTooltip(player, entity.getItem());
		calculateSize();
	}

	public void tick() {
		if (entityItem == null || entityItem.isDead)
			tickCount = 0;
		if (entityItem.cannotPickup()) {
			resetTick();
			System.out.println("Poops");
		}
		Minecraft mc = Minecraft.getMinecraft();
		tickCount--;
		double interpX = mc.getRenderManager().viewerPosX - entityItem.posX;
		double interpY = mc.getRenderManager().viewerPosY - 0.65 - entityItem.posY;
		double interpZ = mc.getRenderManager().viewerPosZ - entityItem.posZ;
		distanceToPlayer = Math.sqrt(interpX * interpX + interpY * interpY + interpZ * interpZ);
	}

	public void resetTick() {
		tickCount = 240;
	}

	@Override
	public int compareTo(Tooltip o) {
		return (int) (o.distanceToPlayer * 1000 - distanceToPlayer * 1000);
	}

	public EntityItem getEntityItem() {
		return entityItem;
	}

	public int getTickCount() {
		return tickCount;
	}

	public boolean isDead() {
		return tickCount <= 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int size() {
		return text.size();
	}

	public List<String> getText() {
		return text;
	}

	public TextFormatting formattingColor() {
		return textFormatting;
	}

	private boolean modsAreLoaded() {
		return Loader.isModLoaded("waila") | Loader.isModLoaded("nei") | Loader.isModLoaded("hwyla");
	}

	private void generateTooltip(EntityPlayer player, ItemStack item) {
		text = item.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		if (!modsAreLoaded() && !Config.getInstance().isHidingModName())
			text.add(ChatFormatting.BLUE.toString() + ChatFormatting.ITALIC.toString() + ModUtils.getModName(item) + ChatFormatting.RESET.toString());
		if (item.getCount() > 1)
			text.set(0, item.getCount() + " x " + text.get(0));
	}

	private void calculateSize() {
		int maxWidth = 0;
		for (int line = 0; line < text.size(); line++) {
			int tmp = Minecraft.getMinecraft().fontRenderer.getStringWidth(text.get(line));
			if (tmp > maxWidth)
				maxWidth = tmp;
		}
		width = maxWidth;
		height = 8;
		if (size() > 1)
			height += 2 + (size() - 1) * 10;
	}
}
