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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WerebearEntity extends TherianthropeEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("Idle");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    protected static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("Attack");

    public WerebearEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer getDefaultAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.39000001192092896).build();

    }

    @Override
    public TherianthropyCurse<?> getCurseType() {
        return Curses.LYCANTHROPY;
    }

    @Override
    public CurseInstance createCurseInstance() {
        return new CurseInstance(ModRegistries.CURSES.getEntry(getCurseType()), false, false);
    }
    @Override
    public int infectionChance() {
        return 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Transforming", 0, state -> PlayState.CONTINUE).triggerableAnim("Transform", TRANSFORM));
        controllers.add(new AnimationController<>(this, "Moving", 0, state ->
        {
            if(state.isMoving()){
                if(state.getAnimatable().getVelocity().length() > 0.1){
                    return state.setAndContinue(RUN);
                } else {
                    return state.setAndContinue(WALK);
                }
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

    @Override
    public void onPlayerTransform(ServerPlayerEntity player) {
        super.onPlayerTransform(player);
        triggerAnim("Transforming", "Transform");
    }
}
