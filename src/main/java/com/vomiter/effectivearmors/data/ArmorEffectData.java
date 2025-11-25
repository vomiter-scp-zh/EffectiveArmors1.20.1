package com.vomiter.effectivearmors.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ArmorEffectData(
        ResourceLocation id,
        List<Ingredient> armor,
        List<MobEffectInstance> effects,
        boolean matchAll
) {
    public static ArmorEffectData fromJson(ResourceLocation idFallback, JsonObject obj) {
        ResourceLocation id = obj.has("id") ? rl(obj.get("id").getAsString()) : idFallback;

        // armor: List<Ingredient>
        List<Ingredient> armorList = new ArrayList<>();
        JsonArray armorArr = obj.getAsJsonArray("armor");
        for (JsonElement el : armorArr) {
            if(el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()){
                String elString = el.getAsString();
                if(!elString.isEmpty() && Character.isDigit(elString.charAt(0))){
                    Pattern PATTERN = Pattern.compile(
                            "\\s*(\\d+)\\s*[xX]\\s*([#]?[a-z0-9_.-]+:[a-z0-9_/.-]+)\\s*"
                    );
                    Matcher m = PATTERN.matcher(elString);
                    if (m.matches()) {
                        int count = Integer.parseInt(m.group(1));
                        String idOrTag = m.group(2);
                        for (int i = 0; i < count; i++) {
                            armorList.add(parseIngredient(idOrTag));
                        }
                        continue;
                    }
                }
            }
            armorList.add(parseIngredient(el));
        }

        // effects: List<MobEffectInstance>
        List<MobEffectInstance> effects = new ArrayList<>();
        JsonArray effArr = obj.getAsJsonArray("effects");
        for (JsonElement el : effArr) {
            if (!el.isJsonObject()) continue;
            effects.add(parseEffect(el.getAsJsonObject()));
        }

        boolean matchAll = obj.has("match_all") && obj.get("match_all").getAsBoolean();

        return new ArmorEffectData(id, armorList, effects, matchAll);
    }

    private static Ingredient parseIngredient(String s){
        if (s.startsWith("#")) {
            ResourceLocation tagId = rl(s.substring(1));
            TagKey<Item> tag = ItemTags.create(tagId);
            return Ingredient.of(tag);
        } else {
            ResourceLocation itemId = rl(s);
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) throw new IllegalArgumentException("Unknown item id: " + itemId);
            return Ingredient.of(item);
        }
    }

    private static Ingredient parseIngredient(JsonElement el) {

        if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
            String s = el.getAsString();
            return parseIngredient(s);
        }

        if (el.isJsonObject() || el.isJsonArray()) {
            Ingredient ing = CraftingHelper.getIngredient(el, true);
            if (ing == null) throw new IllegalArgumentException("Failed to parse Ingredient: " + el);
            return ing;
        }
        throw new IllegalArgumentException("Invalid ingredient json: " + el);
    }

    private static MobEffectInstance parseEffect(JsonObject obj) {
        ResourceLocation effId = rl(obj.get("id").getAsString());
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effId);
        if (effect == null) throw new IllegalArgumentException("Unknown effect id: " + effId);

        int duration = 400;
        int amplifier = obj.has("amplifier") ? obj.get("amplifier").getAsInt() : 0;
        boolean ambient = obj.has("ambient") && obj.get("ambient").getAsBoolean();
        boolean showParticles = !obj.has("show_particles") || obj.get("show_particles").getAsBoolean();
        boolean showIcon = !obj.has("show_icon") || obj.get("show_icon").getAsBoolean();

        return new MobEffectInstance(effect, duration, amplifier, ambient, showParticles, showIcon);
    }

    private static ResourceLocation rl(String s) {
        return new ResourceLocation(s);
    }

    public boolean matches(Supplier<ItemStack[]> equippedSupplier) {
        ItemStack[] eq = equippedSupplier.get();
        if (eq == null || eq.length == 0) return false;

        List<ItemStack> pool = new ArrayList<>();
        for (ItemStack s : eq) if (!s.isEmpty()) pool.add(s);

        if (matchAll) {
            for (Ingredient ing : armor) {
                boolean any = false;
                for (int i = 0; i < pool.size(); i++) {
                    if (ing.test(pool.get(i))) {
                        any = true; pool.remove(i); break;
                    }

                }
                if (!any) return false;
            }
            return true;
        } else {
            for (Ingredient ing : armor) {
                for (ItemStack s : pool) {
                    if (ing.test(s)) return true;
                }
            }
            return false;
        }
    }
}
