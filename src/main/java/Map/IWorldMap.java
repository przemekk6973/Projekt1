package Map;

import Core.IPositionChangeObserver;
import Core.Vector2d;
import Objects.Animal;
import Objects.Grass;
import Objects.IMapElement;
import Simulation.IDayChangeObserver;

import java.util.List;
import java.util.Map;

public interface IWorldMap extends IPositionChangeObserver, IDayChangeObserver {

    Vector2d newAnimalPosition(Vector2d position,Animal animal);
    void putAnimalOnMap(Animal animal);
    IMapElement objectsAt(Vector2d position);
    void putPlantOnMap(Vector2d position);
    int getHeight();
    int getWidth();
    void removePlantFromMap(Vector2d Position);
    Map<Vector2d, List<Animal>> getAnimals();
    Map<Vector2d, Grass> getPlants();

}
