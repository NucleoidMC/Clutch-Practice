package io.github.haykam821.clutchpractice.game.map;

import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.plasmid.api.game.GameOpenException;

public class ClutchPracticeMapBuilder {
	private final ClutchPracticeMapConfig config;

	public ClutchPracticeMapBuilder(ClutchPracticeMapConfig config) {
		this.config = config;
	}

	public ClutchPracticeMap build(MinecraftServer server) {
		try {
			MapTemplate template = MapTemplateSerializer.loadFromResource(server, this.config.getId());
			return new ClutchPracticeMap(this.config, template);
		} catch (IOException exception) {
			throw new GameOpenException(Component.translatable("text.clutchpractice.template_load_failed"), exception);
		}
	}
}