package net.brutuspvp.core.model;

import java.util.UUID;

import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.Punishment;

public class Mute extends Punishment {

	public Mute(UUID player, UUID actor, String reason, Type type) {
		super(player, actor, reason, type);
	}
}