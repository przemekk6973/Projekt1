package Simulation;

import Core.IPositionChangeObserver;
import Core.Vector2d;
import Map.AbstractWorldMap;
import Objects.Animal;
import Objects.Grass;
import Objects.IMapElement;
import animalmanagement.AnimalTracker;
import javafx.application.Platform;
import javafx.util.Pair;

import java.util.*;

import static java.lang.System.out;

public class SimulationEngine implements IEngine, Runnable {
    private final AbstractWorldMap map;
    private static int moveDelayMs;
    private boolean isPaused = false;
    public StatisticsEngine statisticsEngine;
    private final AnimalTracker animalTracker;
    private static int dailyGrassAmount;
    List<IDayChangeObserver> dayObservers = new ArrayList<>();
    List<IPositionChangeObserver> positionObservers = new ArrayList<>();

    public static void setMoveDelayMs(int moveDelayMs) {
        SimulationEngine.moveDelayMs = moveDelayMs;
    }

    public static void setDailyGrassAmount(int dailyGrassAmount) {
        SimulationEngine.dailyGrassAmount = dailyGrassAmount;
    }

    @Override
    public void addDayObserver(IDayChangeObserver observer) {
        dayObservers.add(observer);
    }

    @Override
    public void removeDayObserver(IDayChangeObserver observer) {
        dayObservers.remove(observer);
    }

    @Override
    public void addPositionObserver(IPositionChangeObserver observer) {
        positionObservers.add(observer);
    }

    @Override
    public void removePositionObserver(IPositionChangeObserver observer) {
        positionObservers.remove(observer);
    }

    public SimulationEngine(AbstractWorldMap map, int animalsAmount, AnimalTracker animalTracker, String mapName) {
        this.animalTracker = animalTracker;
        Random rn = new Random();
        statisticsEngine = new StatisticsEngine(map, mapName);
        for (int i = 0; i < animalsAmount; i++) {
            Vector2d position = new Vector2d(rn.nextInt(AbstractWorldMap.getWidth()), rn.nextInt(AbstractWorldMap.getHeight()));
            while (map.objectsAt(position) != null) {
                position = new Vector2d(rn.nextInt(AbstractWorldMap.getWidth()), rn.nextInt(AbstractWorldMap.getHeight()));
            }
            map.place(new Animal(map, position, 0));
        }
        this.map = map;
        addDayObserver(statisticsEngine);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, IMapElement object) {
        for (IPositionChangeObserver observer : positionObservers) {
            observer.positionChanged(oldPosition, newPosition, object);
        }
    }

    public void newDayHasCome() {
        for (IDayChangeObserver observer : dayObservers) {
            observer.newDayHasCome();
        }
    }

    private void removeDeadAnimals() {
        List<Animal> animalListCopy = new Vector<>(map.mapObjectsHandler.getAliveAnimals());
        for (Animal animal : animalListCopy) {
            if (animal.isOutOfEnergy()) {
                if (animal == animalTracker.getAnimal()) {
                    animalTracker.justDied(statisticsEngine.getDaysCounter());
                }
                statisticsEngine.addALifespan((statisticsEngine.getDaysCounter() - animal.getDayOfBirth()));
                map.mapObjectsHandler.removeAnimal(animal);
                map.mapObjectsHandler.getAliveAnimals().remove(animal);
            }
        }
        Platform.runLater(statisticsEngine);
    }

    private void moveAllAnimals() {
        for (Animal animal : map.mapObjectsHandler.getAliveAnimals()) {
            animal.subtractMoveEnergy();
            Vector2d oldPosition = new Vector2d(animal.getPosition().x, animal.getPosition().y);
            animal.move(statisticsEngine.getDaysCounter());
            positionChanged(oldPosition, animal.getPosition(), animal);
        }
    }

    private void feedAllAnimals() {
        SortedMap<Vector2d, List<IMapElement>> objectPositions = map.mapObjectsHandler.getObjectPositions();
        List<Vector2d> keySetCopy = new Vector<>(objectPositions.keySet());
        for (Vector2d position : keySetCopy) {
            calculateEatingForPos(objectPositions, position);
        }
        statisticsEngine.updateMostPopularGenotype();
    }

    private void calculateEatingForPos(SortedMap<Vector2d, List<IMapElement>> objectPositions, Vector2d position) {
        Integer highestEnergy = null;
        int highestEnergyCounter = 0;
        boolean isGrassHere = false;

        for (IMapElement mapElement : objectPositions.get(position)) {
            if (mapElement instanceof Animal) {
                if (highestEnergy == null) {
                    highestEnergy = ((Animal) mapElement).getEnergy();
                    highestEnergyCounter = 1;
                } else {
                    if (((Animal) mapElement).getEnergy() == highestEnergy) {
                        highestEnergyCounter++;
                    }
                    if (((Animal) mapElement).getEnergy() > highestEnergy) {
                        highestEnergy = ((Animal) mapElement).getEnergy();
                        highestEnergyCounter = 1;
                    }
                }
            }
            if (mapElement instanceof Grass) {
                isGrassHere = true;
            }

        }
        if (isGrassHere) {
            for (IMapElement mapElement : objectPositions.get(position)) {
                if (mapElement instanceof Animal && highestEnergy != null && ((Animal) mapElement).getEnergy() == highestEnergy) {
                    ((Animal) mapElement).giveEnergy(Animal.getPlantEnergy() / highestEnergyCounter);
                }
            }
            if (highestEnergy != null) {
                map.mapObjectsHandler.removeGrass(position);
                positionChanged(position, position, null);
            }
        }
    }

    public Double getAliveAnimalsCounter() {
        return map.getAliveAnimalsCounter();
    }

    private void addGrassToMap() {
        for (int i = 0; i < dailyGrassAmount; i++){
            Pair<Vector2d, Vector2d> addedGrasses = map.addGrasses();
            if (addedGrasses.getKey() != null) {
                positionChanged(addedGrasses.getKey(), addedGrasses.getKey(), null);
            }
            if (addedGrasses.getValue() != null) {
                positionChanged(addedGrasses.getValue(), addedGrasses.getValue(), null);
        }
    }
}

    private Pair<Animal, Animal> get2StrongestAnimalsAtPos(SortedMap<Vector2d, List<IMapElement>> objectPositions, Vector2d position) {
        Animal strongestAnimal = null;
        Animal secondStrongestAnimal = null;
        for (IMapElement mapElement : objectPositions.get(position)) {
            if (mapElement instanceof Animal) {
                if (strongestAnimal == null) {
                    strongestAnimal = (Animal) mapElement;
                } else {
                    if (((Animal) mapElement).getEnergy() >= strongestAnimal.getEnergy()) {
                        secondStrongestAnimal = strongestAnimal;
                        strongestAnimal = (Animal) mapElement;
                    }
                }
            }
        }
        return new Pair<>(strongestAnimal, secondStrongestAnimal);
    }

    private void reproduceAllAnimals() {
        for (Vector2d position : map.mapObjectsHandler.getObjectPositions().keySet()) {
            Pair<Animal, Animal> animalsInLoveUwU = get2StrongestAnimalsAtPos(map.mapObjectsHandler.getObjectPositions(), position);
            if (animalsInLoveUwU.getValue() != null && animalsInLoveUwU.getValue().getEnergy() > Animal.getBreedableEnergy()) {
                Animal animal = new Animal(animalsInLoveUwU.getKey(), animalsInLoveUwU.getValue(), statisticsEngine.getDaysCounter());
                map.place(animal);
                if (animal.isOffspringOfTrackedAnimal()) {
                    animalTracker.addToOffspringCounter();
                }
            }
        }
    }

    public void pausePlayButtonPressed() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private boolean isEveryoneDead() {
        return getAliveAnimalsCounter() == 0;
    }

    private void addStatisticsToCharts() {
        statisticsEngine.addData(LineCharts.aliveAnimalsCounter, map.getAliveAnimalsCounter());
        statisticsEngine.addData(LineCharts.grassCounter, map.getGrassCounter());
        statisticsEngine.addData(LineCharts.avgEnergy, statisticsEngine.getAvgEnergy());
        statisticsEngine.addData(LineCharts.avgAnimalsLiveSpan, statisticsEngine.getAvgLiveSpan());
        statisticsEngine.addData(LineCharts.avgAnimalsChildrenNumber, statisticsEngine.getAvgChildrenCount());
    }

    public void run() {
        while (!isEveryoneDead()) {
            if (!isPaused) {
                removeDeadAnimals();
                moveAllAnimals();
                feedAllAnimals();
                reproduceAllAnimals();
                addGrassToMap();
                addStatisticsToCharts();
                newDayHasCome();

                try {
                    Thread.sleep(moveDelayMs);
                } catch (InterruptedException e) {
                    out.println("Interrupted Threat Simulation Engine");
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    out.println("Interrupted Threat Simulation Engine");
                    e.printStackTrace();
                }
            }
        }
    }
}
