package io.github.haykam821.clutchpractice.clutch;

import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import io.github.haykam821.clutchpractice.game.map.ClutchPracticeMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ItemLike;

public class WallPlacementClutchType extends PlacementClutchType {
	protected WallPlacementClutchType(ItemLike item) {
		super(item);
	}

	@Override
	public void clearArea(ServerLevel world, ClutchPracticeMap map, TrackedBlockStateProvider floor, TrackedBlockStateProvider base) {
		map.clearArea(world, floor);
		map.placeRandomBase(world, base, 1, 2);
	}
}
