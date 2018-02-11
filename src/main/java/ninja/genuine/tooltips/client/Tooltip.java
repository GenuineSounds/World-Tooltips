package ninja.genuine.tooltips.client;

import static com.mojang.realmsclient.gui.ChatFormatting.BLUE;
import static com.mojang.realmsclient.gui.ChatFormatting.ITALIC;
import static com.mojang.realmsclient.gui.ChatFormatting.RESET;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

public class Tooltip implements Comparable<Tooltip> {

	private static Minecraft mc = Minecraft.getMinecraft();
	private EntityItem entity;
	private EntityPlayer player;
	private TextFormatting textFormatting;
	private List<String> text = new ArrayList<>();
	private int width, height;
	private double distanceToPlayer;
	private int tickCount = 240;

	public Tooltip(EntityPlayer player, EntityItem entity) {
		this.player = player;
		this.entity = entity;
		textFormatting = entity.getItem().getRarity().rarityColor;
		generateTooltip(player);
		calculateSize();
	}

	public void tick() {
		if (entity == null || entity.isDead)
			tickCount = 0;
		if (entity.cannotPickup()) {
			resetTick();
			System.out.println("Poops");
		}
		tickCount--;
		distanceToPlayer = entity.getDistance(player);
	}

	public void resetTick() {
		tickCount = 240;
	}

	@Override
	public int compareTo(Tooltip o) {
		return (int) (o.distanceToPlayer * 1000 - distanceToPlayer * 1000);
	}

	public EntityItem getEntity() {
		return entity;
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

	private void generateTooltip(EntityPlayer player) {
		boolean advanced = mc.gameSettings.advancedItemTooltips;
		text = entity.getItem().getTooltip(player, advanced ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
		if (!modsAreLoaded() && !Config.getInstance().isHidingModName())
			text.add(BLUE.toString() + ITALIC.toString() + ModUtils.getModName(entity) + RESET.toString());
		if (entity.getItem().getCount() > 1)
			text.set(0, entity.getItem().getCount() + " x " + text.get(0));
	}

	private void calculateSize() {
		int maxWidth = 0;
		for (int line = 0; line < text.size(); line++) {
			int tmp = mc.fontRenderer.getStringWidth(text.get(line));
			if (tmp > maxWidth)
				maxWidth = tmp;
		}
		width = maxWidth;
		height = 8;
		if (size() > 1)
			height += 2 + (size() - 1) * 10;
	}
}
