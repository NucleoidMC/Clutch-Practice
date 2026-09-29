package io.github.haykam821.clutchpractice.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ClutchPracticeMapConfig {
	public static final Codec<ClutchPracticeMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(ClutchPracticeMapConfig::getId),
			BlockStateProvider.DIRECT_CODEC.optionalFieldOf("floor_provider", BlockStateProvider.of(Blocks.END_STONE_BRICKS)).forGetter(ClutchPracticeMapConfig::getFloorProvider),
			BlockStateProvider.DIRECT_CODEC.optionalFieldOf("base_provider", BlockStateProvider.of(Blocks.SMOOTH_STONE)).forGetter(ClutchPracticeMapConfig::getBaseProvider)
		).apply(instance, ClutchPracticeMapConfig::new);
	});

	private final Identifier id;
	private final BlockStateProvider floorProvider;
	private final BlockStateProvider baseProvider;

	public ClutchPracticeMapConfig(Identifier id, BlockStateProvider floorProvider, BlockStateProvider baseProvider) {
		this.id = id;
		this.floorProvider = floorProvider;
		this.baseProvider = baseProvider;
	}

	public Identifier getId() {
		return this.id;
	}

	public BlockStateProvider getFloorProvider() {
		return this.floorProvider;
	}

	public BlockStateProvider getBaseProvider() {
		return this.baseProvider;
	}
}
