package com.vomiter.effectivearmors.cap;

import net.minecraft.resources.ResourceLocation;
import java.util.Set;

public interface IArmorEffectTracker {
    Set<ResourceLocation> active();

    void setActive(Set<ResourceLocation> ids);
}
