package Map;


//import Core.Config;
import Core.Vector2d;
import Objects.Animal;

import java.util.Random;

public class PortalMap extends AbstractWorldMap {


    //kontrola pozycji na mapie tak jakby była portalem

    @Override
    public Vector2d positionAfterMove(Vector2d oldPosition, Vector2d moveVector, Animal animal) {
        Vector2d newPosition = oldPosition.add(moveVector);
        if (newPosition.getX() >= width || newPosition.getY() >= height || newPosition.getX() < 0 || newPosition.getY() < 0)
        {
            animal.setEnergy(animal.getEnergy() - (animal.getEnergy() / Animal.getLoosableEnergy()));
            return generateNewAnimalPosition();

        }
        else{
            return newPosition;
        }
    }


    private Vector2d generateNewAnimalPosition()
    {
        Random rand = new Random();
        return new Vector2d(rand.nextInt(width-1), rand.nextInt(height-1));


    }



}
