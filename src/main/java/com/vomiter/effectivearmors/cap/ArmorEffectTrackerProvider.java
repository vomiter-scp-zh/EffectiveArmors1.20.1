package com.vomiter.effectivearmors.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorEffectTrackerProvider implements ICapabilityProvider, net.minecraftforge.common.util.INBTSerializable<CompoundTag> {
    public static final Capability<IArmorEffectTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private final ArmorEffectTrackerImpl impl = new ArmorEffectTrackerImpl();
    private final LazyOptional<IArmorEffectTracker> opt = LazyOptional.of(() -> impl);

    @Override public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, opt);
    }

    @Override public CompoundTag serializeNBT() { return impl.serializeNBT(); }

    @Override public void deserializeNBT(CompoundTag nbt) { impl.deserializeNBT(nbt); }
}
