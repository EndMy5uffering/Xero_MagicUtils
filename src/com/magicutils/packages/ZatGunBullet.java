package com.magicutils.packages;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ZatGunBullet extends Bullet{

	public ZatGunBullet(Particle particle, Particle impectParticle, double speed, double damage, Vector dir, Vector loc,
			World w, Entity firedBy) {
		super(particle, impectParticle, speed, damage, dir, loc, w, firedBy);
		// TODO Auto-generated constructor stub
	}

}
