package dev.thomasglasser.minejago.world.entity;

import com.google.common.util.concurrent.AtomicDouble;
import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.advancements.MinejagoCriteriaTriggers;
import dev.thomasglasser.minejago.client.MinejagoKeyMappings;
import dev.thomasglasser.minejago.core.registries.MinejagoRegistries;
import dev.thomasglasser.minejago.network.ClientboundRefreshVipDataPayload;
import dev.thomasglasser.minejago.network.ClientboundStartMeditationPayload;
import dev.thomasglasser.minejago.network.ClientboundStartMegaMeditationPayload;
import dev.thomasglasser.minejago.network.ClientboundStopAnimationPayload;
import dev.thomasglasser.minejago.network.ClientboundStopMeditationPayload;
import dev.thomasglasser.minejago.network.ClientboundStopSpinjitzuPayload;
import dev.thomasglasser.minejago.network.ServerboundStartMeditationPayload;
import dev.thomasglasser.minejago.network.ServerboundStartSpinjitzuPayload;
import dev.thomasglasser.minejago.network.ServerboundStopMeditationPayload;
import dev.thomasglasser.minejago.network.ServerboundStopSpinjitzuPayload;
import dev.thomasglasser.minejago.sounds.MinejagoSoundEvents;
import dev.thomasglasser.minejago.world.attachment.MinejagoAttachmentTypes;
import dev.thomasglasser.minejago.world.entity.character.Character;
import dev.thomasglasser.minejago.world.entity.character.Cole;
import dev.thomasglasser.minejago.world.entity.character.Zane;
import dev.thomasglasser.minejago.world.entity.power.Power;
import dev.thomasglasser.minejago.world.entity.skulkin.raid.SkulkinRaid;
import dev.thomasglasser.minejago.world.entity.skulkin.raid.SkulkinRaidsHolder;
import dev.thomasglasser.minejago.world.focus.FocusConstants;
import dev.thomasglasser.minejago.world.focus.FocusData;
import dev.thomasglasser.minejago.world.focus.modifier.blockstate.BlockStateFocusModifiers;
import dev.thomasglasser.minejago.world.focus.modifier.entity.EntityTypeFocusModifiers;
import dev.thomasglasser.minejago.world.focus.modifier.itemstack.ItemStackFocusModifiers;
import dev.thomasglasser.minejago.world.focus.modifier.resourcekey.ResourceKeyFocusModifiers;
import dev.thomasglasser.minejago.world.focus.modifier.world.Weather;
import dev.thomasglasser.minejago.world.focus.modifier.world.WorldFocusModifiers;
import dev.thomasglasser.minejago.world.item.GoldenWeaponItem;
import dev.thomasglasser.minejago.world.item.MinejagoItems;
import dev.thomasglasser.minejago.world.level.MinejagoLevelUtils;
import dev.thomasglasser.minejago.world.level.block.TeapotBlock;
import dev.thomasglasser.minejago.world.level.gameevent.MinejagoGameEvents;
import dev.thomasglasser.minejago.world.level.storage.SpinjitzuData;
import dev.thomasglasser.tommylib.api.platform.TommyLibServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MinejagoEntityEvents
{
    public static final Predicate<LivingEntity> NO_SPINJITZU = (player ->
            player.isCrouching() ||
                    player.getVehicle() != null ||
                    player.isVisuallySwimming() ||
                    player.isUnderWater() ||
                    player.isSleeping() ||
                    player.isFreezing() ||
                    player.isNoGravity() ||
                    player.isInLava() ||
                    player.isFallFlying() ||
                    player.isBlocking() ||
                    player.getActiveEffects().stream().anyMatch((mobEffectInstance -> mobEffectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)) ||
                    player.isInWater() ||
                    TommyLibServices.ENTITY.getPersistentData(player).getInt("OffGroundTicks") > 30 ||
                    player.getData(MinejagoAttachmentTypes.FOCUS).getFocusLevel() < FocusConstants.SPINJITZU_LEVEL);

    public static final Predicate<LivingEntity> NO_MEDITATION = (player ->
            player.isCrouching() ||
            player.getVehicle() != null ||
            player.isVisuallySwimming() ||
            player.isSleeping() ||
            player.isNoGravity() ||
            player.isInLava() ||
            player.isFallFlying() ||
            !player.blockPosition().toString().equals(TommyLibServices.ENTITY.getPersistentData(player).getString("StartPos")));

    public static void onPlayerTick(PlayerTickEvent.Post event)
    {
        Player player = event.getEntity();
        if (player.level().tickRateManager().runsNormally())
        {
            FocusData focusData = player.getData(MinejagoAttachmentTypes.FOCUS);
            focusData.tick(player);

            SpinjitzuData spinjitzu = player.getData(MinejagoAttachmentTypes.SPINJITZU);
            CompoundTag persistentData = TommyLibServices.ENTITY.getPersistentData(player);
            int waitTicks = persistentData.getInt("WaitTicks");
            if (player instanceof ServerPlayer serverPlayer)
            {
                ServerLevel level = serverPlayer.serverLevel();
                if ((level.getDifficulty() == Difficulty.PEACEFUL || serverPlayer.getAbilities().instabuild) && focusData.needsFocus())
                {
                    focusData.setFocusLevel(FocusConstants.MAX_FOCUS);
                }

                int j = Mth.clamp(serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                if (j >= 24000 && !serverPlayer.getAbilities().instabuild)
                {
                    focusData.addExhaustion(FocusConstants.EXHAUSTION_INSOMNIA);
                }

                if (!player.onGround())
                {
                    persistentData.putInt("OffGroundTicks", persistentData.getInt("OffGroundTicks") + 1);
                    TommyLibServices.ENTITY.setPersistentData(player, persistentData, false);
                }
                else if (persistentData.getInt("OffGroundTicks") > 0)
                {
                    persistentData.putInt("OffGroundTicks", 0);
                    TommyLibServices.ENTITY.setPersistentData(player, persistentData, false);
                }

                if (spinjitzu.unlocked())
                {
                    if (spinjitzu.active())
                    {
                        if (focusData.isMeditating())
                        {
                            focusData.stopMeditating();
                            TommyLibServices.NETWORK.sendToAllClients(new ClientboundStopAnimationPayload(serverPlayer.getUUID()), serverPlayer.getServer());
                        }

                        if (NO_SPINJITZU.test(serverPlayer))
                        {
                            stopSpinjitzu(spinjitzu, serverPlayer, !serverPlayer.isCrouching());
                            return;
                        }
                        MinejagoCriteriaTriggers.DID_SPINJITZU.get().trigger(serverPlayer);
                        focusData.addExhaustion(FocusConstants.EXHAUSTION_SPINJITZU);
                        if (player.tickCount % 20 == 0)
                        {
                            level.playSound(null, serverPlayer.blockPosition(), MinejagoSoundEvents.SPINJITZU_ACTIVE.get(), SoundSource.PLAYERS);
                            level.gameEvent(serverPlayer, MinejagoGameEvents.SPINJITZU, serverPlayer.blockPosition());
                        }
                        Power power = player.level().registryAccess().registryOrThrow(MinejagoRegistries.POWER).getHolderOrThrow(player.getData(MinejagoAttachmentTypes.POWER).power()).value();
                        // TODO: Spinjitzu colors
                        /*if (!power.is(MinejagoPowers.NONE))
                        {
                            MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, power.getMainSpinjitzuColor(), power.getAltSpinjitzuColor(), 10.5, false);
                            if (power.getBorderParticle() != null)
                            {
                                MinejagoParticleUtils.renderNormalSpinjitzuBorder(power.getBorderParticle(), serverPlayer, 4, false);
                            }
                        }
                        else if (serverPlayer.getTeam() != null)
                        {
                            switch (serverPlayer.getTeam().getColor())
                            {
                                case RED ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_RED, SpinjitzuParticleOptions.TEAM_RED, 10.5, false);
                                case AQUA ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_AQUA, SpinjitzuParticleOptions.TEAM_AQUA, 10.5, false);
                                case BLUE ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_BLUE, SpinjitzuParticleOptions.TEAM_BLUE, 10.5, false);
                                case GOLD ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GOLD, SpinjitzuParticleOptions.TEAM_GOLD, 10.5, false);
                                case GRAY ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GRAY, SpinjitzuParticleOptions.TEAM_GRAY, 10.5, false);
                                case BLACK ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_BLACK, SpinjitzuParticleOptions.TEAM_BLACK, 10.5, false);
                                case GREEN ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GREEN, SpinjitzuParticleOptions.TEAM_GREEN, 10.5, false);
                                case WHITE ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_WHITE, SpinjitzuParticleOptions.TEAM_WHITE, 10.5, false);
                                case YELLOW ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_YELLOW, SpinjitzuParticleOptions.TEAM_YELLOW, 10.5, false);
                                case DARK_RED ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_RED, SpinjitzuParticleOptions.TEAM_DARK_RED, 10.5, false);
                                case DARK_AQUA ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_AQUA, SpinjitzuParticleOptions.TEAM_DARK_AQUA, 10.5, false);
                                case DARK_BLUE ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_BLUE, SpinjitzuParticleOptions.TEAM_DARK_BLUE, 10.5, false);
                                case DARK_GRAY ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_GRAY, SpinjitzuParticleOptions.TEAM_DARK_GRAY, 10.5, false);
                                case DARK_GREEN ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_GREEN, SpinjitzuParticleOptions.TEAM_DARK_GREEN, 10.5, false);
                                case DARK_PURPLE ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_PURPLE, SpinjitzuParticleOptions.TEAM_DARK_PURPLE, 10.5, false);
                                case LIGHT_PURPLE ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_LIGHT_PURPLE, SpinjitzuParticleOptions.TEAM_LIGHT_PURPLE, 10.5, false);
                                default ->
                                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.DEFAULT, SpinjitzuParticleOptions.DEFAULT, 10.5, false);
                            }
                        }
                        else
                        {
                            MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.DEFAULT, SpinjitzuParticleOptions.DEFAULT, 10.5, false);
                        }*/
                    }
                }
                else if (spinjitzu.active())
                {
                    stopSpinjitzu(spinjitzu, serverPlayer, true);
                }

                if (focusData.isMeditating())
                {
                    if (NO_MEDITATION.test(serverPlayer))
                    {
                        focusData.stopMeditating();
                        TommyLibServices.NETWORK.sendToAllClients(new ClientboundStopMeditationPayload(serverPlayer.getUUID(), true), serverPlayer.getServer());
                    }

                    if ((focusData.isMegaMeditating() && player.tickCount % 200 == 0) || (focusData.isNormalMeditating() && player.tickCount % 60 == 0))
                    {
                        BlockPos playerPos = player.blockPosition();
                        Biome biome = level.getBiome(playerPos).value();
                        AtomicDouble i = new AtomicDouble(1);
                        Weather weather = Weather.CLEAR;
                        Biome.Precipitation precipitation = biome.getPrecipitationAt(playerPos);
                        if (level.isThundering())
                        {
                            if (precipitation == Biome.Precipitation.SNOW)
                            {
                                weather = Weather.THUNDER_SNOW;
                            }
                            else if (precipitation == Biome.Precipitation.RAIN) weather = Weather.THUNDER_RAIN;
                        }
                        else if (level.isRaining())
                        {
                            if (precipitation == Biome.Precipitation.SNOW)
                            {
                                weather = Weather.SNOW;
                            }
                            else if (precipitation == Biome.Precipitation.RAIN) weather = Weather.RAIN;
                        }
                        i.set(WorldFocusModifiers.applyModifier((int) level.getDayTime(), weather, playerPos.getY(), TeapotBlock.getBiomeTemperature(level, playerPos), i.get()));
                        i.set(ResourceKeyFocusModifiers.applyModifier(level.getBiome(playerPos).unwrapKey().orElseThrow(), i.get()));
                        i.set(ResourceKeyFocusModifiers.applyModifier(level.dimension(), i.get()));
                        serverPlayer.serverLevel().structureManager().getAllStructuresAt(playerPos).keySet().forEach(structure -> i.set(ResourceKeyFocusModifiers.applyModifier(level.registryAccess().registryOrThrow(Registries.STRUCTURE).getResourceKey(structure).orElseThrow(), i.get())));
                        Stream<BlockState> blocks = level.getBlockStates(player.getBoundingBox().inflate(2));
                        blocks.forEach(blockState -> i.set(BlockStateFocusModifiers.applyModifier(blockState, i.get())));
                        serverPlayer.getActiveEffects().forEach(mobEffectInstance -> i.set(ResourceKeyFocusModifiers.applyModifier(mobEffectInstance.getEffect().unwrapKey().orElseThrow(), i.get()) * (mobEffectInstance.getAmplifier() + 1)));
                        List<Entity> entities = level.getEntities(serverPlayer, serverPlayer.getBoundingBox().inflate(2));
                        entities.forEach(entity ->
                        {
                            if (entity instanceof ItemEntity item)
                            {
                                i.set(ItemStackFocusModifiers.applyModifier(item.getItem(), i.get()));
                            }
                            else if (entity instanceof ItemFrame itemFrame)
                            {
                                i.set(ItemStackFocusModifiers.applyModifier(itemFrame.getItem(), i.get()));
                            }
                            else
                            {
                                i.set(EntityTypeFocusModifiers.applyModifier(entity, i.get()));
                            }
                        });
                        focusData.meditate(focusData.isMegaMeditating(), (int) i.getAndSet(0), 0.1f);
                    }

                    if (focusData.canMegaMeditate(serverPlayer))
                    {
                        if (!focusData.isMegaMeditating())
                        {
                            focusData.startMegaMeditating();
                            TommyLibServices.NETWORK.sendToAllClients(new ClientboundStartMegaMeditationPayload(player.getUUID()), level.getServer());
                        }
                    }
                    else if (focusData.isMegaMeditating())
                    {
                        focusData.startMeditating();
                        TommyLibServices.NETWORK.sendToAllClients(new ClientboundStartMeditationPayload(player.getUUID()), level.getServer());
                    }
                }

                if (MinejagoLevelUtils.isGoldenWeaponsMapHolderNearby(serverPlayer, SkulkinRaid.RADIUS_BUFFER)) {
                    ((SkulkinRaidsHolder)level).getSkulkinRaids().createOrExtendSkulkinRaid(serverPlayer);
                }
            }
            else
            {
                if (waitTicks > 0)
                {
                    persistentData.putInt("WaitTicks", --waitTicks);
                }
                else if (MinejagoKeyMappings.ACTIVATE_SPINJITZU.isDown() && !focusData.isMeditating())
                {
                    if (spinjitzu.active())
                    {
                        TommyLibServices.NETWORK.sendToServer(ServerboundStopSpinjitzuPayload.INSTANCE);
                    }
                    else if (!NO_SPINJITZU.test(player))
                    {
                        TommyLibServices.NETWORK.sendToServer(ServerboundStartSpinjitzuPayload.INSTANCE);
                    }
                    persistentData.putInt("WaitTicks", 10);
                }
                else if (MinejagoKeyMappings.MEDITATE.isDown() && !spinjitzu.active())
                {
                    if (focusData.isMeditating())
                    {
                        focusData.stopMeditating();
                        TommyLibServices.NETWORK.sendToServer(new ServerboundStopMeditationPayload(false));
                    }
                    else if (!NO_MEDITATION.test(player))
                    {
                        focusData.startMeditating();
                        TommyLibServices.NETWORK.sendToServer(ServerboundStartMeditationPayload.INSTANCE);
                    }
                    persistentData.putInt("WaitTicks", 10);
                }
                else if (player.isShiftKeyDown())
                {
                    if (spinjitzu.active())
                    {
                        TommyLibServices.NETWORK.sendToServer(ServerboundStopSpinjitzuPayload.INSTANCE);
                    }
                    if (focusData.isMeditating())
                    {
                        focusData.stopMeditating();
                        TommyLibServices.NETWORK.sendToServer(new ServerboundStopMeditationPayload(false));
                    }
                    persistentData.putInt("WaitTicks", 10);
                }
                TommyLibServices.ENTITY.setPersistentData(player, persistentData, false);
            }
        }
    }

    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        Player player = event.getEntity();
        for (ServerPlayer serverPlayer : ((ServerLevel) player.level()).getPlayers(serverPlayer -> true))
        {
            TommyLibServices.NETWORK.sendToAllClients(ClientboundRefreshVipDataPayload.INSTANCE, serverPlayer.getServer());
        }
    }

    public static void onLivingTick(EntityTickEvent.Post event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity)
        {
            if (livingEntity instanceof Player player)
            {
                Inventory i = player.getInventory();

                if (i.contains(MinejagoItems.SCYTHE_OF_QUAKES.get().getDefaultInstance()) /*&& i.contains(MinejagoElements.SHURIKENS_OF_ICE.get().getDefaultInstance()) && i.contains(MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get().getDefaultInstance()) && i.contains(MinejagoElements.SWORD_OF_FIRE.get().getDefaultInstance())*/)
                {
                    GoldenWeaponItem.overload(livingEntity);
                }
            }
            else if (livingEntity instanceof InventoryCarrier carrier)
            {
                boolean f = false, e = false, i = false, l = false;

                for (ItemStack stack : livingEntity.getAllSlots())
                {
                    if (stack.getItem() == MinejagoItems.SCYTHE_OF_QUAKES.get())
                    {
                        e = true;
                    }
                /*else if (stack.getItem() == MinejagoElements.SWORD_OF_FIRE.get())
                {
                    f = true;
                }
                else if (stack.getItem() == MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())
                {
                    l = true;
                }
                else if (stack.getItem() == MinejagoElements.SHURIKENS_OF_ICE.get())
                {
                    i = true;
                }*/
                }

                SimpleContainer inventory = carrier.getInventory();

                if ((inventory.hasAnyOf(Set.of(MinejagoItems.SCYTHE_OF_QUAKES.get())) || e) /*&& (inventory.hasAnyOf(Set.of(MinejagoElements.SWORD_OF_FIRE.get())) || f) && (inventory.hasAnyOf(Set.of(MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())) || l) && (inventory.hasAnyOf(Set.of(MinejagoElements.SHURIKENS_OF_ICE.get())) || i)*/)
                {
                    GoldenWeaponItem.overload(livingEntity);
                }
            }
            else
            {
                boolean f = false, e = false, i = false, l = false;

                for (ItemStack stack : livingEntity.getAllSlots())
                {
                    if (stack.getItem() == MinejagoItems.SCYTHE_OF_QUAKES.get())
                    {
                        e = true;
                    }
                /*else if (stack.getItem() == MinejagoElements.SWORD_OF_FIRE.get())
                {
                    f = true;
                }
                else if (stack.getItem() == MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())
                {
                    l = true;
                }
                else if (stack.getItem() == MinejagoElements.SHURIKENS_OF_ICE.get())
                {
                    i = true;
                }*/
                }

                if (f && e && i && l) GoldenWeaponItem.overload(livingEntity);
            }
        }
    }

    public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        Player player = event.getEntity();
        Level world = event.getLevel();
        InteractionHand hand = event.getHand();
        Entity entity = event.getTarget();
        CompoundTag persistentData = TommyLibServices.ENTITY.getPersistentData(entity);
        if (world instanceof ServerLevel && hand == InteractionHand.MAIN_HAND && entity instanceof Painting painting && painting.getVariant().is(Minejago.modLoc( "four_weapons")) && !persistentData.getBoolean("MapTaken"))
        {
            player.addItem(MinejagoItems.EMPTY_GOLDEN_WEAPONS_MAP.get().getDefaultInstance());
            if (!player.isCreative())
            {
                persistentData.putBoolean("MapTaken", true);
                persistentData.putBoolean("MapTakenByPlayer", true);
                TommyLibServices.ENTITY.setPersistentData(entity, persistentData, true);
            }
        }
    }

    public static void stopSpinjitzu(SpinjitzuData spinjitzu, ServerPlayer serverPlayer, boolean fail)
    {
        if (spinjitzu.active())
        {
            serverPlayer.setData(MinejagoAttachmentTypes.SPINJITZU, new SpinjitzuData(spinjitzu.unlocked(), false));
            TommyLibServices.NETWORK.sendToAllClients(new ClientboundStopSpinjitzuPayload(serverPlayer.getUUID(), fail), serverPlayer.getServer());
            AttributeInstance speed = serverPlayer.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && speed.hasModifier(SpinjitzuData.SPEED_MODIFIER))
                speed.removeModifier(SpinjitzuData.SPEED_MODIFIER);
            AttributeInstance kb = serverPlayer.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (kb != null && kb.hasModifier(SpinjitzuData.KNOCKBACK_MODIFIER))
                kb.removeModifier(SpinjitzuData.KNOCKBACK_MODIFIER);
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), MinejagoSoundEvents.SPINJITZU_STOP.get(), SoundSource.PLAYERS);
        }
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        for (EntityType<? extends LivingEntity> type : MinejagoEntityTypes.getAllAttributes().keySet())
        {
            event.put(type, MinejagoEntityTypes.getAllAttributes().get(type));
        }
    }

    public static void onSpawnPlacementsRegister(SpawnPlacementRegisterEvent event)
    {
        event.register(MinejagoEntityTypes.ZANE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.OCEAN_FLOOR_WG, Zane::checkZaneSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(MinejagoEntityTypes.COLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ((entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource) -> Character.checkCharacterSpawnRules(Cole.class, entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource)), SpawnPlacementRegisterEvent.Operation.AND);
    }

    public static void onLivingKnockBack(LivingKnockBackEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp && event.getStrength() > 2)
        {
            MinejagoEntityEvents.stopSpinjitzu(sp.getData(MinejagoAttachmentTypes.SPINJITZU), sp, true);
        }
    }
}