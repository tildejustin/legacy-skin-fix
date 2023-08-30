package dev.tildejustin.legacyskinfix.mixin;

import dev.tildejustin.legacyskinfix.LegacySkinFix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    public ClientPlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void getSkinUrlInject(Minecraft world, World session, Session i, int par4, CallbackInfo ci) {
        if (LegacySkinFix.skins.get(LegacySkinFix.Type.SKIN) != null) {
            this.skinUrl = LegacySkinFix.skins.get(LegacySkinFix.Type.SKIN);
        }
    }

    @Inject(method = "method_2510", at = @At(value = "TAIL"))
    private void getCapeUrlInject(CallbackInfo ci) {
        if (LegacySkinFix.skins.get(LegacySkinFix.Type.CAPE) != null) {
            this.field_3236 = this.field_4008 = LegacySkinFix.skins.get(LegacySkinFix.Type.CAPE);
        }
    }
}
