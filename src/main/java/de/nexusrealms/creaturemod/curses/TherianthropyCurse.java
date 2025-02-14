package de.nexusrealms.creaturemod.curses;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.entities.therianthrope.TherianthropeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TherianthropyCurse<T extends TherianthropeEntity> extends Curse {
    private final EntityType<T> entityType;
    private final int priority;
    public TherianthropyCurse(EntityType<T> entityType, int priority) {
        this.entityType = entityType;
        this.priority = priority;
    }
    public static boolean transformPlayer(ServerPlayerEntity player){
        List<CurseInstance> curses = Curses.getCurses(player);
        Optional<? extends TherianthropyCurse<?>> curse = curses.stream()
                .filter(curseInstance -> curseInstance.getType().value() instanceof TherianthropyCurse<?>)
                .map(curseInstance -> (TherianthropyCurse<?>) curseInstance.getType().value())
                .min(Comparator.comparingInt(TherianthropyCurse::getPriority));
        return curse.map(therianthropyCurse -> therianthropyCurse.transformTo(player)).orElse(false);
    }
    public int getPriority(){
        return priority;
    }
    public boolean transformTo(ServerPlayerEntity player){
        ServerWorld world = player.getServerWorld();
        T entity = entityType.create(world);
        if(entity == null) return false;
        entity.refreshPositionAndAngles(player.getPos(), player.getYaw(), player.getPitch());
        player.changeGameMode(GameMode.SPECTATOR);
        player.getServerWorld().spawnEntity(entity);
        player.setCameraEntity(entity);
        ModEntityComponents.THERIANTHROPY.maybeGet(player).ifPresent(therianthropyComponent -> therianthropyComponent.setEntity(entity));
        entity.onPlayerTransform(player);
        return true;
    }
    public static void untransformPlayer(ServerPlayerEntity player){
        Optional<TherianthropyComponent> component = ModEntityComponents.THERIANTHROPY.maybeGet(player);
        if(component.isPresent()){
            Optional<UUID> optional = component.get().getUuid();
            if(optional.isPresent()){
                Entity entity = player.getServerWorld().getEntity(optional.get());
                player.setCameraEntity(player);
                player.changeGameMode(GameMode.SURVIVAL);
                component.get().setEntity(null);

                if(entity instanceof TherianthropeEntity therianthrope){
                    therianthrope.onPlayerUntransform(player);
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
