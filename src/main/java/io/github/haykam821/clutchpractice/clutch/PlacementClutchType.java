package io.github.haykam821.clutchpractice.clutch;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import io.github.haykam821.clutchpractice.game.map.ClutchPracticeMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;

public class PlacementClutchType extends ClutchType {
	private final ItemStack stack;

	protected PlacementClutchType(ItemConvertible item) {
		super(new ItemStack(item));

		this.stack = new ItemStack(item);
	}

	@Override
	protected Text createName() {
		return this.stack.getName();
	}

	@Override
	public void addItems(Consumer<ItemStack> adder, Set<BlockState> floor, Set<BlockState> base, RegistryWrapper.WrapperLookup registries) {
		RegistryEntryLookup<Block> blocks = registries.getOrThrow(RegistryKeys.BLOCK);

		ItemStack stack = this.stack.copy();

		List<BlockPredicate> predicates = base.stream()
			.map(state -> {
				BlockPredicate.Builder builder = BlockPredicate.Builder.create();
				builder.blocks(blocks, state.getBlock());

				StatePredicate.Builder stateBuilder = StatePredicate.Builder.create();

				for (Property<?> property : state.getProperties()) {
					stateBuilder.exactMatch(property, state.get(property).toString());
				}

				builder.state(stateBuilder);

				return builder.build();
			})
			.toList();

		BlockPredicatesChecker checker = new BlockPredicatesChecker(predicates);
		stack.set(DataComponentTypes.CAN_PLACE_ON, checker);

		adder.accept(stack);
	}

	@Override
	public void clearArea(ServerWorld world, ClutchPracticeMap map, TrackedBlockStateProvider floor, TrackedBlockStateProvider base) {
		map.clearArea(world, floor);
		map.placeRandomBase(world, base, 0);
	}
}
