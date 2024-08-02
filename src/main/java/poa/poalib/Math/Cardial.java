package poa.poalib.Math;

public class Cardial {
    public enum Cardinal {
        EAST(270),
        NORTH(0),
        SOUTH(180),
        WEST(90);


        Cardinal(double rotation) {
        }

        public static Cardinal getWholeCardinal(double rotation) {
            double yaw = (rotation + 90 + 360) % 360;
            if (yaw < 45 || yaw >= 315)
                return EAST;
            else if (yaw < 135)
                return SOUTH;
            else if (yaw < 225)
                return WEST;
            return NORTH;
        }
    }
}
