package io.github.haykam821.clutchpractice.game.map;

import java.util.Set;

import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.level.generator.TemplateChunkGenerator;

public class ClutchPracticeMap {
	private static final BlockBounds EMPTY_BOUNDS = BlockBounds.ofBlock(BlockPos.ZERO);
	private static final BlockState AIR = Blocks.AIR.defaultBlockState();

	private static final Vec2 DEFAULT_SPAWN_ROTATION = new Vec2(90, 0);

	private final ClutchPracticeMapConfig config;
	private final MapTemplate template;
	private final AABB box;
	private final BlockBounds area;
	private final AABB exit;

	private final BlockBounds clutchSelector;
	private final Vec3 clutchDisplayPos;

	public ClutchPracticeMap(ClutchPracticeMapConfig config, MapTemplate template) {
		this.config = config;
		this.template = template;

		this.box = this.template.getBounds().asBox();

		this.area = ClutchPracticeMap.getBounds(template, "area");
		this.exit = ClutchPracticeMap.getBox(template, "exit");

		this.clutchSelector = ClutchPracticeMap.getBounds(template, "clutch_selector");
		this.clutchDisplayPos = this.clutchSelector == null ? null : this.clutchSelector.center();
	}

	public BlockBounds getArea() {
		return this.area;
	}

	public void clearArea(ServerLevel world, TrackedBlockStateProvider floor) {
		RandomSource random = world.getRandom();
		int minY = this.area.min().getY();

		for (BlockPos pos : this.area) {
			if (pos.getY() == minY) {
				world.setBlockAndUpdate(pos, floor.get(world, random, pos));
			} else {
				world.setBlockAndUpdate(pos, AIR);
			}
		}
	}

	public void placeRandomBase(ServerLevel world, TrackedBlockStateProvider base, int offsetY) {
		this.placeRandomBase(world, base, offsetY, offsetY);
	}

	public void placeRandomBase(ServerLevel world, TrackedBlockStateProvider base, int minOffsetY, int maxOffsetY) {
		RandomSource random = world.getRandom();
		int minY = this.area.min().getY();

		int baseX = Mth.nextInt(random, this.area.min().getX(), this.area.max().getX());
		int baseZ = Mth.nextInt(random, this.area.min().getZ(), this.area.max().getZ());

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(baseX, minY + minOffsetY, baseZ);

		while (pos.getY() <= minY + maxOffsetY) {
			BlockState baseState = base.get(world, random, pos);
			world.setBlockAndUpdate(pos, baseState);

			pos.move(Direction.UP);
		}
	}

	public TrackedBlockStateProvider getTrackedFloorProvider() {
		return new TrackedBlockStateProvider(this.config.getFloorProvider());
	}

	public TrackedBlockStateProvider getTrackedBaseProvider() {
		return new TrackedBlockStateProvider(this.config.getBaseProvider());
	}

	public AABB getExit() {
		return this.exit;
	}

	public BlockBounds getClutchSelector() {
		return this.clutchSelector;
	}

	public Vec3 getClutchDisplayPos() {
		return this.clutchDisplayPos;
	}

	public Vec3 getSpawn() {
		TemplateRegion spawn = this.template.getMetadata().getFirstRegion("spawn");
		if (spawn != null) {
			return spawn.getBounds().centerBottom();
		}

		return new Vec3(0.5, 76, 0.5);
	}

	private Vec2 getSpawnRotation() {
		TemplateRegion spawn = this.template.getMetadata().getFirstRegion("spawn");
		if (spawn != null) {
			return spawn.getData().read("Rotation", Vec2.CODEC).orElse(DEFAULT_SPAWN_ROTATION);
		}

		return DEFAULT_SPAWN_ROTATION;
	}

	public void spawn(ServerPlayer player) {
		Vec3 spawn = this.getSpawn();
		Vec2 rotation = this.getSpawnRotation();

		player.teleportTo(player.level(), spawn.x(), spawn.y(), spawn.z(), Set.of(), rotation.x, rotation.y, true);
	}

	public boolean respawnIfOutOfBounds(ServerPlayer player) {
		if (this.box.contains(player.position())) {
			return false;
		}

		this.spawn(player);
		return true;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}

	private static BlockBounds getBounds(MapTemplate template, String marker) {
		BlockBounds bounds = template.getMetadata().getFirstRegionBounds(marker);
		return bounds == null ? EMPTY_BOUNDS : bounds;
	}

	private static AABB getBox(MapTemplate template, String marker) {
		BlockBounds bounds = template.getMetadata().getFirstRegionBounds(marker);
		return bounds == null ? new AABB(Vec3.ZERO, Vec3.ZERO) : bounds.asBox();
	}
}
