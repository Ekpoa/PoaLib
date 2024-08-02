package poa.poalib.Vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Vectors {

    public static Vector directionFromTo(Vector from, Vector to) {
        return to.clone().subtract(from.clone()).normalize().multiply(2);
    }

    public static List<Location> circlePoints(Location location, double radius, double secondRadius, int density, float xRotation, float yRotation, float zRotation) {
        List<Location> locations = new ArrayList<>();
        double limit = 0;
        double pii = Math.PI * 2;
        double rateDiv = Math.PI / Math.abs(density);
        limit = pii;

        for (double theta = 0; theta <= limit; theta += rateDiv) {
            double x = radius * Math.cos(radius * theta);
            double z = secondRadius * Math.sin(radius * theta);

            Vector vector = new Vector(x, 0, z);
            vector.rotateAroundX(Math.toRadians(xRotation));
            vector.rotateAroundY(Math.toRadians(yRotation));
            vector.rotateAroundZ(Math.toRadians(zRotation));

            locations.add(location.clone().add(vector));
        }
        return locations;
    }

    public static List<Location> circlePointsv2(Location location, double scaleX, double scaleZ, double density) {
        List<Location> locations = new ArrayList<>();
        density = density / 10;
        for (double i = 0; i < 2 * Math.PI; i += density) {
            double x = Math.cos(i) * scaleX;
            double z = Math.sin(i) * scaleZ;

            locations.add(location.clone().add(new Vector(x, 0, z)));
        }
        return locations;
    }

    public List<Location> cubeOutline(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double particleDistance, World world) {
        List<Location> result = new ArrayList<>();

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    public static Set<Vector> calculateCircle(double radius, double particleDensity, double cutoffAngle) {
        Set<Vector> points = new HashSet<>();
        double stepSize = particleDensity / radius;
        for (double theta = 0; theta < cutoffAngle; theta += stepSize) {
            points.add(new Vector(Math.cos(theta) * radius, 0, Math.sin(theta) * radius));
        }
        return points;
    }

    public static Set<Vector> calculateDisc(double radius, double particleDensity, double cutoffAngle) {
        Set<Vector> points = new HashSet<>();
        for (double subRadius = particleDensity; subRadius < radius; subRadius += particleDensity) {
            points.addAll(calculateCircle(subRadius, particleDensity, cutoffAngle));
        }
        points.addAll(calculateCircle(radius, particleDensity, cutoffAngle));
        return points;
    }

    public static List<Location> filledCirclePoints(Location center, double radius, float density) {
        double cutoffAngle = 2 * Math.PI;
        Set<Vector> points = Vectors.calculateDisc(radius, density, cutoffAngle);
        List<Location> locations = new ArrayList<>();
        for (Vector point : points) {
            Location ta = center.clone().add(point);
            if (!locations.contains(ta))
                locations.add(ta);
        }
        return locations;
    }


}
