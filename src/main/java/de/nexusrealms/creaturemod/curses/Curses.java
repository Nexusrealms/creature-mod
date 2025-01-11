package de.nexusrealms.creaturemod.curses;

import de.nexusrealms.creaturemod.ModEntityComponents;
import net.minecraft.entity.LivingEntity;

import java.util.List;
import java.util.function.Predicate;

public class Curses {

    public static void applyCurse(CurseInstance instance, LivingEntity living){
        ModEntityComponents.CURSES.maybeGet(living).ifPresent(curseComponent -> curseComponent.addCurse(instance));
    }
    public static void removeAllCurses(Predicate<CurseInstance> predicate, LivingEntity living){
        ModEntityComponents.CURSES.maybeGet(living).ifPresent(curseComponent -> curseComponent.removeCurses(predicate));
    }
    public static List<CurseInstance> getCurses(LivingEntity living){
        return ModEntityComponents.CURSES.maybeGet(living).orElse(new CurseComponent(living)).getCurses();
    }
}
