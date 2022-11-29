package com.wcl102.villagermarkers;

import com.wcl102.villagermarkers.client.resource.VillagerResource;
import com.wcl102.villagermarkers.network.Network;
import com.wcl102.villagermarkers.network.packets.PacketVillagerData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

public class ServerVillagerManager {
    Map<IMerchant, Integer> merchants = new HashMap<>();

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        IMerchant r = null;

        for (Map.Entry<IMerchant, Integer> e : merchants.entrySet()) {
            if (e.getKey().getCustomer() == null) {
                int ticks = e.getValue();

                if (ticks == 100) {
                    EntityVillager villager = (EntityVillager) e.getKey();

                    if (villager != null) {
                        VillagerResource resource = new VillagerResource(villager);

                        // Send to all
                        Network.INSTANCE.sendToAll(new PacketVillagerData.Message(villager.getUniqueID(), resource));
                    }
                    r = e.getKey();
                }
                else {
                    merchants.put(e.getKey(), ticks + 1);
                }
            }
        }
        merchants.remove(r);
    }


    @SubscribeEvent
    public void trading(MerchantTradeOffersEvent event) {
        IMerchant e = event.getMerchant();

        // add merchant to list and go to server tick to see if it still has customer
        if (e instanceof EntityVillager) {
            merchants.put(e, 0);
        }
    }
}