package com.science.gtnl.api.mixinHelper;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.AENetworkProxy;

public interface IOutputME {

    default IItemList<IAEItemStack> getItemCache() {
        return null;
    }

    default IItemList<IAEFluidStack> getFluidCache() {
        return null;
    }

    long getLastOutputTick();

    void setLastOutputTick(long value);

    long getLastInputTick();

    void setLastInputTick(long value);

    long getTickCounter();

    void setTickCounter(long value);

    boolean isAdditionalConnection();

    void setAdditionalConnection(boolean value);

    EntityPlayer getLastClickedPlayer();

    void setLastClickedPlayer(EntityPlayer player);

    default List<ItemStack> getLockedItems() {
        return null;
    }

    default void setLockedItems(List<ItemStack> items) {};

    default List<String> getLockedFluids() {
        return null;
    }

    default void setLockedFluids(List<String> items) {}

    default AENetworkProxy getGridProxy() {
        return null;
    }

    default void setGridProxy(AENetworkProxy proxy) {}

    default void gtnl$updateValidGridProxySides() {}

    default void gtnl$checkFluidLock() {}

    default void gtnl$flushCachedStack() {}
}
