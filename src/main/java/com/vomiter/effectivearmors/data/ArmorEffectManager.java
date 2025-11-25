package com.vomiter.effectivearmors.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vomiter.effectivearmors.EffectiveArmors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArmorEffectManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "effectivearmors/armor_effects";

    private static Map<ResourceLocation, ArmorEffectData> ENTRIES = Map.of();

    public ArmorEffectManager() {
        super(GSON, FOLDER);
    }

    public static Collection<ArmorEffectData> all() { return ENTRIES.values(); }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, ArmorEffectData> temp = new HashMap<>();
        jsons.forEach((rl, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                ArmorEffectData data = ArmorEffectData.fromJson(rl, obj);
                temp.put(data.id(), data);
            } catch (Exception ex) {
                EffectiveArmors.LOGGER.error("Effective Armor Parsing Error at {}", rl);
            }
        });
        ENTRIES = Map.copyOf(temp);
    }

    public static ItemStack[] getEquippedFour(net.minecraft.world.entity.player.Player p) {
        return new ItemStack[]{
                p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD),
                p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST),
                p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS),
                p.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)
        };
    }
}
