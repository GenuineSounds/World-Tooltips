package ninja.genuine.tooltips.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.Config;
import ninja.genuine.utils.ModUtils;

public class RenderEvent {

	private Minecraft mc;
	private List<Tooltip> tooltips = new ArrayList<>();
	private EntityItem entity;

	public RenderEvent() {
		mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void tick(final WorldTickEvent event) {
		if (Minecraft.getMinecraft().world == null)
			return;
		synchronized (tooltips) {
			tooltips.removeIf(Objects::isNull);
			tooltips.removeIf(Tooltip::isDead);
			for (Tooltip tooltip : tooltips)
				tooltip.tick();
			Collections.sort(tooltips);
			try {
				entity = ModUtils.getMouseOver(mc, 0);
			} catch (ConcurrentModificationException e) {}
			if (entity == null)
				return;
			boolean createTooltip = true;
			for (Tooltip tooltip : tooltips) {
				if (tooltip.getEntity() == entity) {
					tooltip.reset();
					createTooltip = false;
				}
			}
			if (createTooltip)
				tooltips.add(new Tooltip(Minecraft.getMinecraft().player, entity));
		}
	}

	@SubscribeEvent
	public void render(final RenderWorldLastEvent event) {
		if (!Config.getInstance().isEnabled() || Minecraft.getMinecraft().world == null)
			return;
		synchronized (tooltips) {
			for (Tooltip tooltip : tooltips)
				RenderHelper.renderTooltip(tooltip, event.getPartialTicks());
		}
	}
}
