package com.vomiter.effectivearmors.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class ArmorEffectTrackerImpl implements IArmorEffectTracker {
    private final Set<ResourceLocation> active = new HashSet<>();

    @Override public Set<ResourceLocation> active() { return active; }

    @Override public void setActive(Set<ResourceLocation> ids) {
        active.clear();
        active.addAll(ids);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ResourceLocation rl : active) {
            CompoundTag t = new CompoundTag();
            t.putString("id", rl.toString());
            list.add(t);
        }
        tag.put("list", list);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        active.clear();
        ListTag list = tag.getList("list", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            if (t instanceof CompoundTag c) {
                String s = c.getString("id");
                if (!s.isEmpty()) active.add(new ResourceLocation(s));
            }
        }
    }
}
