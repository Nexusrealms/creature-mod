package de.nexusrealms.creaturemod.magic;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.function.Consumer;

public class MagicChecks {
    public static boolean shouldDoSorcery(PlayerEntity player){
        return true;
    }
    public static int getComponentDissipationFactor(PlayerEntity player){
        return 0;
    }
    public static <T extends PlayerEntity> void doIfDoesSorcery(Collection<T> players, Consumer<T> action){
        players.stream().filter(MagicChecks::shouldDoSorcery).forEach(action);
    }
}
