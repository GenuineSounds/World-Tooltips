package ninja.genuine.tooltips;

import java.util.regex.Pattern;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ninja.genuine.tooltips.client.RenderEvent;
import ninja.genuine.tooltips.client.render.TooltipRenderer;

@Mod(modid = WorldTooltips.MODID, name = WorldTooltips.NAME, version = WorldTooltips.VERSION, canBeDeactivated = true, clientSideOnly = true, updateJSON = WorldTooltips.URL + "update.json", useMetadata = true)
public class WorldTooltips {

	@Instance(WorldTooltips.MODID)
	public static WorldTooltips instance;

	public static final String MODID = "worldtooltips";

	public static final String NAME = "World Tooltips";

	public static final String URL = "http://genuine.ninja/world-tooltips/";

	public static final String VERSION = "1.2.0";

	public static final String DESC = "This is a color in hex form (ie: 0xAB12cd or #AB12cd), one can always lookup your favorite colors online.";

	public static int colorPrimary, colorOutline;

	public RenderEvent re;

	private final Pattern pattern = Pattern.compile("^(0[xX]|#)[0-9a-fA-F]{1,8}$");

	public WorldTooltips() {
		instance = this;
	}

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		final Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		try {
			colorPrimary = Integer.decode(config.getString("primary", "Colors", "0x100010", DESC, pattern));
		}
		catch (NumberFormatException e) {
			colorPrimary = 0x100010;
		}
		try {
			colorOutline = Integer.decode(config.getString("outline", "Colors", "0x5000FF", DESC, pattern));
		}
		catch (NumberFormatException e) {
			colorOutline = 0x5000FF;
		}
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		re = new RenderEvent();
		MinecraftForge.EVENT_BUS.register(re);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event) {
		re.post();
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		MinecraftForge.EVENT_BUS.unregister(re);
		re.disable();
	}
}
