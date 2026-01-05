package xyz.datenflieger.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.modules.DamageIndicator;
import xyz.datenflieger.util.HealthRenderStateExtension;

@Mixin(EntityRenderer.class)
public abstract class DamageIndicatorLivingEntityRendererMixin<T extends net.minecraft.entity.Entity, S extends EntityRenderState> {
	private static final Identifier GUI_ATLAS = net.minecraft.client.render.TexturedRenderLayers.GUI_ATLAS_TEXTURE;
	private static final SpriteIdentifier HEART_CONTAINER = new SpriteIdentifier(GUI_ATLAS, Identifier.of("minecraft", "hud/heart/container"));
	private static final SpriteIdentifier HEART_FULL = new SpriteIdentifier(GUI_ATLAS, Identifier.of("minecraft", "hud/heart/full"));
	private static final SpriteIdentifier HEART_HALF = new SpriteIdentifier(GUI_ATLAS, Identifier.of("minecraft", "hud/heart/half"));
	private static final SpriteIdentifier HEART_ABS_FULL = new SpriteIdentifier(GUI_ATLAS, Identifier.of("minecraft", "hud/heart/absorbing_full"));
	private static final SpriteIdentifier HEART_ABS_HALF = new SpriteIdentifier(GUI_ATLAS, Identifier.of("minecraft", "hud/heart/absorbing_half"));

	@Inject(method = "renderLabelIfPresent", at = @At("TAIL"))
	private void moss$renderHealthLabel(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
		DamageIndicator module = DamageIndicator.INSTANCE;
		if (module == null || !module.active()) return;
		if (!(state instanceof HealthRenderStateExtension ext)) return;

		float health = ext.moss$getHealth();
		float maxHealth = ext.moss$getMaxHealth();
		float absorption = module.includeAbsorption.get() ? ext.moss$getAbsorption() : 0f;

		Vec3d labelPos = state.nameLabelPos;
		if (labelPos == null && state instanceof LivingEntityRenderState livingState) {
			labelPos = new Vec3d(0.0, livingState.standingEyeHeight + 0.4F, 0.0);
		}
		if (labelPos == null) return;

		switch (module.displayType.get()) {
			case HEARTS -> renderTexturedHearts(matrices, queue, labelPos, state, cameraState, health, absorption, maxHealth);
			case AMOUNT -> renderTextLabel(matrices, queue, labelPos, state, cameraState, formatAmount(health, maxHealth, absorption));
			case PERCENT -> renderTextLabel(matrices, queue, labelPos, state, cameraState, formatPercent(health, maxHealth));
		}
	}

	@Unique
	private void renderTextLabel(MatrixStack matrices, OrderedRenderCommandQueue queue, Vec3d labelPos, S state, CameraRenderState cameraState, String text) {
		MutableText healthText = Text.literal(text).formatted(Formatting.RED);
		matrices.push();
		matrices.translate(0.0F, 4.5F * 1.15F * 0.025F, 0.0F);
		queue.submitLabel(matrices, labelPos, 0, healthText, true, state.light, state.squaredDistanceToCamera, cameraState);
		matrices.pop();
	}

	@Unique
	private void renderTexturedHearts(MatrixStack matrices, OrderedRenderCommandQueue queue, Vec3d labelPos, S state, CameraRenderState cameraState, float health, float absorption, float maxHealth) {
		final int heartCount = (int)Math.ceil((health + absorption) / 2.0f);
		int displayTmp = Math.max(heartCount, (int)Math.ceil(maxHealth / 2.0f));
		if (displayTmp <= 0) displayTmp = 1;
		final int displayHearts = displayTmp;

		final float scale = 0.14f;
		final float size = 9.0f * scale;
		final float spacing = size + 0.15f;
		final float startX = -(displayHearts * spacing) / 2.0f;

		matrices.push();
		matrices.translate(labelPos.x, labelPos.y, labelPos.z);
		matrices.multiply(cameraState.orientation);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
		matrices.scale(scale, scale, scale);

		int fullHearts = (int)(health / 2.0f);
		boolean halfHeart = (health % 2.0f) >= 1.0f;
		int absFull = (int)(absorption / 2.0f);
		boolean absHalf = (absorption % 2.0f) >= 1.0f;

		var atlasManager = MinecraftClient.getInstance().getAtlasManager();
		var layer = net.minecraft.client.render.RenderLayer.getEntityCutoutNoCull(GUI_ATLAS);

		var containerSprite = atlasManager.getSprite(HEART_CONTAINER);
		var fullSprite = atlasManager.getSprite(HEART_FULL);
		var halfSprite = atlasManager.getSprite(HEART_HALF);
		var absFullSprite = atlasManager.getSprite(HEART_ABS_FULL);
		var absHalfSprite = atlasManager.getSprite(HEART_ABS_HALF);

		queue.submitCustom(matrices, layer, (entry, vc) -> {
			for (int i = 0; i < displayHearts; i++) {
				drawQuad(entry, vc, startX + i * spacing, 0, size, size, containerSprite);
			}
		});

		queue.submitCustom(matrices, layer, (entry, vc) -> {
			for (int i = 0; i < absFull; i++) {
				drawQuad(entry, vc, startX + i * spacing, 0, size, size, absFullSprite);
			}
		});
		if (absHalf) {
			queue.submitCustom(matrices, layer, (entry, vc) -> {
				drawQuad(entry, vc, startX + absFull * spacing, 0, size, size, absHalfSprite);
			});
		}

		queue.submitCustom(matrices, layer, (entry, vc) -> {
			for (int i = 0; i < fullHearts; i++) {
				drawQuad(entry, vc, startX + i * spacing, 0, size, size, fullSprite);
			}
		});
		if (halfHeart) {
			queue.submitCustom(matrices, layer, (entry, vc) -> {
				drawQuad(entry, vc, startX + fullHearts * spacing, 0, size, size, halfSprite);
			});
		}

		matrices.pop();
	}

	@Unique
	private static String formatAmount(float health, float maxHealth, float absorption) {
		float total = health + absorption;
		return String.format("%.0f/%.0f", Math.ceil(total) / 2.0f, Math.ceil(maxHealth) / 2.0f);
	}

	@Unique
	private static String formatPercent(float health, float maxHealth) {
		if (maxHealth <= 0.0f) return "0%";
		return String.format("%.0f%%", (health / maxHealth) * 100.0f);
	}

	@Unique
	private static void drawQuad(MatrixStack.Entry entry, VertexConsumer vc, float x, float y, float w, float h, net.minecraft.client.texture.Sprite sprite) {
		var mat = entry.getPositionMatrix();
		float u0 = sprite.getMinU();
		float v0 = sprite.getMinV();
		float u1 = sprite.getMaxU();
		float v1 = sprite.getMaxV();
		int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;
		int overlay = OverlayTexture.DEFAULT_UV;
		vc.vertex(mat, x, y + h, 0).color(1f, 1f, 1f, 1f).texture(u0, v1).overlay(overlay).light(light).normal(0f, 0f, 1f);
		vc.vertex(mat, x + w, y + h, 0).color(1f, 1f, 1f, 1f).texture(u1, v1).overlay(overlay).light(light).normal(0f, 0f, 1f);
		vc.vertex(mat, x + w, y, 0).color(1f, 1f, 1f, 1f).texture(u1, v0).overlay(overlay).light(light).normal(0f, 0f, 1f);
		vc.vertex(mat, x, y, 0).color(1f, 1f, 1f, 1f).texture(u0, v0).overlay(overlay).light(light).normal(0f, 0f, 1f);
	}
}
