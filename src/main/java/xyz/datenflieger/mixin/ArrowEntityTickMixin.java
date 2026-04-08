package xyz.datenflieger.mixin;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.modules.ArrowTrails;

@Mixin(Arrow.class)
public abstract class ArrowEntityTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void moss$arrowTrails_tick(CallbackInfo ci) {
        ArrowTrails module = ArrowTrails.INSTANCE;
        if (module == null || !module.active()) return;

        Arrow arrow = (Arrow) (Object) this;
        Level world = arrow.level();
        if (!world.isClientSide()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if ((Boolean) module.ownOnly.get() && arrow.getOwner() != null && minecraft.player != null) {
            if (arrow.getOwner().getId() != minecraft.player.getId()) {
                return;
            }
        }

        double speed = arrow.getDeltaMovement().length();
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

        int packedColor = ARGB.color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        DustParticleOptions effect = new DustParticleOptions(packedColor, size);

        int density = (Integer) module.particleDensity.get();
        double spread = ((Integer) module.offsetSpread1000.get()) / 1000.0;
        for (int i = 0; i < density; i++) {
            double xOffset = (arrow.getRandom().nextDouble() - 0.5) * spread;
            double yOffset = (arrow.getRandom().nextDouble() - 0.5) * spread;
            double zOffset = (arrow.getRandom().nextDouble() - 0.5) * spread;
            world.addParticle(
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
