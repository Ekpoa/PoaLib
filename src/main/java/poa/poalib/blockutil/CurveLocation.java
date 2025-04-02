package poa.poalib.blockutil;

import org.bukkit.Location;
import org.bukkit.World;

public class CurveLocation {

    public static Location[] getBezierCurve(Location start, Location control, Location end, int numPoints) {
        Location[] points = new Location[numPoints];
        World world = start.getWorld();

        for (int i = 0; i < numPoints; i++) {
            // Calculate t (progression along the curve)
            double t = (double) i / (numPoints - 1);

            // Calculate Bezier point using the quadratic formula
            double x = Math.pow(1 - t, 2) * start.getX() + 2 * (1 - t) * t * control.getX() + Math.pow(t, 2) * end.getX();
            double y = Math.pow(1 - t, 2) * start.getY() + 2 * (1 - t) * t * control.getY() + Math.pow(t, 2) * end.getY();
            double z = Math.pow(1 - t, 2) * start.getZ() + 2 * (1 - t) * t * control.getZ() + Math.pow(t, 2) * end.getZ();

            points[i] = new Location(world, x, y, z);
        }

        return points;
    }

}
