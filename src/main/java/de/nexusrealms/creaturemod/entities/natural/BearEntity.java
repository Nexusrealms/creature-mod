package de.nexusrealms.creaturemod.entities.natural;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.brain.ModActivities;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.entities.brain.sensor.EntitiesAroundGuardedPositionSensor;
import de.nexusrealms.creaturemod.entities.brain.task.*;
import de.nexusrealms.creaturemod.entities.spectral.WraithEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

public class BearEntity extends AnimalEntity implements SmartBrainOwner<BearEntity>, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run");

    public static final BiPredicate<BlockState, BearEntity> BEEHIVE_PREDICATE = (state, bearEntity) -> state.isIn(BlockTags.BEEHIVES) && BeehiveBlockEntity.getHoneyLevel(state) > 0;

    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("Attack");
    protected static final RawAnimation HONEY = RawAnimation.begin().thenPlay("Honey");
    protected int hungerTicks = 0;
    public BearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.MEAT);
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.BEAR.create(world);
    }
    public static DefaultAttributeContainer getDefaultAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20000001192092896)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1).build();

    }


    @Override
    public void baseTick() {
        super.baseTick();
        if(hungerTicks > 0){
            hungerTicks--;
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        hungerTicks = nbt.getInt("hungerticks");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("hungerticks", hungerTicks);
    }
    public void eatHoneyAtPos(BlockPos pos){
        triggerAnim("Attacking", "Honey");
        BlockState state = getWorld().getBlockState(pos);
        getWorld().setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, 0));
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Moving", 0, state ->
        {
            if(state.isMoving()){
                if(state.getAnimatable().getVelocity().length() > 0.1){
                    return state.setAndContinue(RUN);
                } else {
                    return state.setAndContinue(WALK);
                }
            }
            return PlayState.STOP;
        }
        ));
        controllers.add(new AnimationController<>(this, "Attacking", 5, state -> PlayState.CONTINUE).triggerableAnim("Attack", ATTACK).triggerableAnim("Honey", HONEY));
    }

    @Override
    public void swingHand(Hand hand) {
        triggerAnim("Attacking", "Attack");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public List<? extends ExtendedSensor<? extends BearEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(), // This tracks nearby entities
                new HurtBySensor<>(),
                new NearbyBlocksSensor<BearEntity>()
                        .setRadius(16)
                        .setPredicate(BEEHIVE_PREDICATE)
        );
    }

    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void mobTick() {
        tickBrain(this);
    }
    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        if(other instanceof SalmonEntity){
            BrainUtils.setForgettableMemory(this, ModMemories.FULL, Unit.INSTANCE, 1000);
        }
        return super.onKilledOther(world, other);
    }
    @Override
    public BrainActivityGroup<BearEntity> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<BearEntity> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(// Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new RetaliateOrTargetOthers<>().retaliationPredicate(living -> true).attackablePredicate(living -> living instanceof SalmonEntity),          // Set the attack target and walk target based on nearby entities
                        new BreedWithPartner<>(),
                        new FindHoney<>(),
                        new SetEntityLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetween(30, 60)))); // Do nothing for 1.5->3 seconds
    }

    @Override
    public List<Activity> getActivityPriorities() {
        return ObjectArrayList.of(Activity.FIGHT,ModActivities.EAT_HONEY, Activity.IDLE);
    }

    @Override
    public Set<Activity> getScheduleIgnoringActivities() {
        return ObjectArraySet.of(Activity.FIGHT, ModActivities.EAT_HONEY);
    }
    public boolean isInEatingRange(BlockPos pos){
        return getBoundingBox().expand(1).contains(pos.toCenterPos());
    }
    @Override
    public BrainActivityGroup<BearEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }
    public BrainActivityGroup<BearEntity> getEatHoneyTasks(){
        return ModActivities.createActivityGroup(ModActivities.EAT_HONEY,
                bearEntityBrainActivityGroup -> bearEntityBrainActivityGroup.priority(8).requireAndWipeMemoriesOnUse(ModMemories.TARGET_BEEHIVE),
                new InvalidateTargetBeehive<>(),
                new SetTargetBeehiveToWalkTarget<>(),
                new EatFromBeehive()
        );
    }
    @Override
    public Map<Activity, BrainActivityGroup<? extends BearEntity>> getAdditionalTasks() {
        Map<Activity, BrainActivityGroup<? extends BearEntity>> map = new Object2ObjectOpenHashMap<>(1);
        map.put(ModActivities.EAT_HONEY, getEatHoneyTasks());
        return map;
    }
}
