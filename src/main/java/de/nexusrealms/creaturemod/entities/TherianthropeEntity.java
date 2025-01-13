package de.nexusrealms.creaturemod.entities;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.curses.Curse;
import de.nexusrealms.creaturemod.curses.CurseInstance;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class TherianthropeEntity extends HostileEntity implements Ownable {
    private UUID ownerId;
    private ServerPlayerEntity owner;

    protected TherianthropeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    public void setOwner(ServerPlayerEntity owner){
        this.owner = owner;
        this.ownerId = owner.getUuid();
    }
    @Override
    public @Nullable Entity getOwner() {
        if(owner != null){
            return owner;
        } else if (ownerId != null && getWorld() instanceof ServerWorld world){
            owner = (ServerPlayerEntity) world.getEntity(ownerId);
            return owner;
        } else {
            CreatureMod.LOGGER.error("A therianthrope entity was found with no owner, discarding.");
            this.discard();
            return null;
        }
    }
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FleeEntityGoal(this, ArmadilloEntity.class, 6.0F, 1.0, 1.2, (entity) -> {
            return !((ArmadilloEntity)entity).isNotIdle();
        }));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 0.8 ,false));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }
    public abstract Curse getCurseType();
    public abstract CurseInstance createCurseInstance();

    ///The closer to zero, the more likely
    public abstract int infectionChance();
    @Override
    public boolean tryAttack(Entity target) {
        if(target instanceof ServerPlayerEntity player){
            if(!TherianthropyCurse.isTherianthrope(player) && getWorld().getRandom().nextBetween(0, infectionChance()) == 0){
                CurseInstance instance = createCurseInstance();
                Curses.applyCurse(instance, player);
            }
        }
        return super.tryAttack(target);
    }
}
