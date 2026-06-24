package win.korowin.enhanced_attack_indicator_reborn.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private boolean renderFullness = false;

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float enhancedAttackIndicatorReborn$setBarProgress(LocalPlayer player, float baseTime) {
        float progress = EnhancedAttackIndicatorRebornClient.getProgress(player.getAttackStrengthScale(baseTime));
        if (progress == 2.0F) renderFullness = true;
        return progress == 2.0F ? 1.0F : progress;
    }

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isAlive()Z"))
    private boolean enhancedAttackIndicatorReborn$dontShowPlus(Entity _entity) {
        return false;
    }

    @Unique
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.parse("hud/crosshair_attack_indicator_full");

    @Inject(method = "extractCrosshair", at = @At("TAIL"))
    private void enhancedAttackIndicatorReborn$showPlus(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!renderFullness) return;
        int j = guiGraphics.guiHeight() / 2 - 7 + 16;
        int k = guiGraphics.guiWidth() / 2 - 8;
        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
        renderFullness = false;
    }

    @Redirect(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float enhancedAttackIndicatorReborn$setHotBarProgress(LocalPlayer player, float baseTime) {
        float progress = EnhancedAttackIndicatorRebornClient.getProgress(player.getAttackStrengthScale(baseTime));
        return progress == 2.0F ? 0.99F : progress;
    }
}

