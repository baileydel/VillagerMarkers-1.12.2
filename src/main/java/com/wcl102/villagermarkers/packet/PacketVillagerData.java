package com.wcl102.villagermarkers.packet;

import com.wcl102.villagermarkers.render.Markers;
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

//TODO come back to this class and get rid of redundancy
public class PacketVillagerData implements IMessageHandler<PacketVillagerData.Message, IMessage> {

    @Override
    public IMessage onMessage(PacketVillagerData.Message message, MessageContext ctx) {
        if (!ctx.side.isClient()) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            EntityVillager villager = (EntityVillager) player.mcServer.getWorld(player.dimension).getEntityFromUuid(Objects.requireNonNull(UUID.fromString(message.tag.getString("UUID"))));

            if (villager != null) {
                NBTTagCompound tag = new NBTTagCompound();

                villager.writeToNBT(tag);

                String profession = tag.getString("ProfessionName");
                int career = tag.getInteger("Career") - 1;

                String careerName = villager.getProfessionForge().getCareer(career).getName();

                int careerLevel = tag.getInteger("CareerLevel");

                return new Message(villager.getUniqueID(), profession, careerName, career, careerLevel);
            }
        }
        else {
            Markers.villagers.put(UUID.fromString(message.tag.getString("UUID")), message);
        }
        return null;
    }

    public static class Message implements IMessage {

        private NBTTagCompound tag;

        private String profession;
        private String careerName;
        private int career;
        private int careerlevel;

        public Message() {}

        public Message(UUID uuid) {
            this.tag = new NBTTagCompound();
            this.tag.setString("UUID", uuid.toString());
        }

        public Message(UUID uuid, String profession, String careerName,  int career, int careerlevel) {
            this(uuid);
            this.tag.setString("Profession", profession);
            this.tag.setString("CareerName", careerName);
            this.tag.setInteger("Career", career);
            this.tag.setInteger("CareerLevel", careerlevel);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.tag = ByteBufUtils.readTag(buf);

            if (this.tag != null) {
                this.profession = this.tag.getString("Profession");
                this.careerName = this.tag.getString("CareerName");
                this.career = this.tag.getInteger("Career");
                this.careerlevel = this.tag.getInteger("CareerLevel");
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeTag(buf, tag);
        }

        public String getCareerName() {
            return careerName;
        }

        public int getCareer() {
            return career;
        }

        public int getCareerlevel() {
            return careerlevel;
        }
    }
}
