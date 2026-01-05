package xyz.datenflieger.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import xyz.datenflieger.util.HealthRenderStateExtension;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements HealthRenderStateExtension {
	private float moss$health;
	private float moss$maxHealth;
	private float moss$absorption;

	@Override
	public void moss$setHealth(float health) {
		this.moss$health = health;
	}

	@Override
	public void moss$setMaxHealth(float maxHealth) {
		this.moss$maxHealth = maxHealth;
	}

	@Override
	public void moss$setAbsorption(float absorption) {
		this.moss$absorption = absorption;
	}

	@Override
	public float moss$getHealth() {
		return moss$health;
	}

	@Override
	public float moss$getMaxHealth() {
		return moss$maxHealth;
	}

	@Override
	public float moss$getAbsorption() {
		return moss$absorption;
	}
}
