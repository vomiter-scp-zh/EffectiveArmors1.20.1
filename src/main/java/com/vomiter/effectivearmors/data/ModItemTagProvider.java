package com.vomiter.effectivearmors.data;

import com.vomiter.effectivearmors.EffectiveArmors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    private TagKey<Item> create(String path){
        return TagKey.create(Registries.ITEM, new ResourceLocation(EffectiveArmors.MOD_ID, path));
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider p_256380_) {
        tag(create("test_helmets")).add(Items.GOLDEN_HELMET, Items.DIAMOND_HELMET);
    }

    public static void onGatherData(GatherDataEvent event){
        CompletableFuture<TagsProvider.TagLookup<Block>> emptyBlockLookup =
                CompletableFuture.completedFuture(new TagsProvider.TagLookup<Block>() {
                    @Override
                    public Optional<TagBuilder> apply(TagKey<Block> blockTagKey) {
                        return Optional.empty();
                    }
                });

        event.getGenerator().addProvider(event.includeServer(),
                new ModItemTagProvider(
                        event.getGenerator().getPackOutput(),
                        event.getLookupProvider(),
                        emptyBlockLookup,
                        EffectiveArmors.MOD_ID,
                        event.getExistingFileHelper()
                )
        );
    }
}
