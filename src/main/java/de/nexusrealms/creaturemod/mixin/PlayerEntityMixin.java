package de.nexusrealms.creaturemod.mixin;

import com.mojang.authlib.GameProfile;
import de.nexusrealms.creaturemod.magic.flow.InventoryFlowStorage;
import de.nexusrealms.creaturemod.magic.flow.InventoryFlowStorageProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements InventoryFlowStorageProvider {
	@Shadow @Final
    PlayerInventory inventory;

	@Unique
	private InventoryFlowStorage inventoryFlowStorage;
	@Inject(method = "<init>", at = @At("TAIL"))
	public void initInventoryFlowStorage(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci){
		inventoryFlowStorage = new InventoryFlowStorage(inventory);
	}
	public InventoryFlowStorage getInventoryFlowStorage() {
		return inventoryFlowStorage;
	}
}