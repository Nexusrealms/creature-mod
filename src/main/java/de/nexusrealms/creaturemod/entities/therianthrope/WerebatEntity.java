package de.nexusrealms.creaturemod.entities.therianthrope;

import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import de.nexusrealms.creaturemod.entities.brain.task.SetEntityLookTarget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomFlyingTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class WerebatEntity extends TherianthropeEntity implements SmartBrainOwner<WerebatEntity>, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation CHASE = RawAnimation.begin().thenLoop("Chase");
    protected static final RawAnimation FLY = RawAnimation.begin().thenLoop("Fly");
    protected static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("Transform");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("Attack");
    public WerebatEntity(EntityType<? extends TherianthropeEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);

    }

    public static DefaultAttributeContainer getDefaultAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.30000001192092896).build();

    }

    @Override
    public void swingHand(Hand hand) {
        triggerAnim("Attacking", "Attack");
    }
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }
    @Override
    protected EntityNavigation createNavigation(World world) {
        SmoothFlyingPathNavigation navigation1 = new SmoothFlyingPathNavigation(this, world);
        navigation1.setCanPathThroughDoors(false);
        navigation1.setCanSwim(true);
        navigation1.setCanEnterOpenDoors(true);
        return navigation1;
    }
    @Override
    public List<? extends ExtendedSensor<? extends WerebatEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(), // This tracks nearby entities
                new HurtBySensor<>()
        );
    }

    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }
    @Override
    public BrainActivityGroup<WerebatEntity> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }
    @Override
    public BrainActivityGroup<WerebatEntity> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(// Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>().attackablePredicate(entity -> entity instanceof PlayerEntity || entity instanceof AllayEntity),          // Set the attack target and walk target based on nearby entities
                        new SetEntityLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomFlyingTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetween(30, 60)))); // Do nothing for 1.5->3 seconds
    }
    @Override
    public BrainActivityGroup<WerebatEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Transforming", 0, state -> PlayState.CONTINUE).triggerableAnim("Transform", TRANSFORM));
        controllers.add(new AnimationController<>(this, "Moving", 5, state ->
        {
            if(state.isMoving()){
                return state.setAndContinue(CHASE);
            } else {
                return state.setAndContinue(FLY);
            }
        }
        ));
        controllers.add(new AnimationController<>(this, "Attacking", 5, state -> PlayState.CONTINUE).triggerableAnim("Attack", ATTACK));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void mobTick() {
        tickBrain(this);
    }

    @Override
    public TherianthropyCurse<?> getCurseType() {
        return Curses.NETOANTHROPY;
    }

    @Override
    public void onPlayerTransform(ServerPlayerEntity player) {
        super.onPlayerTransform(player);
        triggerAnim("Transforming", "Transform");
    }
    @Override
    public int infectionChance() {
        return 0;
    }
}
