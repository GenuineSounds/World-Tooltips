package ninja.genuine.tooltips.client.render;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import ninja.genuine.tooltips.client.Tooltip;
import ninja.genuine.tooltips.client.config.TooltipConfig;
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
		tooltips.removeIf(Objects::isNull);
		tooltips.removeIf(Tooltip::isDead);
		tooltips.forEach(Tooltip::tick);
		Optional<EntityItem> mouseOver = ModUtils.getMouseOver();
		if (mouseOver.isPresent()) {
			boolean createTooltip = true;
			EntityItem entity = mouseOver.get();
			for (Tooltip tooltip : tooltips)
				if (tooltip.getEntity() == entity)
					createTooltip = !tooltip.reset();
			if (createTooltip)
				tooltips.addFirst(new Tooltip(Minecraft.getMinecraft().player, entity));
		}
		for (int i = TooltipConfig.getMaxTooltips(); i < tooltips.size(); i++)
			tooltips.get(i).forceFade();
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!TooltipConfig.isEnabled() || Minecraft.getMinecraft().world == null)
			return;
		for (Tooltip tooltip : tooltips)
			RenderHelper.renderTooltip(tooltip, event.getPartialTicks());
	}
}
