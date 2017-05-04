package gunn.modcurrency.mod.core.network;

import gunn.modcurrency.mod.tile.abAdvSell;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-12
 */
public class PacketSetInfiniteToServer implements IMessage {
    //Player changes infinity mode, sends to server
    private BlockPos blockPos;
    private int data;

    public PacketSetInfiniteToServer(){}

    public void setData(int data, BlockPos pos) {
        this.blockPos = pos;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
    }

    public static class Handler implements IMessageHandler<PacketSetInfiniteToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetInfiniteToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetInfiniteToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            ((abAdvSell) world.getTileEntity(message.blockPos)).setField(6, message.data);
        }
    }
}