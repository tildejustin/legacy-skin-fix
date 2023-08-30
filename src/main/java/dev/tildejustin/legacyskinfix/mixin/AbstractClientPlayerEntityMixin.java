package dev.tildejustin.legacyskinfix.mixin;

import dev.tildejustin.legacyskinfix.LegacySkinFix;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(method = "getSkinUrl", at = @At(value = "HEAD"), cancellable = true)
    private static void getSkinUrlInject(String playerName, CallbackInfoReturnable<String> cir) {
        if (LegacySkinFix.skins.get(LegacySkinFix.Type.SKIN) != null) {
            cir.setReturnValue(LegacySkinFix.skins.get(LegacySkinFix.Type.SKIN));
        }
    }

    @Inject(method = "getCapeUrl", at = @At(value = "HEAD"), cancellable = true)
    private static void getCapeUrlInject(String playerName, CallbackInfoReturnable<String> cir) {
        if (LegacySkinFix.skins.get(LegacySkinFix.Type.CAPE) != null) {
            cir.setReturnValue(LegacySkinFix.skins.get(LegacySkinFix.Type.CAPE));
        }
    }
}
