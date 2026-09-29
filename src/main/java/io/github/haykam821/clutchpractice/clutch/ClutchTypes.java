package io.github.haykam821.clutchpractice.clutch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import io.github.haykam821.clutchpractice.Main;
import xyz.nucleoid.plasmid.api.util.TinyRegistry;

public final class ClutchTypes {
	public static final TinyRegistry<ClutchType> REGISTRY = TinyRegistry.create();

	public static final ClutchType RANDOM = register("random", new RandomClutchType());
	public static final ClutchType LADDER = register("ladder", new WallPlacementClutchType(Blocks.LADDER));
	public static final ClutchType SLIME_BLOCK = register("slime_block", new PlacementClutchType(Blocks.SLIME_BLOCK));
	public static final ClutchType WATER_BUCKET = register("water_bucket", new PlacementClutchType(Items.WATER_BUCKET));

	private ClutchTypes() {
		return;
	}

	public static ClutchType getNext(ClutchType type) {
		List<ClutchType> types = new ArrayList<>(REGISTRY.values());

		int index = types.indexOf(type);
		return types.get((index + 1) % types.size());
	}

	private static ClutchType register(String path, ClutchType type) {
		Identifier id = Main.identifier(path);
		REGISTRY.register(id, type);

		return type;
	}
}
