package de.nexusrealms.creaturemod.entities;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import de.nexusrealms.creaturemod.CreatureMod;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class BearEntity extends AnimalEntity implements Angerable, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("Run");

    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("Attack");
    protected static final RawAnimation HONEY = RawAnimation.begin().thenPlay("Honey");
    protected int hungerTicks = 0;

    @Nullable
    private UUID angryAt;
    private int angerTime;
    protected BearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20000001192092896).build();

    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(7, new SearchForHoneyGoal(this, 1.0, 16));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        //this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, SalmonEntity.class, true, false));
        this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));
        super.initGoals();
    }

    @Override
    public boolean tryAttack(Entity target) {
        triggerAnim("Attacking", "Attack");
        return super.tryAttack(target);
    }

    @Override
    public int getAngerTime() {
        return angerTime;
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public @Nullable UUID getAngryAt() {
        return angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        angerTime = 50;
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

    @Override
    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        if(other instanceof SalmonEntity salmon){
            hungerTicks += 2000;
        }
        return super.onKilledOther(world, other);
    }
    protected void eatHoneyAtPos(BlockPos pos){
        triggerAnim("Attacking", "Honey");
        BlockState state = getWorld().getBlockState(pos);
        getWorld().setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, 0));
        hungerTicks += 2000;
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    private static class SearchForHoneyGoal extends MoveToTargetPosGoal{
        private BlockPos hive = null;
        private final BearEntity bear;
        public SearchForHoneyGoal(BearEntity mob, double speed, int range) {
            super(mob, speed, range);
            bear = mob;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && bear.hungerTicks <= 0;
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++){
                for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++){
                    for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++){
                        if(world.getBlockState(new BlockPos(x, y, z)).isIn(BlockTags.BEEHIVES)){
                            if(BeehiveBlockEntity.getHoneyLevel(world.getBlockState(new BlockPos(x, y, z))) > 0){
                                hive = new BlockPos(x, y, z);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void tick() {
            super.tick();
            if(hasReached()){
                bear.eatHoneyAtPos(hive);
            }
        }
    }
}
