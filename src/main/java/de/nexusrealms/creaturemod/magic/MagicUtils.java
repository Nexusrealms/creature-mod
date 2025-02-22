package de.nexusrealms.creaturemod.magic;

import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.magic.flow.FlowStorage;
import de.nexusrealms.creaturemod.magic.flow.PlayerFlowStorageComponent;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class MagicUtils {
    public static boolean shouldDoSorcery(PlayerEntity player){
        return true;
    }
    public static int getComponentDissipationFactor(PlayerEntity player){
        return 0;
    }
    public static <T extends PlayerEntity> void doIfDoesSorcery(Collection<T> players, Consumer<T> action){
        players.stream().filter(MagicUtils::shouldDoSorcery).forEach(action);
    }
}
