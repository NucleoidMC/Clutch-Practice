package io.github.haykam821.clutchpractice.game.map;

import java.util.Set;

import io.github.haykam821.clutchpractice.TrackedBlockStateProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

public class ClutchPracticeMap {
	private static final BlockBounds EMPTY_BOUNDS = BlockBounds.ofBlock(BlockPos.ORIGIN);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();

	private static final Vec2f DEFAULT_SPAWN_ROTATION = new Vec2f(90, 0);

	private final ClutchPracticeMapConfig config;
	private final MapTemplate template;
	private final Box box;
	private final BlockBounds area;
	private final Box exit;

	private final BlockBounds clutchSelector;
	private final Vec3d clutchDisplayPos;

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

	public void clearArea(ServerWorld world, TrackedBlockStateProvider floor) {
		Random random = world.getRandom();
		int minY = this.area.min().getY();

		for (BlockPos pos : this.area) {
			if (pos.getY() == minY) {
				world.setBlockState(pos, floor.get(random, pos));
			} else {
				world.setBlockState(pos, AIR);
			}
		}
	}

	public void placeRandomBase(ServerWorld world, TrackedBlockStateProvider base, int offsetY) {
		this.placeRandomBase(world, base, offsetY, offsetY);
	}

	public void placeRandomBase(ServerWorld world, TrackedBlockStateProvider base, int minOffsetY, int maxOffsetY) {
		Random random = world.getRandom();
		int minY = this.area.min().getY();

		int baseX = MathHelper.nextInt(random, this.area.min().getX(), this.area.max().getX());
		int baseZ = MathHelper.nextInt(random, this.area.min().getZ(), this.area.max().getZ());

		BlockPos.Mutable pos = new BlockPos.Mutable(baseX, minY + minOffsetY, baseZ);

		while (pos.getY() <= minY + maxOffsetY) {
			BlockState baseState = base.get(random, pos);
			world.setBlockState(pos, baseState);

			pos.move(Direction.UP);
		}
	}

	public TrackedBlockStateProvider getTrackedFloorProvider() {
		return new TrackedBlockStateProvider(this.config.getFloorProvider());
	}

	public TrackedBlockStateProvider getTrackedBaseProvider() {
		return new TrackedBlockStateProvider(this.config.getBaseProvider());
	}

	public Box getExit() {
		return this.exit;
	}

	public BlockBounds getClutchSelector() {
		return this.clutchSelector;
	}

	public Vec3d getClutchDisplayPos() {
		return this.clutchDisplayPos;
	}

	public Vec3d getSpawn() {
		TemplateRegion spawn = this.template.getMetadata().getFirstRegion("spawn");
		if (spawn != null) {
			return spawn.getBounds().centerBottom();
		}

		return new Vec3d(0.5, 76, 0.5);
	}

	private Vec2f getSpawnRotation() {
		TemplateRegion spawn = this.template.getMetadata().getFirstRegion("spawn");
		if (spawn != null) {
			return spawn.getData().get("Rotation", Vec2f.CODEC).orElse(DEFAULT_SPAWN_ROTATION);
		}

		return DEFAULT_SPAWN_ROTATION;
	}

	public void spawn(ServerPlayerEntity player) {
		Vec3d spawn = this.getSpawn();
		Vec2f rotation = this.getSpawnRotation();

		player.teleport(player.getServerWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), Set.of(), rotation.x, rotation.y, true);
	}

	public boolean respawnIfOutOfBounds(ServerPlayerEntity player) {
		if (this.box.contains(player.getPos())) {
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

	private static Box getBox(MapTemplate template, String marker) {
		BlockBounds bounds = template.getMetadata().getFirstRegionBounds(marker);
		return bounds == null ? new Box(Vec3d.ZERO, Vec3d.ZERO) : bounds.asBox();
	}
}
