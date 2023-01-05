package Objects;

import Core.Vector2d;


public class Grass implements IMapElement  {

    private Vector2d position;

    public Grass(Vector2d position)  {
            this.position = position;

    }

    public Vector2d getPosition()
    {
        return this.position;

    }

    @Override
    public String getResourcePath() {
        return "src/main/resources/grass.png";
    }
    public String toString() {
        return "*";
    }
}
