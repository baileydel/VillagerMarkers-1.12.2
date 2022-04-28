package com.wcl102.villagermarkers.mixin;

import com.wcl102.villagermarkers.packet.PacketHandler;
import com.wcl102.villagermarkers.packet.PacketVillagerLevelUp;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements INpc, IMerchant {

    @Shadow public int timeUntilReset;

    @Shadow private boolean needsInitilization;

    @Shadow private UUID lastBuyingPlayer;

    @Shadow private int randomTickDivider;

    @Shadow public abstract boolean isTrading();

    @Shadow @Nullable private MerchantRecipeList buyingList;

    @Shadow protected abstract void populateBuyingList();

    @Shadow private Village village;

    @Shadow private boolean isLookingForHome;

    public EntityVillagerMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "updateAITasks", at = @At("HEAD"), cancellable = true)
    protected void updateAITasks(CallbackInfo ci) {
        ci.cancel();
        if (--this.randomTickDivider <= 0) {
            BlockPos blockpos = new BlockPos(this);
            this.world.getVillageCollection().addToVillagerPositionList(blockpos);
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.village = this.world.getVillageCollection().getNearestVillage(blockpos, 32);

            if (this.village == null) {
                this.detachHome();
            }
            else {
                BlockPos blockpos1 = this.village.getCenter();
                this.setHomePosAndDistance(blockpos1, this.village.getVillageRadius());

                if (this.isLookingForHome)
                {
                    this.isLookingForHome = false;
                    this.village.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    for (MerchantRecipe merchantrecipe : this.buyingList) {
                        if (merchantrecipe.isRecipeDisabled()) {
                            merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                        }
                    }

                    this.populateBuyingList();

                    PacketHandler.INSTANCE.sendToServer(new PacketVillagerLevelUp.Message(this.getUniqueID()));

                    this.needsInitilization = false;

                    if (this.village != null && this.lastBuyingPlayer != null) {
                        this.world.setEntityState(this, (byte)14);
                        this.village.modifyPlayerReputation(this.lastBuyingPlayer, 1);
                    }
                }
                this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
            }
        }
        super.updateAITasks();
    }
}
