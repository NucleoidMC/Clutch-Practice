package io.github.haykam821.clutchpractice.clutch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import io.github.haykam821.clutchpractice.game.map.ClutchPracticeMap;

public class RandomClutchType extends ClutchType {
	protected RandomClutchType() {
		super(Items.BUNDLE.getDefaultInstance());
	}

	@Override
	public ClutchType resolve(RandomSource random) {
		List<ClutchType> types = new ArrayList<>(ClutchTypes.REGISTRY.values());
		types.remove(this);

		return Util.getRandom(types, random);
	}

	@Override
	public void addItems(Consumer<ItemStack> adder, Set<BlockState> floor, Set<BlockState> base, HolderLookup.Provider registries) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearArea(ServerLevel world, ClutchPracticeMap map, TrackedBlockStateProvider floor, TrackedBlockStateProvider base) {
		throw new UnsupportedOperationException();
	}
}
