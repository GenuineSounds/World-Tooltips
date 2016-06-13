package ninja.genuine.tooltip;

import java.util.regex.Pattern;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import ninja.genuine.tooltip.system.TooltipSystem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WorldTooltip.MODID, name = WorldTooltip.NAME, version = WorldTooltip.VERSION)
public class WorldTooltip {

	@Instance(WorldTooltip.MODID)
	public static WorldTooltip instance;
	public static final String MODID = "WorldTooltip";
	public static final String NAME = "WorldTooltip";
	public static final String VERSION = "1.1.0";
	public static final String DESC = "This is a color in hex form (ie: 0xAB12cd or #AB12cd), one can always lookup your favorite colors online.";
	public static String color1, color2;
	private final Pattern pattern = Pattern.compile("^(0[xX]|#)[0-9a-fA-F]{1,8}$");

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		final Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		WorldTooltip.color1 = config.getString("background", "Colors", "0x100010", WorldTooltip.DESC, pattern);
		WorldTooltip.color2 = config.getString("outline", "Colors", "0x5000FF", WorldTooltip.DESC, pattern);
		config.save();
		Blocks.brick_block.setHarvestLevel("pickaxe", 0);
		Blocks.brick_stairs.setHarvestLevel("pickaxe", 0);
		Blocks.double_stone_slab.setHarvestLevel("pickaxe", 0);
		Blocks.nether_brick.setHarvestLevel("pickaxe", 0);
		Blocks.nether_brick_stairs.setHarvestLevel("pickaxe", 0);
		Blocks.sandstone.setHarvestLevel("pickaxe", 0);
		Blocks.sandstone_stairs.setHarvestLevel("pickaxe", 0);
		Blocks.stonebrick.setHarvestLevel("pickaxe", 0);
		Blocks.stone_brick_stairs.setHarvestLevel("pickaxe", 0);
		Blocks.stone_slab.setHarvestLevel("pickaxe", 0);
		Blocks.stone_stairs.setHarvestLevel("pickaxe", 0);
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TooltipSystem());
	}
}
