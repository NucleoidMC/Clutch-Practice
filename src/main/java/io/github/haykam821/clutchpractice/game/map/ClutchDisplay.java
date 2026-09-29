package io.github.haykam821.clutchpractice.game.map;

import org.joml.Matrix4x3f;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.haykam821.clutchpractice.clutch.ClutchType;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClutchDisplay {
	private final ItemDisplayElement icon;
	private final TextDisplayElement name;

	public ClutchDisplay(ServerLevel world, Vec3 pos, ClutchType type) {
		Direction facing = Direction.SOUTH;

		this.icon = new ItemDisplayElement();
		this.icon.setOffset(new Vec3(0, 0.5, 0));

		Matrix4x3f matrix = new Matrix4x3f();
		matrix.rotate(facing.getRotation());
		matrix.translate(0.25f, 0.04f, 0.31f);
		matrix.rotateX(Mth.DEG_TO_RAD * -22.5f);
		matrix.scale(0.5f, 0.5f, 0.25f);
		matrix.translate(0, -0.2f, -1.55f);
		matrix.rotateX((float) Math.PI);
		matrix.translate(-0.5f, -0.5f, -0.5f);
		matrix.rotateLocalZ((float) Math.PI);
		this.icon.setTransformation(matrix);

		this.name = new TextDisplayElement();
		this.name.setOffset(new Vec3(0, 1, -0.4));
		this.name.setBackground(0xB0_00_00_00);
		this.name.setLineWidth(100);
		this.name.setLeftRotation(facing.getRotation().rotateX((float) Math.PI / -2));

		this.setType(type);

		ElementHolder holder = new ElementHolder();

		holder.addElement(this.icon);
		holder.addElement(this.name);

		ChunkAttachment.ofTicking(holder, world, pos);
	}

	public void setType(ClutchType type) {
		this.icon.setItem(type.getIcon());
		this.name.setText(type.getName());
	}
}
