package com.magicutils.packages;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class ZatGunBullet extends Bullet{

	public ZatGunBullet(Particle particle, Particle impectParticle, double distance, double damage, int liveSpan,
			Vector dir, Vector loc, World w, Entity firedBy) {
		super(particle, impectParticle, distance, damage, liveSpan, dir, loc, w, firedBy);
	}

	protected void updateLocation() {
		if(!alive) return;
		if(lifeTime <= 0) {
			this.alive = false;
			this.impact(loc);
			return;
		}
		this.lifeTime--;
		Block b = IntersectedBlock();
		Entity entity = getIntersectedEntity();
		
		
		if(b != null && entity != null) {
			if(getClosestTmin(b.getBoundingBox()) < getClosestTmin(entity.getBoundingBox())) {
				impactBlock(b);
			}else {
				impactEntity(entity);
			}
			return;
		}else if(b != null && entity == null) {
			impactBlock(b);
			return;
		}else if(b == null && entity != null) {
			impactEntity(entity);
			return;
		}else if(b == null && entity == null) {
			drawProjectile(this.prefLoc, this.prefLoc.clone().add(this.dir.clone()));
		}
	}
	
	protected void impactEntity(Entity entity) {
		LivingEntity e = (LivingEntity) entity;
		e.damage(damage, firedBy);
		Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(firedBy, e, DamageCause.ENTITY_ATTACK, this.damage));
		this.impact(slabAABBImpactPoint(e.getBoundingBox()));
	}
	
	protected void drawProjectile(Vector from, Vector to) {
		Vector tempDirection = to.clone().add(from.clone().multiply(-1));
		double tempDirectionlength = tempDirection.length();
		
		double displacementRange = 2;
		
		int subsections = 10;
		
		Vector[] subVectors = new Vector[subsections];
		for(int i = 0; i < subsections; i++) {
			Vector subTemp = tempDirection.clone().multiply(tempDirectionlength/subsections);
			subVectors[i] = from.clone().add(subTemp);
		}
		for(int i = 0; i < subsections; i++) {
			subVectors[i] = subVectors[i].add(new Vector(new Random().nextDouble()*displacementRange,
					new Random().nextDouble()*displacementRange,
					new Random().nextDouble()*displacementRange));
		}
		for(int i = 0; i < subsections; i++) {
			
		}
		
		
		
		w.getPlayers().forEach(x -> {
			if(x.getLocation().toVector().subtract(loc).length() <= 50 && alive) {
				for(double i = 0; i < 1; i += (1.0/(tempDirectionlength*2.0))) {
					PackageManager.SendParticalPackage(x, particle, from.clone().add(tempDirection), dir, (float)dir.length()*0.5f,0);
				}
			}
		});
	}
	
	protected void impact(Vector loc) {
		drawProjectile(this.prefLoc, loc);
//		w.getPlayers().forEach(x -> {
//			if(x.getLocation().toVector().subtract(loc).length() <= 50) {
//				PackageManager.SendParticalPackage(x, impactParticle, loc, new Vector(0,0,0), 0.2f,20);
//			}
//		});
		
		for(Entity e : w.getEntities()) {
			if(e instanceof LivingEntity) {
				double d = e.getBoundingBox().getCenter().distance(loc);
				if(d < 2) {
					LivingEntity entity = (LivingEntity) e;
					entity.damage(damage*1/d, firedBy);
					Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(firedBy, entity, DamageCause.ENTITY_ATTACK, this.damage));
					
				}
			}
		}
		
	}
	
	protected boolean slabAABB(BoundingBox b) {
		double tx1 = (b.getMinX() - this.prefLoc.getX())/dir.getX();
	    double tx2 = (b.getMaxX() - this.prefLoc.getX())/dir.getX();
	    double ty1 = (b.getMinY() - this.prefLoc.getY())/dir.getY();
	    double ty2 = (b.getMaxY() - this.prefLoc.getY())/dir.getY();
	    double tz1 = (b.getMinZ() - this.prefLoc.getZ())/dir.getZ();
	    double tz2 = (b.getMaxZ() - this.prefLoc.getZ())/dir.getZ();

	    double tmin = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
	    double tmax = Math.min(Math.min(Math.max(tx1, tx2), Math.max(ty1, ty2)), Math.max(tz1, tz2));
	    
	    return tmax >= tmin && tmin > 0;
	}

}
