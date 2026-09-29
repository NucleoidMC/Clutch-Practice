package io.github.haykam821.clutchpractice.clutch;

import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import io.github.haykam821.clutchpractice.game.map.ClutchPracticeMap;

public abstract class ClutchType {
	private final ItemStack icon;
	private Component name;

	public ClutchType(ItemStack icon) {
		this.icon = icon;
	}

	public final ItemStack getIcon() {
		return this.icon;
	}

	protected Component createName() {
		Identifier id = ClutchTypes.REGISTRY.getIdentifier(this);
		return Component.translatable(Util.makeDescriptionId("clutchType", id));
	}

	public final Component getName() {
		if (this.name == null) {
			this.name = this.createName();
		}

		return this.name;
	}

	public ClutchType resolve(RandomSource random) {
		return this;
	}

	public abstract void addItems(Consumer<ItemStack> adder, Set<BlockState> floor, Set<BlockState> base, HolderLookup.Provider registries);

	public abstract void clearArea(ServerLevel world, ClutchPracticeMap map, TrackedBlockStateProvider floor, TrackedBlockStateProvider base);
}
