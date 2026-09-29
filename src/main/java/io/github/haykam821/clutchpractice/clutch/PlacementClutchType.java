package io.github.haykam821.clutchpractice.clutch;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import io.github.haykam821.clutchpractice.game.map.ClutchPracticeMap;

public class PlacementClutchType extends ClutchType {
	private final ItemStack stack;

	protected PlacementClutchType(ItemLike item) {
		super(new ItemStack(item));

		this.stack = new ItemStack(item);
	}

	@Override
	protected Component createName() {
		return this.stack.getHoverName();
	}

	@Override
	public void addItems(Consumer<ItemStack> adder, Set<BlockState> floor, Set<BlockState> base, HolderLookup.Provider registries) {
		HolderGetter<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);

		ItemStack stack = this.stack.copy();

		List<BlockPredicate> predicates = base.stream()
			.map(state -> {
				BlockPredicate.Builder builder = BlockPredicate.Builder.block();
				builder.of(blocks, state.getBlock());

				StatePropertiesPredicate.Builder stateBuilder = StatePropertiesPredicate.Builder.properties();

				for (Property<?> property : state.getProperties()) {
					stateBuilder.hasProperty(property, state.getValue(property).toString());
				}

				builder.setProperties(stateBuilder);

				return builder.build();
			})
			.toList();

		AdventureModePredicate component = new AdventureModePredicate(predicates);
		stack.set(DataComponents.CAN_PLACE_ON, component);

		adder.accept(stack);
	}

	@Override
	public void clearArea(ServerLevel world, ClutchPracticeMap map, TrackedBlockStateProvider floor, TrackedBlockStateProvider base) {
		map.clearArea(world, floor);
		map.placeRandomBase(world, base, 0);
	}
}
