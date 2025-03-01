package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.data.ParticleSettings;
import foundry.veil.api.quasar.data.QuasarParticleData;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import foundry.veil.api.resource.VeilDynamicRegistry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ForwardBeamEffect(RegistryEntry<QuasarParticleData> particleSettings) implements SpellEffect<Entity> {
    public static final MapCodec<ForwardBeamEffect> CODEC = QuasarParticleData.CODEC.fieldOf("particleSettings").xmap(ForwardBeamEffect::new, ForwardBeamEffect::particleSettings);
    public static final Identifier BEAM_EMITTER = CreatureMod.id("colored_beam_emitter");
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.FORWARD_BEAM;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(origin.getWorld() instanceof ClientWorld clientWorld){
            //TODO Figure why Veil particles are weirdly coupled and what that means for my plans
/*            ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
            ParticleEmitter emitter = manager.createEmitter(BEAM_EMITTER);
            emitter.setParticleData(particleSettings.value());*/
        }
        return true;
    }
}
