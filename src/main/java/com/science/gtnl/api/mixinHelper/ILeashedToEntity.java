package com.science.gtnl.api.mixinHelper;

import net.minecraft.entity.Entity;

public interface ILeashedToEntity {

    void gtnl$setLeashedToEntity(Entity entityIn, boolean sendAttachNotification);

    boolean gtnl$getLeashed();

    Entity gtnl$getLeashedToEntity();

    void gtnl$updateLeashedState();

    void gtnl$clearLeashed(boolean p_110160_1_, boolean p_110160_2_);
}
