package de.nexusrealms.creaturemod.entities.therianthrope;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.curses.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.CowEntity;
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
    public abstract TherianthropyCurse<?> getCurseType();
    public CurseInstance createCurseInstance() {
        return new CurseInstance(ModRegistries.CURSES.getEntry(getCurseType()), false, false);
    }

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

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        super.onKilledBy(adversary);
        if(getWorld() instanceof ServerWorld world && getOwner() != null){
            TherianthropyCurse.untransformPlayer((ServerPlayerEntity) world.getPlayerByUuid(ownerId));
        }
    }
    public void onPlayerTransform(ServerPlayerEntity player){
        setOwner(player);
    }
    public void onPlayerUntransform(ServerPlayerEntity player){
        discard();
    }
}
