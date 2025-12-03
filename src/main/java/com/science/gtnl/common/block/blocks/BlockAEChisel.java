package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockAEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntityAEChisel;
import com.science.gtnl.common.packet.AEChiselSyncParallel;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.block.AEBaseTileBlock;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockAEChisel extends AEBaseTileBlock {

    public BlockAEChisel() {
        super(Material.iron);
        setHardness(50.0F);
        setResistance(2000.0F);
        setBlockName("AEChisel");
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockAEChisel.class, getUnlocalizedName());
        GTNLItemList.AEChisel.set(new ItemStack(this, 1));
        setTileEntity(TileEntityAEChisel.class);
        setBlockTextureName(RESOURCE_ROOT_ID + ":AEChisel");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityAEChisel te) {
            if (!super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) {
                if (player instanceof EntityPlayerMP p) {
                    if (te.getParallel() != 1) {
                        ScienceNotLeisure.network.sendTo(new AEChiselSyncParallel(te), p);
                    }
                    p.openGui(ScienceNotLeisure.instance, GuiType.AEChiselGUI.getID(), world, x, y, z);
                }
            }
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, Block a, int b) {
        w.removeTileEntity(x, y, z);
    }

}
