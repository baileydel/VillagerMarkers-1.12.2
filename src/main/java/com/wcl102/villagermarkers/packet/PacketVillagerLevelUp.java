package com.wcl102.villagermarkers.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

public class PacketVillagerLevelUp implements IMessageHandler<PacketVillagerLevelUp.Message, IMessage> {

    @Override
    public IMessage onMessage(PacketVillagerLevelUp.Message message, MessageContext ctx) {
        // Server Response
        if (ctx.side.isServer()) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            EntityVillager villager = (EntityVillager) player.mcServer.getWorld(player.dimension).getEntityFromUuid(Objects.requireNonNull(UUID.fromString(message.tag.getString("UUID"))));

            if (villager != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketVillagerData().onMessage(new PacketVillagerData.Message(villager.getUniqueID()), ctx), new NetworkRegistry.TargetPoint(villager.dimension, villager.posX, villager.posY, villager.posZ, 128));
            }
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
