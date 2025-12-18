package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.enums.GTValues.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.dreammaster.gthandler.CustomItemList;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.mixins.late.EnhancedLootBags.AccessorItemLootBag;
import com.science.gtnl.utils.enums.BlockIcons;
import com.science.gtnl.utils.item.ItemUtils;

import eu.usrv.enhancedlootbags.core.LootGroupsHandler;
import eu.usrv.enhancedlootbags.core.items.ItemLootBag;
import eu.usrv.enhancedlootbags.core.serializer.LootGroups;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

public class LootBagRedemption extends MTEBasicMachine {

    public static final Random random = new Random();

    public LootBagRedemption(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { StatCollector.translateToLocal("Tooltip_LootBagRedemption_00"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_01"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_02"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_03"),
                StatCollector.translateToLocal("Tooltip_LootBagRedemption_04") },
            2,
            9,
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_SIDE_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_SIDE_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_FRONT_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_TOP_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_TOP_REPLICATOR_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW)
                    .glow()
                    .build()),
            TextureFactory.of(
                TextureFactory.of(BlockIcons.OVERLAY_BOTTOM_REPLICATOR),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_BOTTOM_REPLICATOR_GLOW)
                    .glow()
                    .build()));
    }

    public LootBagRedemption(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures, 2, 9);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LootBagRedemption(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public int checkRecipe() {
        ItemStack slotA = getInputAt(0);
        ItemStack slotB = getInputAt(1);
        if (slotA == null && slotB == null) return 0;

        ItemStack lootBagStack = null;
        ItemStack coinStack = null;

        if (slotA != null && slotA.getItem() instanceof ItemLootBag) {
            lootBagStack = slotA;
        } else if (slotB != null && slotB.getItem() instanceof ItemLootBag) {
            lootBagStack = slotB;
        }

        if (slotA != null && GTUtility.areStacksEqual(slotA, CustomItemList.CoinTechnician.get(1))) {
            coinStack = slotA;
        } else if (slotB != null && GTUtility.areStacksEqual(slotB, CustomItemList.CoinTechnician.get(1))) {
            coinStack = slotB;
        }

        if (lootBagStack == null || coinStack == null) return 0;

        ItemLootBag lootBag = (ItemLootBag) lootBagStack.getItem();
        LootGroupsHandler lootGroupsHandler = ((AccessorItemLootBag) lootBag).getLGHandler();
        if (lootGroupsHandler == null) return 0;

        ItemStack specialItem = getSpecialSlot();
        int groupID = lootBagStack.getItemDamage();

        LootGroups.LootGroup tGrp = lootGroupsHandler.getMergedGroupFromID(groupID, 3);
        if (tGrp == null) return 0;

        List<ItemStack> resultItems = new ArrayList<>();
        boolean specialItemFound = false;

        if (specialItem != null) {
            for (LootGroups.LootGroup.Drop drop : tGrp.getDrops()) {
                ItemStack dropStack = drop.getItemStack(1);
                if (dropStack != null && GTUtility.areStacksEqual(dropStack, specialItem, true)) {
                    specialItemFound = true;
                    int maxAmount = drop.getAmount();
                    ItemStack specialDropStack = drop.getItemStack(maxAmount);
                    if (specialDropStack != null) {
                        while (specialDropStack.stackSize > specialDropStack.getMaxStackSize()) {
                            resultItems.add(specialDropStack.splitStack(specialDropStack.getMaxStackSize()));
                        }
                        resultItems.add(specialDropStack);
                    }
                    break;
                }
            }
        }

        int itemsToDropCount = tGrp.getMinItems();
        if (tGrp.getMaxItems() > tGrp.getMinItems()) {
            itemsToDropCount = random.nextInt(tGrp.getMaxItems() - tGrp.getMinItems() + 1) + tGrp.getMinItems();
        }

        if (specialItemFound) {
            int specialItemCount = 0;
            for (ItemStack stack : resultItems) {
                if (GTUtility.areStacksEqual(stack, specialItem, true)) {
                    specialItemCount += stack.stackSize;
                }
            }
            itemsToDropCount = Math.max(0, itemsToDropCount - specialItemCount);
        }

        while (itemsToDropCount > 0) {
            LootGroups.LootGroup.Drop tSelectedDrop = null;
            double tRnd = random.nextDouble() * tGrp.getMaxWeight();

            for (LootGroups.LootGroup.Drop tDr : tGrp.getDrops()) {
                tRnd -= tDr.getChance();
                if (tRnd <= 0.0D) {
                    tSelectedDrop = tDr;
                    break;
                }
            }

            if (tSelectedDrop != null) {
                List<LootGroups.LootGroup.Drop> tPossibleItemDrops = lootGroupsHandler
                    .getItemGroupDrops(tGrp, tSelectedDrop);
                for (LootGroups.LootGroup.Drop dr : tPossibleItemDrops) {
                    int tAmount = dr.getAmount();
                    if (dr.getIsRandomAmount()) tAmount = random.nextInt(tAmount) + 1;

                    ItemStack tStackAll = dr.getItemStack(tAmount);
                    if (tStackAll != null) {
                        while (tStackAll.stackSize > tStackAll.getMaxStackSize()) {
                            resultItems.add(tStackAll.splitStack(tStackAll.getMaxStackSize()));
                        }
                        resultItems.add(tStackAll);
                        itemsToDropCount -= 1;
                    }
                }
            }

            if (tSelectedDrop == null) {
                break;
            }
        }

        if (!resultItems.isEmpty() && this.canOutput(resultItems.toArray(new ItemStack[0]))) {

            if (coinStack.stackSize < 4) return 0;

            lootBagStack.stackSize -= 1;
            coinStack.stackSize -= 4;

            for (int i = 0; i < resultItems.size() && i < mOutputItems.length; i++) {
                this.mOutputItems[i] = resultItems.get(i);
            }

            this.mMaxProgresstime = 20;
            this.mEUt = (int) TierEU.RECIPE_LV;
            return 2;
        }

        return 0;
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.IC2_MACHINES_MAGNETIZER_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public long maxEUStore() {
        return Math.max(getEUVar(), V[6]);
    }

    @Override
    public boolean hasEnoughEnergyToCheckRecipe() {
        return getBaseMetaTileEntity().getStoredEU() > TierEU.RECIPE_LV;
    }
}
