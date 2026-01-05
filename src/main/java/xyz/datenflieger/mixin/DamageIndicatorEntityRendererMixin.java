package xyz.datenflieger.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.modules.DamageIndicator;
import xyz.datenflieger.util.HealthRenderStateExtension;

@Mixin(EntityRenderer.class)
public abstract class DamageIndicatorEntityRendererMixin<T extends Entity, S extends EntityRenderState> {

	@Inject(method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V", at = @At("TAIL"))
	private void moss$storeHealth(T entity, S state, float tickDelta, CallbackInfo ci) {
		if (!(entity instanceof LivingEntity living) || !(state instanceof HealthRenderStateExtension ext)) return;

		DamageIndicator module = DamageIndicator.INSTANCE;
		if (module == null || !module.active()) return;
		boolean isPlayer = living instanceof net.minecraft.entity.player.PlayerEntity;
		if (isPlayer && !module.includePlayers.get()) return;
		if (!isPlayer && !module.includeOtherEntities.get()) return;

		ext.moss$setHealth(living.getHealth());
		ext.moss$setMaxHealth(living.getMaxHealth());
		ext.moss$setAbsorption(living.getAbsorptionAmount());
	}
}
