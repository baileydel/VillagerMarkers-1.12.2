package com.wcl102.villagermarkers.packet;

import com.wcl102.villagermarkers.render.Markers;
import com.wcl102.villagermarkers.resource.VillagerResource;
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
            EntityVillager villager = (EntityVillager) player.mcServer.getWorld(player.dimension).getEntityFromUuid(Objects.requireNonNull(UUID.fromString(message.tag.getString("UUID"))));

            if (villager != null) {
                NBTTagCompound tag = new NBTTagCompound();

                villager.writeToNBT(tag);

                int career = tag.getInteger("Career") - 1;
                int careerLevel = tag.getInteger("CareerLevel");
                String careerName = villager.getProfessionForge().getCareer(career).getName();

                // Sent to Client
                return new Message(villager.getUniqueID(), careerName, career, careerLevel);
            }
        }
        else {
            // Client Response
            Markers.villagers.put(UUID.fromString(message.tag.getString("UUID")), new VillagerResource(message.tag));
        }
        return null;
    }

    public static class Message implements IMessage {
        public NBTTagCompound tag;

        public Message() {}

        public Message(UUID uuid) {
            this.tag = new NBTTagCompound();
            this.tag.setString("UUID", uuid.toString());
        }

        public Message(UUID uuid, String careerName,  int career, int careerlevel) {
            this(uuid);
            this.tag.setString("CareerName", careerName);
            this.tag.setInteger("Career", career);
            this.tag.setInteger("CareerLevel", careerlevel);
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
