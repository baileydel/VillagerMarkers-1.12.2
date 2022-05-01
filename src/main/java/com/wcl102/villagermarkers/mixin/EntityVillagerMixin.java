package com.wcl102.villagermarkers.mixin;

import com.wcl102.villagermarkers.packet.PacketHandler;
import com.wcl102.villagermarkers.packet.PacketVillagerLevelUp;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements INpc, IMerchant {

    @Shadow private int careerId;

    @Shadow private int careerLevel;

    public EntityVillagerMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "populateBuyingList", at = @At(value = "HEAD"))
    private void populateBuyingList(CallbackInfo ci) {
        if (this.careerId != 0 && this.careerLevel != 0) {
            PacketHandler.INSTANCE.sendToServer(new PacketVillagerLevelUp.Message(this.getUniqueID()));
        }
    }
}
