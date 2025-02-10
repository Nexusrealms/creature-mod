package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.util.CreatureModBrainUtils;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.Optional;

public class TeleportHome<E extends MobEntity> extends ExtendedBehaviour<E> {
    private final Optional<Identifier> identifier;
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3).usesMemories(MemoryModuleType.HOME, ModMemories.IS_HOME_THREATHENED);

    public TeleportHome(Identifier particleEmitter) {
        this.identifier = Optional.of(particleEmitter);
    }
    public TeleportHome() {
        this.identifier = Optional.empty();
    }
    protected boolean doStartCheck(ServerWorld level, E entity, long gameTime) {
        return (CreatureModBrainUtils.hasMemoryConditional(entity, MemoryModuleType.HOME, memory -> memory.pos().isWithinDistance(entity.getPos(), entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)))
                && BrainUtils.hasMemory(entity, ModMemories.IS_HOME_THREATHENED)
                && super.doStartCheck(level, entity, gameTime));
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.clearMemory(entity, ModMemories.IS_HOME_THREATHENED);
        GlobalPos pos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
        identifier.ifPresent(i -> {
            ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
            ParticleEmitter emitter = manager.createEmitter(i);
            Vec3d vec3d = pos.pos().toCenterPos();
            emitter.setPosition(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            manager.addParticleSystem(emitter);
        });
        if(pos.dimension().equals(entity.getWorld().getRegistryKey())){
            entity.teleport(pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), true);
        } else {
            entity.teleportTo(new TeleportTarget(entity.getServer().getWorld(pos.dimension()),
                    pos.pos().toCenterPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
        }
    }
}
