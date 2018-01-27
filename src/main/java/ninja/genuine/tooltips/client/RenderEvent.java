package ninja.genuine.tooltips.client;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.genuine.tooltips.Config;
import ninja.genuine.tooltips.Constants;

public class RenderEvent {

	private Minecraft mc;
	private Tooltip cache;
	private EntityItem entity;

	public RenderEvent() {}

	public void post() {
		mc = Minecraft.getMinecraft();
	}

	public void sync() {
		if (!Objects.isNull(cache))
			cache.sync();
	}

	@SubscribeEvent
	public void render(final RenderWorldLastEvent event) {
		entity = getMouseOver(mc, event.getPartialTicks());
		if (!Objects.isNull(entity)) {
			if (Objects.isNull(cache) || cache.getEntity() != entity)
				cache = new Tooltip(Minecraft.getMinecraft().player, entity);
			cache.renderTooltip3D(mc, event.getPartialTicks());
		}
	}

	@SubscribeEvent
	public void render(final RenderGameOverlayEvent.Post event) {
		// TODO Let's make it a choice to do 2D or 3D tooltips. Just need to make a nice anchoring gui first.
		// renderer.renderTooltip2D(mc, item, generateTooltip(mc, mc.player, item.getEntityItem()), event.getPartialTicks());
	}

	public static EntityItem getMouseOver(Minecraft mc, float partialTicks) {
		mc.mcProfiler.startSection(Constants.MODID);
		Entity viewer = mc.getRenderViewEntity();
		int range = Config.getInstance().getMaxDistance();
		Vec3d eyes = viewer.getPositionEyes(partialTicks);
		Vec3d look = viewer.getLook(partialTicks);
		Vec3d view = eyes.addVector(look.x * range, look.y * range, look.z * range);
		double distance = 0;
		EntityItem out = null;
		List<EntityItem> list = mc.world.getEntitiesWithinAABB(EntityItem.class, viewer.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).grow(1F, 1F, 1F));
		for (int i = 0; i < list.size(); i++) {
			EntityItem entity = list.get(i);
			AxisAlignedBB aabb = entity.getEntityBoundingBox().offset(0, 0.25, 0).grow(entity.getCollisionBorderSize() + 0.1);
			RayTraceResult ray = aabb.calculateIntercept(eyes, view);
			if (aabb.contains(eyes)) {
				if (distance > 0) {
					out = entity;
					distance = 0;
				}
			} else if (ray != null) {
				double d = eyes.distanceTo(ray.hitVec);
				if (d < distance || distance == 0.0D) {
					out = entity;
					distance = d;
				}
			}
		}
		mc.mcProfiler.endSection();
		return out;
	}
}
