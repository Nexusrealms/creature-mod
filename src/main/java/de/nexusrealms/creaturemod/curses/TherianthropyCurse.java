package de.nexusrealms.creaturemod.curses;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.entities.TherianthropeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

import java.util.Optional;
import java.util.UUID;

public class TherianthropyCurse<T extends TherianthropeEntity> extends Curse {
    private final EntityType<T> entityType;

    public TherianthropyCurse(EntityType<T> entityType) {
        this.entityType = entityType;
    }
    public void transformTo(ServerPlayerEntity player){
        ServerWorld world = player.getServerWorld();
        T entity = entityType.create(world);
        if(entity == null) return;
        entity.refreshPositionAndAngles(player.getPos(), player.getYaw(), player.getPitch());
        player.changeGameMode(GameMode.SPECTATOR);
        player.setCameraEntity(entity);
        player.getServerWorld().spawnEntity(entity);
    }
    public void transformFrom(ServerPlayerEntity player){
        Optional<TherianthropyComponent> component = ModEntityComponents.THERIANTHROPY.maybeGet(player);
        if(component.isPresent()){
            Optional<UUID> optional = component.get().getUuid();
            if(optional.isPresent()){
                Entity entity = player.getServerWorld().getEntity(optional.get());
                player.setCameraEntity(player);
                if(entity != null){
                    entity.discard();
                } else {
                    CreatureMod.LOGGER.error("Found a therianthrope with an invalid entity! Farpo did you forget to handle persistence you moron!");
                }
            }
        }
    }
    public static boolean isTherianthrope(PlayerEntity player){
        return Curses.hasCurse(curseInstance -> curseInstance.getType().value() instanceof TherianthropyCurse<?>, player);
    }
}
