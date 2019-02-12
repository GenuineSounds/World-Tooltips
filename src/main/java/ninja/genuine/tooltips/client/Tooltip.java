package ninja.genuine.tooltips.client;

import static com.mojang.realmsclient.gui.ChatFormatting.BLUE;
import static com.mojang.realmsclient.gui.ChatFormatting.ITALIC;
import static com.mojang.realmsclient.gui.ChatFormatting.RESET;

import java.util.ArrayList;
import java.util.List;

import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class Tooltip implements Comparable<Tooltip> {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final Config cfg = Config.getInstance();
	private ScaledResolution sr = new ScaledResolution(mc);
	private EntityItem entity;
	private EntityPlayer player;
	private TextFormatting textFormatting;
	private List<String> text = new ArrayList<>();
	private int width, height;
	private int tickCount;
	private int fadeCount;
	public double distanceToPlayer;
	public double scale;
	public int alpha;
	public int colorBackground;
	public int colorOutline;
	public int colorOutlineShade;
	private boolean forceFade;
	private boolean countDown = true;

	public Tooltip(EntityPlayer player, EntityItem entity) {
		this.player = player;
		this.entity = entity;
		textFormatting = entity.getItem().getRarity().rarityColor;
		generateTooltip(player);
		calculateSize();
		fadeCount = cfg.getFadeTime();
		tickCount = cfg.getShowTime() + fadeCount;
	}

	private void generateTooltip(EntityPlayer player) {
		boolean advanced = mc.gameSettings.advancedItemTooltips;
		text = entity.getItem().getTooltip(player, advanced ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
		if (!modsAreLoaded() && !cfg.isHidingModName())
			text.add(BLUE.toString() + ITALIC.toString() + ModUtils.getModName(entity) + RESET.toString());
		if (entity.getItem().getCount() > 1)
			text.set(0, entity.getItem().getCount() + " x " + text.get(0));
	}

	private void calculateSize() {
		int max = 0;
		for (int line = 0; line < text.size(); line++) {
			int tmp = mc.fontRenderer.getStringWidth(text.get(line));
			if (tmp > max)
				max = tmp;
		}
		width = max;
		height = 8;
		if (size() > 1)
			height += 2 + (size() - 1) * 10;
	}

	public void tick() {
		sr = new ScaledResolution(mc);
		if (entity == null || entity.isDead)
			tickCount = 0;
		if (countDown)
			tickCount--;
		else
			tickCount += cfg.getFadeTime() / 4;
		if (tickCount < 0)
			tickCount = 0;
		if (tickCount > cfg.getShowTime() + fadeCount)
			tickCount = cfg.getShowTime() + fadeCount;
		generateTooltip(player);
		calculateSize();
		distanceToPlayer = entity.getDistance(player);
		scale = distanceToPlayer / ((6 - sr.getScaleFactor()) * 160);
		if (scale < 0.01)
			scale = 0.01;
		scale *= cfg.getScale().getDouble();
		if (getFade() > cfg.getOpacity().getDouble())
			alpha = ((int) (cfg.getOpacity().getDouble() * 0xFF) & 0xFF) << 24;
		else
			alpha = ((int) (getFade() * 0xFF) & 0xFF) << 24;
		colorBackground = cfg.getBackgroundColor() | alpha;
		colorOutline = ((cfg.isOverridingOutline() ? cfg.getOutlineColor() : ModUtils.getRarityColor(this)) | alpha) & 0xFFE0E0E0;
		colorOutlineShade = ((colorOutline & 0xFEFEFE) >> 1) | alpha;
		countDown = true;
	}

	public double getFade() {
		if (tickCount > fadeCount)
			return 1D;
		return Math.abs(Math.pow(-1, 2) * ((double) tickCount / (double) fadeCount));
	}

	public void forceFade() {
		if (forceFade)
			return;
		tickCount = 10;
		fadeCount = 10;
		forceFade = true;
	}

	private boolean modsAreLoaded() {
		return Loader.isModLoaded("waila") | Loader.isModLoaded("nei") | Loader.isModLoaded("hwyla");
	}

	@Override
	public int compareTo(Tooltip o) {
		return (int) (o.distanceToPlayer * 10000 - distanceToPlayer * 10000);
	}

	public boolean reset() {
		if (forceFade)
			return false;
		countDown = false;
		return true;
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
}
