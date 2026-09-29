package io.github.haykam821.clutchpractice;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class TrackedBlockStateProvider {
	private final BlockStateProvider delegate;
	private final Set<BlockState> states = new HashSet<>();

	public TrackedBlockStateProvider(BlockStateProvider delegate) {
		this.delegate = delegate;
	}

	public BlockState get(LevelAccessor level, RandomSource random, BlockPos pos) {
		BlockState state = this.delegate.getState(level, random, pos);
		this.states.add(state);

		return state;
	}

	public Set<BlockState> getStates() {
		return this.states;
	}
}
