package de.nexusrealms.creaturemod.entities.therianthrope;

import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.curses.CurseInstance;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WerewolfEntity extends TherianthropeEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("Attack");

    public WerewolfEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer getDefaultAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.40000001192092896).build();

    }

    @Override
    public TherianthropyCurse<?> getCurseType() {
        return Curses.LYCANTHROPY;
    }


    @Override
    public int infectionChance() {
        return 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Moving", 0, state ->
        {
            if(state.isMoving()){
                return state.setAndContinue(RUN);
            } else {
                return state.setAndContinue(IDLE);
            }
        }
        ));
        controllers.add(new AnimationController<>(this, "Attacking", 5, state -> PlayState.CONTINUE).triggerableAnim("Attack", ATTACK));
    }

    @Override
    public boolean tryAttack(Entity target) {
        triggerAnim("Attacking", "Attack");
        return super.tryAttack(target);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
