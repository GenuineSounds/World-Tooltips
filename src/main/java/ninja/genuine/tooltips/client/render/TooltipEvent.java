package ninja.genuine.tooltips.client.render;

import java.util.LinkedList;
import java.util.Objects;

import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TooltipEvent {

	private LinkedList<Tooltip> tooltips = new LinkedList<>();

	public TooltipEvent() {}

	@SubscribeEvent
	public void tick(ClientTickEvent event) {
		if (event.phase != Phase.START)
			return;
		EntityItem entity = null;
		try {
			entity = ModUtils.getMouseOver(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player, 0).get();
		} catch (Exception e) {}
		synchronized (tooltips) {
			tooltips.removeIf(Objects::isNull);
			tooltips.removeIf(Tooltip::isDead);
			tooltips.forEach(Tooltip::tick);
			boolean createTooltip = true;
			for (Tooltip tooltip : tooltips)
				if (tooltip.getEntity() == entity) {
					createTooltip = false;
					tooltip.reset();
				}
			if (createTooltip && entity != null)
				tooltips.addFirst(new Tooltip(Minecraft.getMinecraft().player, entity));
			for (int i = Config.getInstance().getMaxTooltips(); i < tooltips.size(); i++)
				tooltips.get(i).forceFade();
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!Config.getInstance().isEnabled() || Minecraft.getMinecraft().world == null)
			return;
		synchronized (tooltips) {
			for (Tooltip tooltip : tooltips)
				RenderHelper.renderTooltip(tooltip, event.getPartialTicks());
		}
	}
}
