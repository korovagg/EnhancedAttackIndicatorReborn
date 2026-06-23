package win.korowin.enhanced_attack_indicator_reborn.mixin;

import win.korowin.enhanced_attack_indicator_reborn.EnhancedAttackIndicatorReborn;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept crosshair and hotbar attack indicator rendering.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Unique boolean renderFullness = false;

	/**
	 * Redirects the weapon progress calculation for the crosshair.
	 */
	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
	private float setBarProgress(ClientPlayerEntity player, float baseTime) {

		float progress = EnhancedAttackIndicatorReborn.getProgress(player.getAttackCooldownProgress(baseTime));

		if (progress == 2.0F)
			renderFullness = true;

		return progress == 2.0F ? 1.0F : progress;

	}

	/**
	 * Prevents the default "+" (charged) indicator from showing, as we handle it ourselves.
	 */
	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isAlive()Z"))
	private boolean dontShowPlus(Entity _entity) {
		return false;
	}

	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.of("hud/crosshair_attack_indicator_full");

	/**
	 * Injects the custom "+" indicator when renderFullness is true.
	 */
	@Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
	private void showPlus(DrawContext context, RenderTickCounter tickDelta, CallbackInfo info) {
		if (renderFullness) {
			int j = context.getScaledWindowHeight() / 2 - 7 + 16;
			int k = context.getScaledWindowWidth() / 2 - 8;
			context.drawGuiTexture(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
			renderFullness = false;
		}
	}

	/**
	 * Redirects the weapon progress calculation for the hotbar.
	 */
	@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
	private float setHotBarProgress(ClientPlayerEntity player, float baseTime) {
		float progress = EnhancedAttackIndicatorReborn.getProgress(player.getAttackCooldownProgress(baseTime));
		return progress == 2.0F ? 0.99F : progress;
	}

}
