package xyz.datenflieger.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.datenflieger.modules.DamageIndicator;
import xyz.datenflieger.util.HealthRenderStateExtension;

@Mixin(LivingEntityRenderer.class)
public abstract class DamageIndicatorLivingEntityRendererMixin {
	private static final Identifier HEART_CONTAINER = Identifier.withDefaultNamespace("hud/heart/container");
	private static final Identifier HEART_FULL = Identifier.withDefaultNamespace("hud/heart/full");
	private static final Identifier HEART_HALF = Identifier.withDefaultNamespace("hud/heart/half");
	private static final Identifier HEART_ABS_FULL = Identifier.withDefaultNamespace("hud/heart/absorbing_full");
	private static final Identifier HEART_ABS_HALF = Identifier.withDefaultNamespace("hud/heart/absorbing_half");

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
	private void moss$renderHealthLabel(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState, CallbackInfo ci) {
		DamageIndicator module = DamageIndicator.INSTANCE;
		if (module == null || !module.active()) return;
		if (!(state instanceof HealthRenderStateExtension ext)) return;

		float health = ext.moss$getHealth();
		float maxHealth = ext.moss$getMaxHealth();
		float absorption = module.includeAbsorption.get() ? ext.moss$getAbsorption() : 0f;
		if (maxHealth <= 0.0f) return;

		Vec3 labelPos = state.nameTagAttachment;
		if (labelPos == null && state instanceof LivingEntityRenderState livingState) {
			labelPos = new Vec3(0.0, livingState.eyeHeight + 0.4F, 0.0);
		}
		if (labelPos == null) return;

		String text;
		switch (module.displayType.get()) {
			case Hearts -> {
				renderTexturedHearts(poseStack, submitNodeCollector, labelPos, state, cameraState, health, absorption, maxHealth);
				return;
			}
			case Amount -> text = formatAmount(health, maxHealth, absorption);
			case Percent -> text = formatPercent(health, maxHealth);
			default -> {
				return;
			}
		}

		renderTextLabel(poseStack, submitNodeCollector, labelPos, state, cameraState, text);
	}

	@Unique
	private void renderTextLabel(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Vec3 labelPos, LivingEntityRenderState state, CameraRenderState cameraState, String text) {
		Component healthText = Component.literal(text).withStyle(ChatFormatting.RED);
		poseStack.pushPose();
		poseStack.translate(0.0F, 4.5F * 1.15F * 0.025F, 0.0F);
		submitNodeCollector.submitNameTag(poseStack, labelPos, 0, healthText, !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, cameraState);
		poseStack.popPose();
	}

	@Unique
	private void renderTexturedHearts(
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		Vec3 labelPos,
		LivingEntityRenderState state,
		CameraRenderState cameraState,
		float health,
		float absorption,
		float maxHealth
	) {
		int normalHearts = Math.max(1, (int) Math.ceil(maxHealth / 2.0f));
		int absorptionHearts = (int) Math.ceil(absorption / 2.0f);
		int heartsToDisplay = normalHearts + absorptionHearts;

		int redFull = Math.min(normalHearts, (int) (health / 2.0f));
		boolean redHalf = (health % 2.0f) >= 1.0f && redFull < normalHearts;
		int yellowFull = (int) (absorption / 2.0f);
		boolean yellowHalf = (absorption % 2.0f) >= 1.0f;

		final int heartsPerRow = 10;
		final float pixelSize = 0.02f;
		final float heartSize = 9.0f * pixelSize;
		final float spacing = 8.0f * pixelSize;
		final float rowSpacing = 10.0f * pixelSize;
		final float yOffset = ((Integer) DamageIndicator.INSTANCE.heartsYOffsetPx.get()) * pixelSize;

		Minecraft mc = Minecraft.getInstance();
		var atlas = mc.getAtlasManager();
		TextureAtlasSprite containerSprite = atlas.get(new SpriteId(Sheets.GUI_SHEET, HEART_CONTAINER));
		TextureAtlasSprite fullSprite = atlas.get(new SpriteId(Sheets.GUI_SHEET, HEART_FULL));
		TextureAtlasSprite halfSprite = atlas.get(new SpriteId(Sheets.GUI_SHEET, HEART_HALF));
		TextureAtlasSprite absFullSprite = atlas.get(new SpriteId(Sheets.GUI_SHEET, HEART_ABS_FULL));
		TextureAtlasSprite absHalfSprite = atlas.get(new SpriteId(Sheets.GUI_SHEET, HEART_ABS_HALF));

		poseStack.pushPose();
		poseStack.translate(labelPos.x, labelPos.y + 0.18 + yOffset, labelPos.z);
		poseStack.mulPose(cameraState.orientation);
		poseStack.scale(1.0f, -1.0f, 1.0f);

		RenderType renderType = RenderTypes.entityCutout(Sheets.GUI_SHEET, false);
		int light = state.lightCoords;

		submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
			for (int i = 0; i < heartsToDisplay; i++) {
				int row = i / heartsPerRow;
				int col = i % heartsPerRow;
				int heartsInRow = Math.min(heartsPerRow, heartsToDisplay - row * heartsPerRow);
				float rowWidth = (heartsInRow - 1) * spacing + heartSize;
				float x = -rowWidth / 2.0f + col * spacing;
				float y = row * rowSpacing;

				drawHeartQuad(buffer, pose, x, y, heartSize, containerSprite, light);

				if (i < redFull) {
					drawHeartQuad(buffer, pose, x, y, heartSize, fullSprite, light);
				} else if (redHalf && i == redFull) {
					drawHeartQuad(buffer, pose, x, y, heartSize, halfSprite, light);
				}

				int yellowStart = normalHearts;
				if (i >= yellowStart) {
					int yellowIndex = i - yellowStart;
					if (yellowIndex < yellowFull) {
						drawHeartQuad(buffer, pose, x, y, heartSize, absFullSprite, light);
					} else if (yellowHalf && yellowIndex == yellowFull) {
						drawHeartQuad(buffer, pose, x, y, heartSize, absHalfSprite, light);
					}
				}
			}
		});

		poseStack.popPose();
	}

	@Unique
	private static String formatAmount(float health, float maxHealth, float absorption) {
		float total = health + absorption;
		return compactFloat((float)Math.ceil(total) / 2.0f) + "/" + compactFloat((float)Math.ceil(maxHealth) / 2.0f);
	}

	@Unique
	private static String formatPercent(float health, float maxHealth) {
		if (maxHealth <= 0.0f) return "0%";
		return String.format(Locale.ROOT, "%.0f%%", (health / maxHealth) * 100.0f);
	}

	@Unique
	private static String compactFloat(float value) {
		if (Math.abs(value - Math.round(value)) < 0.001f) {
			return Integer.toString(Math.round(value));
		}
		return String.format(Locale.ROOT, "%.1f", value);
	}

	@Unique
	private static void drawHeartQuad(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float size, TextureAtlasSprite sprite, int light) {
		float u0 = sprite.getU0();
		float v0 = sprite.getV0();
		float u1 = sprite.getU1();
		float v1 = sprite.getV1();
		float x1 = x + size;
		float y1 = y + size;

		buffer.addVertex(pose, x, y1, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv(u0, v1)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(light)
			.setNormal(pose, 0.0f, 0.0f, 1.0f);
		buffer.addVertex(pose, x1, y1, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv(u1, v1)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(light)
			.setNormal(pose, 0.0f, 0.0f, 1.0f);
		buffer.addVertex(pose, x1, y, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv(u1, v0)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(light)
			.setNormal(pose, 0.0f, 0.0f, 1.0f);
		buffer.addVertex(pose, x, y, 0.0f)
			.setColor(255, 255, 255, 255)
			.setUv(u0, v0)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(light)
			.setNormal(pose, 0.0f, 0.0f, 1.0f);
	}
}
