package xyz.datenflieger.util;

public interface HealthRenderStateExtension {
	void moss$setHealth(float health);
	void moss$setMaxHealth(float maxHealth);
	void moss$setAbsorption(float absorption);

	float moss$getHealth();
	float moss$getMaxHealth();
	float moss$getAbsorption();
}
