package de.nexusrealms.creaturemod.entities.elemental;

import de.nexusrealms.creaturemod.CreatureMod;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomHoverTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class AirElementalEntity extends PathAwareEntity implements SmartBrainOwner<AirElementalEntity>, GeoEntity {
    private boolean hasAttachedEmitter = false;
    private boolean isDamageEmitterAttached = false;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation MOVE = RawAnimation.begin().thenLoop("Move");
    private static final Identifier EMITTER = CreatureMod.id("air_elemental_body");
    private static final Identifier HURT_EMITTER = CreatureMod.id("air_elemental_hurt");

    public AirElementalEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 90, true);
    }
    public static DefaultAttributeContainer getDefaultAttributes(){
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.50000001192092896).build();

    }
    @Override
    public List<? extends ExtendedSensor<? extends AirElementalEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(), // This tracks nearby entities
                new HurtBySensor<>());
    }
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }
    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.isIn(DamageTypeTags.IS_FIRE) || damageSource.isIn(DamageTypeTags.IS_PLAYER_ATTACK);
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
    public void tick() {
        super.tick();
        if(getWorld().isClient() && !hasAttachedEmitter){
            ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
            ParticleEmitter emitter = manager.createEmitter(EMITTER);
            emitter.setAttachedEntity(this);
            manager.addParticleSystem(emitter);
            hasAttachedEmitter = true;
        }
        if(getWorld().isClient()){
            if(!isDamageEmitterAttached && hurtTime > 0){
                ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
                ParticleEmitter emitter = manager.createEmitter(HURT_EMITTER);
                emitter.setAttachedEntity(this);
                manager.addParticleSystem(emitter);
                isDamageEmitterAttached = true;
            }
            if(isDamageEmitterAttached && hurtTime <= 0){
                isDamageEmitterAttached = false;
            }
        }
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
    public BrainActivityGroup<AirElementalEntity> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<AirElementalEntity> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(// Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>(),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomHoverTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetween(30, 60)))); // Do nothing for 1.5->3 seconds
    }
    @Override
    public BrainActivityGroup<AirElementalEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>().invalidateIf((entity, target) -> target instanceof PlayerEntity player && player.getAbilities().invulnerable), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, animationState -> animationState.setAndContinue(MOVE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
