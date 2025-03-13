package de.nexusrealms.creaturemod.entities.projectile;

import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.entity.LaunchSpellEffect;
import de.nexusrealms.creaturemod.util.DirectedBlockPosition;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaunchedSpellEntity extends ExplosiveProjectileEntity {
    //Yeah this is definitely not the best way
    private static TrackedData<String> EMITTER = DataTracker.registerData(LaunchedSpellEntity.class, TrackedDataHandlerRegistry.STRING);
    public LaunchedSpellEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
    private boolean hasAttachedEmitter = false;
    private LaunchSpellEffect launchEffect;
    private ItemStack castItem;
    //TODO Also store the clickTarget if it becomes necessary
    @Override
    public void tick() {
        super.tick();
        if(getWorld() instanceof ClientWorld){
            if(Identifier.tryParse(dataTracker.get(EMITTER)) instanceof Identifier emitterId){
                if(!hasAttachedEmitter){
                    ParticleSystemManager particleSystemManager = VeilRenderSystem.renderer().getParticleManager();
                    ParticleEmitter emitter = particleSystemManager.createEmitter(emitterId);
                    if(emitter != null){
                        emitter.setAttachedEntity(this);
                        particleSystemManager.addParticleSystem(emitter);
                        hasAttachedEmitter = true;
                    }
                }
            }
        }
    }

    @Override
    protected @Nullable ParticleEffect getParticleType() {
        return null;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    public void setData(Entity summonAt, Identifier emitter, LaunchSpellEffect launchEffect, ItemStack castItem){
        refreshPositionAndAngles(summonAt.getEyePos(), summonAt.getYaw(), summonAt.getPitch());
        refreshPosition();
        this.setVelocity(summonAt.getRotationVector().normalize().multiply(1f));
        this.velocityDirty = true;
        dataTracker.set(EMITTER, emitter.toString());
        this.launchEffect = launchEffect;
        this.castItem = castItem;
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(getOwner() instanceof ServerPlayerEntity player){
            launchEffect.entityHitEffect().ifPresent(effect -> effect.apply(player, entityHitResult.getEntity(), castItem, null));
        }
        discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if(getOwner() instanceof ServerPlayerEntity player){
            launchEffect.blockHitEffect().ifPresent(effect -> effect.apply(player, new DirectedBlockPosition(getWorld(), blockHitResult.getBlockPos(), false, blockHitResult.getSide()), castItem, null));
        }
        discard();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Emitter", dataTracker.get(EMITTER));
        nbt.put("LaunchEffect", LaunchSpellEffect.STORE_CODEC.encodeStart(getRegistryManager().getOps(NbtOps.INSTANCE), launchEffect).getOrThrow());
        nbt.put("CastItem", ItemStack.CODEC.encodeStart(getRegistryManager().getOps(NbtOps.INSTANCE), castItem).result().orElse(null));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        dataTracker.set(EMITTER, nbt.getString("emitter"));
        launchEffect = LaunchSpellEffect.STORE_CODEC.parse(getRegistryManager().getOps(NbtOps.INSTANCE), nbt.getCompound("LaunchEffect")).getOrThrow();
        castItem = ItemStack.CODEC.parse(getRegistryManager().getOps(NbtOps.INSTANCE), nbt.getCompound("CastItem")).result().orElse(null);
    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(EMITTER, "!");
    }
}
