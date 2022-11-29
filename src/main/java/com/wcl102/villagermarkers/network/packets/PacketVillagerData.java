package com.wcl102.villagermarkers.network.packets;

import com.wcl102.villagermarkers.client.ClientVillagerManager;
import com.wcl102.villagermarkers.client.resource.VillagerResource;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

public class PacketVillagerData implements IMessageHandler<PacketVillagerData.Message, IMessage> {

    @Override
    public IMessage onMessage(PacketVillagerData.Message message, MessageContext ctx) {
        // Server Response
        if (ctx.side.isServer()) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            //TODO redo
            EntityVillager villager = (EntityVillager) player.mcServer.getWorld(player.dimension).getEntityFromUuid(Objects.requireNonNull(UUID.fromString(message.tag.getString("UUID"))));

            if (villager != null) {
                VillagerResource r = new VillagerResource(villager);

                // Send to Client
                return new Message(villager.getUniqueID(), r);
            }
        }
        else {
            // Client Response
            ClientVillagerManager.add(UUID.fromString(message.tag.getString("UUID")), new VillagerResource(message.tag));
        }
        return null;
    }

    public static class Message implements IMessage {
        public NBTTagCompound tag;

        public Message() {}

        public Message(UUID uuid, VillagerResource resource) {
            this(uuid);
            this.tag.setString("Career", resource.getCareerName());
            this.tag.setInteger("CareerLevel", resource.getLevel());
        }

        // Used by client to ask for villager data.
        public Message(UUID uuid) {
            this.tag = new NBTTagCompound();
            this.tag.setString("UUID", uuid.toString());
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.tag = ByteBufUtils.readTag(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeTag(buf, tag);
        }
    }
}
