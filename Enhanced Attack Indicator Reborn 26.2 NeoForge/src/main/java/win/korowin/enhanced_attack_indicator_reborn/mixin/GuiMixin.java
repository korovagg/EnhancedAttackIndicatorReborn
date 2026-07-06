package win.korowin.enhanced_attack_indicator_reborn.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.korowin.enhanced_attack_indicator_reborn.EnhancedAttackIndicatorRebornClient;

@Mixin(Hud.class)
public class GuiMixin {
    @Unique
    private boolean renderFullness = false;

    @Unique
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.parse("hud/crosshair_attack_indicator_full");

    // Replaces the crosshair attack progress with the mod's contextual progress value.
    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float enhancedAttackIndicatorReborn$setBarProgress(LocalPlayer player, float baseTime) {
        float progress = EnhancedAttackIndicatorRebornClient.getProgress(player.getAttackStrengthScale(baseTime));
        if (progress == 2.0F) renderFullness = true;
        return progress == 2.0F ? 1.0F : progress;
    }

    // Prevents the vanilla plus marker so the custom full indicator can replace it.
    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isAlive()Z"))
    private boolean enhancedAttackIndicatorReborn$dontShowPlus(Entity entity) {
        return false;
    }

    // Draws the full crosshair indicator when the contextual progress wants a completed state.
    @Inject(method = "extractCrosshair", at = @At("TAIL"))
    private void enhancedAttackIndicatorReborn$showPlus(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!renderFullness) return;
        int y = guiGraphics.guiHeight() / 2 - 7 + 16;
        int x = guiGraphics.guiWidth() / 2 - 8;
        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, x, y, 16, 16);
        renderFullness = false;
    }

    // Replaces the hotbar attack progress with the mod's contextual progress value.
    @Redirect(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float enhancedAttackIndicatorReborn$setHotBarProgress(LocalPlayer player, float baseTime) {
        float progress = EnhancedAttackIndicatorRebornClient.getProgress(player.getAttackStrengthScale(baseTime));
        return progress == 2.0F ? 0.99F : progress;
    }
}
