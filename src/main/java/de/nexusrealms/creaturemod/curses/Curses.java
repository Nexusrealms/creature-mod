package de.nexusrealms.creaturemod.curses;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.therianthrope.WerebearEntity;
import de.nexusrealms.creaturemod.entities.therianthrope.WerewolfEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;

import java.util.List;
import java.util.function.Predicate;

public class Curses {
    public static final TherianthropyCurse<WerewolfEntity> LYCANTHROPY = create("lycanthropy", new TherianthropyCurse<>(ModEntities.WEREWOLF, 0));
    public static final TherianthropyCurse<WerebearEntity> URSANTHROPY = create("ursanthropy", new TherianthropyCurse<>(ModEntities.WEREBEAR, 1));

    private static <T extends Curse> T create(String name, T curse){
        return Registry.register(ModRegistries.CURSES, CreatureMod.id(name), curse);
    }
    public static void applyCurse(CurseInstance instance, LivingEntity living){
        ModEntityComponents.CURSES.maybeGet(living).ifPresent(curseComponent -> curseComponent.addCurse(instance));
    }
    public static void removeAllCurses(Predicate<CurseInstance> predicate, LivingEntity living){
        ModEntityComponents.CURSES.maybeGet(living).ifPresent(curseComponent -> curseComponent.removeCurses(predicate));
    }
    public static List<CurseInstance> getCurses(LivingEntity living){
        return ModEntityComponents.CURSES.maybeGet(living).orElse(new CurseComponent(living)).getCurses();
    }
    public static boolean hasCurse(Predicate<CurseInstance> predicate, LivingEntity living){
        return getCurses(living).stream().anyMatch(predicate);
    }
    public static void init(){}
}
