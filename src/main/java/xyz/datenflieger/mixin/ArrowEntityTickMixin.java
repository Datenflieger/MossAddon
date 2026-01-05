package xyz.datenflieger.mixin;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.modules.ArrowTrails;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void moss$arrowTrails_tick(CallbackInfo ci) {
        ArrowTrails module = ArrowTrails.INSTANCE;
        if (module == null || !module.active()) return;

        ArrowEntity arrow = (ArrowEntity) (Object) this;
        World world = arrow.getEntityWorld();
        if (!world.isClient()) return;

        if ((Boolean) module.ownOnly.get() && arrow.getOwner() != null && MinecraftClient.getInstance().player != null) {
            if (arrow.getOwner().getId() != MinecraftClient.getInstance().player.getId()) {
                return;
            }
        }

        double speed = arrow.getVelocity().length();
        double minSpeed = ((Integer) module.minSpeed100.get()) / 100.0;
        if (speed < minSpeed) return;

        float size = ((Integer) module.particleSize10.get()) / 10.0f;
        Color rgb;
        if ((Boolean) module.rainbow.get()) {
            float hueSpeed = ((Integer) module.rgbSpeed.get()) / 1000.0f;
            long time = System.currentTimeMillis();
            float hue = (time % (long) (360.0f / hueSpeed)) * hueSpeed / 360.0f;
            rgb = Color.getHSBColor(hue, 1.0f, 1.0f);
        } else {
            rgb = module.color.get().value();
        }

        Vector3f vec = new Vector3f(rgb.getRed() / 255.0f, rgb.getGreen() / 255.0f, rgb.getBlue() / 255.0f);
        int packedColor = ColorHelper.getArgb(
            (int)(vec.x * 255.0f),
            (int)(vec.y * 255.0f),
            (int)(vec.z * 255.0f)
        );
        DustParticleEffect effect = new DustParticleEffect(packedColor, size);

        int density = (Integer) module.particleDensity.get();
        double spread = ((Integer) module.offsetSpread1000.get()) / 1000.0;
        for (int i = 0; i < density; i++) {
            double xOffset = (world.random.nextDouble() - 0.5) * spread;
            double yOffset = (world.random.nextDouble() - 0.5) * spread;
            double zOffset = (world.random.nextDouble() - 0.5) * spread;
            world.addParticleClient(
                effect,
                arrow.getX() + xOffset,
                arrow.getY() + yOffset,
                arrow.getZ() + zOffset,
                0.0D,
                0.0D,
                0.0D
            );
        }
    }
}
