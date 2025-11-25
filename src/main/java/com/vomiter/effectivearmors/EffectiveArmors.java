package com.vomiter.effectivearmors;

import com.mojang.logging.LogUtils;
import com.vomiter.effectivearmors.cap.ArmorEffectTrackerProvider;
import com.vomiter.effectivearmors.data.ArmorEffectManager;
import com.vomiter.effectivearmors.data.ModItemTagProvider;
import com.vomiter.effectivearmors.event.ForgeEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EffectiveArmors.MOD_ID)
public class EffectiveArmors {
    public static final String MOD_ID = "effectivearmors";
    public static final Logger LOGGER = LogUtils.getLogger();


    public EffectiveArmors() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ModItemTagProvider::onGatherData);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onAddReloadListeners);
        forgeBus.addGenericListener(Entity.class, this::onAttachCaps);

        forgeBus.register(new ForgeEventHandler());
    }

    private void onAddReloadListeners(AddReloadListenerEvent e) {
        e.addListener(new ArmorEffectManager());
    }

    private void onAttachCaps(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof net.minecraft.world.entity.player.Player) {
            e.addCapability(new ResourceLocation(MOD_ID, "armor_effect_tracker"), new ArmorEffectTrackerProvider());
        }
    }
}
