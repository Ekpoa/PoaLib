package poa.poalib.PacketUtil;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import poa.poalib.Vector.Vectors;

import java.util.Set;

public class Particles {


    public static void spawnParticles(Particle particle, Location location, int amount){
        for(Player p : location.getNearbyPlayers(200))
            p.spawnParticle(particle,location,amount);
    }

    public static void spawnParticlesNoVelocity(Particle particle, Location location, int amount){
        for(Player p : location.getNearbyPlayers(200))
            p.spawnParticle(particle,location, amount, 0, 0, 0,0);
    }

    public static void spawnParticlesOffset(Particle particle, Location location, int amount, int x, int y, int z){
        for(Player p : location.getNearbyPlayers(200))
            p.spawnParticle(particle,location,amount, x, y,z);
    }

    public static void particleLine(Particle particle, Location loc1, Location loc2, double step){
        Location l1 = loc1.clone();
        Vector v = Vectors.directionFromTo(loc1.toVector(), loc2.toVector());
        v.multiply(step);

        double times = Math.floor(loc1.distanceSquared(loc2) / step);

        for (int i = 0; i < times; i++) {
            spawnParticlesNoVelocity(particle, l1, 1);
            l1 = l1.clone().toVector().add(v).toLocation(l1.getWorld());
        }
    }

    public static void spawnParticleCircle(Location center, double radius, float density, Particle particle) {
        World world = center.getWorld();

        double cutoffAngle = 2* Math.PI;

        Set<Vector> points = Vectors.calculateDisc(radius, density, cutoffAngle);
        for (Vector point : points) {
            world.spawnParticle(particle, center.clone().add(point), 1, 0, 0, 0, 0, null, true);
        }
    }

    //world.spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0, null, true);
}
