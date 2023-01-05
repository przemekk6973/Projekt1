package Map;

import Core.Vector2d;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;


    public MapDirection next()
    {
        return switch (this) {
            case NORTH ->  NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST ->   SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH ->  SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST ->   NORTH_WEST;
            case NORTH_WEST -> NORTH;

        };
    }
    public MapDirection previous()
    {
       return switch (this) {
            case NORTH ->  NORTH_WEST;
            case NORTH_WEST -> WEST;
            case WEST -> SOUTH_WEST;
            case SOUTH_WEST -> SOUTH;
            case SOUTH ->  SOUTH_EAST;
            case SOUTH_EAST -> EAST;
            case EAST ->   NORTH_EAST;
            case NORTH_EAST -> NORTH;


        };

    }

    public MapDirection reverse()
    {
        return switch (this) {
            case NORTH ->  SOUTH;
            case NORTH_WEST -> SOUTH_EAST;
            case WEST -> EAST;
            case SOUTH_WEST -> NORTH_EAST;
            case SOUTH ->  NORTH;
            case SOUTH_EAST -> NORTH_WEST;
            case EAST ->   WEST;
            case NORTH_EAST -> SOUTH_WEST;
        };
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTH_WEST -> new Vector2d(-1, 1);
        };


    }

    public MapDirection turnRightBy(int numberOfTurns) {
        return values()[(ordinal() + numberOfTurns) % values().length];
    }

    @Override
    public String toString() {
        return switch (this) {

            case NORTH ->  "Północ";
            case NORTH_EAST -> "Północny Wschód";
            case NORTH_WEST -> "Północny Zachód";
            case SOUTH ->  "Południe";
            case SOUTH_EAST -> "Południowy Wschód";
            case SOUTH_WEST -> "Południowy Zachód";
            case EAST ->   "Wschód";
            case WEST ->   "Zachód";

        };

    }
}
