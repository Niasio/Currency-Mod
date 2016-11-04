package gunn.modcurrency.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class TileVendor extends TileEntity implements ICapabilityProvider, ITickable{

    public static final int MONEY_SLOT_COUNT = 1;
    public static final int VEND_SLOT_COUNT = 30;
    public static final int TOTAL_SLOTS_COUNT = MONEY_SLOT_COUNT + VEND_SLOT_COUNT;

    private int bank;
    private ItemStackHandler itemStackHandler = new ItemStackHandler(TOTAL_SLOTS_COUNT) {
        @Override
        protected void onContentsChanged(int slot) { markDirty(); }
    };

    public TileVendor(){
        bank = 0;
    }

    @Override
    public void update() {
        if(!worldObj.isRemote){
            if(itemStackHandler.getStackInSlot(0) != null){
                int amnt;
                switch(itemStackHandler.getStackInSlot(0).getItemDamage()){
                    case 0:         //One Dollar Bill
                        amnt = 1;
                        break;
                    case 1:         //Five Dollar Bill
                        amnt = 5;
                        break;
                    case 2:         //Ten Dollar Bill
                        amnt = 10;
                        break;
                    case 3:         //Twenty Dollar Bill
                        amnt = 20;
                        break;
                    case 4:         //Fifty Dollar Bill
                        amnt = 50;
                        break;
                    case 5:         //One Hundred Dollar Bill
                        amnt = 100;
                        break;
                    default:
                        amnt = -1;
                        break;
                }
                amnt = amnt * itemStackHandler.getStackInSlot(0).stackSize;
                itemStackHandler.setStackInSlot(0, null);
                bank = bank + amnt;
                markDirty();
            }
        }
    }
    
    
    
    
    
    //<editor-fold desc="Item Handler Methods">
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return (T) itemStackHandler;
        }
        return super.getCapability(capability, facing);
    }
    
    public boolean canInteractWith(EntityPlayer player){
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
    //</editor-fold>
    
    //<editor-fold desc="Packet and Server-to-Client Mumbo Jumbo">
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("items")) itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        if(compound.hasKey("bank")) bank = compound.getInteger("bank");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        compound.setInteger("bank", bank);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("bank", bank);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        bank = getUpdatePacket().getNbtCompound().getInteger("bank");
    }

    public int getFieldCount(){
        return 1;
    }

    public void setField(int id, int value){
        if(id == 0){
            bank = value;
        }
    }

    public int getField(int id){
        if(id == 0){
            return bank;
        }
        return -1;
    }
    //</editor-fold>
}