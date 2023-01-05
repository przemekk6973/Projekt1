package Map;

//import Core.Config;
import Core.Vector2d;
import Objects.Animal;

public class RoundMap extends AbstractWorldMap {


    @Override
    public Vector2d positionAfterMove(Vector2d oldPosition, Vector2d moveVector, Animal animal) {
        // -1 -1
        Vector2d newPosition = oldPosition.add(moveVector);

        //-1 0

        if (newPosition.getY() < 0)
        {
            newPosition =  new Vector2d(newPosition.getX(),0); 
        }

        else if(newPosition.getY()  >= height)
        {
            newPosition =  new Vector2d(newPosition.getX(),width-1);
        }

         if(newPosition.getX() >= width)
        {
            newPosition =  new Vector2d(0,newPosition.getY());

        }

        else if(newPosition.getX() < 0  )
        {
            newPosition=  new Vector2d(width-1, newPosition.getY() );
        }
        return  newPosition;


    }


}
