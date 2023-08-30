package dev.tildejustin.legacyskinfix.mixin;

import dev.tildejustin.legacyskinfix.LegacySkinFix;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "initializeGame", at = @At(value = "TAIL"))
    private void initMod(CallbackInfo ci) {
        LegacySkinFix.initialize();
    }
}
