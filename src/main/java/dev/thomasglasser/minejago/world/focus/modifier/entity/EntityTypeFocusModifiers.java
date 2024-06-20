package dev.thomasglasser.minejago.world.focus.modifier.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.thomasglasser.minejago.Minejago;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EntityTypeFocusModifiers
{
	private static final List<EntityTypeFocusModifier> ENTITY_TYPE_FOCUS_MODIFIERS = new ArrayList<>();

	private EntityTypeFocusModifiers() {
		throw new UnsupportedOperationException("EntityTypeFocusModifiers only contains static definitions.");
	}

	public static void load(ResourceManager resourceManager) {
		ENTITY_TYPE_FOCUS_MODIFIERS.clear();
		resourceManager.listResources(Minejago.MOD_ID + "/focus_modifiers/entity_type", (path) -> path.getPath().endsWith(".json"))
				.forEach(EntityTypeFocusModifiers::load);
	}

	private static void load(ResourceLocation resourceId, Resource resource) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(resourceId.getNamespace(), resourceId.getPath().replace(".json", ""));

		try {
			InputStreamReader reader = new InputStreamReader(resource.open());

			try {
				JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
				EntityTypeFocusModifier.fromJson(id, json).ifPresent(ENTITY_TYPE_FOCUS_MODIFIERS::add);
			} catch (Throwable var7) {
				try {
					reader.close();
				} catch (Throwable var6) {
					var7.addSuppressed(var6);
				}

				throw var7;
			}

			reader.close();
		} catch (IllegalStateException | IOException var8) {
			Minejago.LOGGER.warn("Failed to load entity focus modifier \"" + id + "\".");
		}

	}

	public static double applyModifier(Entity entity, double oldValue) {
		List<EntityTypeFocusModifier> data = ENTITY_TYPE_FOCUS_MODIFIERS.stream().filter(modifier -> modifier.getEntityType().equals(entity.getType()) && NbtUtils.compareNbt(modifier.getNbt(), new EntityDataAccessor(entity).getData(), true)).toList();
		if (!data.isEmpty())
		{
			double newValue = oldValue;
			for (EntityTypeFocusModifier modifier : data)
			{
				newValue = modifier.apply(newValue);
			}
			return newValue;
		}
		return oldValue;
	}
}