package com.vomiter.effectivearmors.event;

import com.vomiter.effectivearmors.cap.ArmorEffectTrackerProvider;
import com.vomiter.effectivearmors.data.ArmorEffectData;
import com.vomiter.effectivearmors.data.ArmorEffectManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class ForgeEventHandler {
    private static final int APPLY_INTERVAL_TICKS = 100;
    private static final int EFFECT_DURATION_TICKS = 200;

    public void applyEffects(Player p){
        p.getCapability(ArmorEffectTrackerProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.active().isEmpty()) return;
            Set<ResourceLocation> stillMatches = new HashSet<>();
            for (ArmorEffectData d : ArmorEffectManager.all()) {
                if (cap.active().contains(d.id()) && d.matches(() -> ArmorEffectManager.getEquippedFour(p))) {
                    stillMatches.add(d.id());
                    for (MobEffectInstance inst : d.effects()) {
                        MobEffectInstance toApply = new MobEffectInstance(
                                inst.getEffect(),
                                EFFECT_DURATION_TICKS,
                                inst.getAmplifier(),
                                inst.isAmbient(),
                                inst.isVisible(),
                                inst.showIcon()
                        );
                        p.addEffect(toApply);
                    }
                }
            }
            cap.setActive(stillMatches);
        });

    }

    @SubscribeEvent
    public void onEquipChanged(LivingEquipmentChangeEvent e) {
        if (!(e.getEntity() instanceof Player p) || p.level().isClientSide) return;

        Set<ResourceLocation> matches = new HashSet<>();
        for (ArmorEffectData d : ArmorEffectManager.all()) {
            if (d.matches(() -> ArmorEffectManager.getEquippedFour(p))) {
                matches.add(d.id());
            }
        }
        p.getCapability(ArmorEffectTrackerProvider.CAPABILITY).ifPresent(cap -> cap.setActive(matches));
        applyEffects(p);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.player.level().isClientSide) return;
        Player p = e.player;

        if (p.tickCount % APPLY_INTERVAL_TICKS != 0) return;
        applyEffects(p);
    }
}
