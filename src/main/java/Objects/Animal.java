 package Objects;

 import Core.IPositionChangeObserver;
 import Core.Vector2d;
 import Map.AbstractWorldMap;
 import Map.MapDirection;
 import Simulation.StatisticsEngine;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.Random;

public class Animal implements IMapElement {
    private Vector2d position;
    private MapDirection direction;
    private final AbstractWorldMap map;
    private final List<IPositionChangeObserver> observers = new ArrayList<>();
    private int energy;
    private final int dayOfBirth;
    private int childrenCounter=0;
    private final int[] genotype;
    private boolean isOffspringOfTrackedAnimal;
    private boolean isTracked;
    public StatisticsEngine statisticsEngine;

    private static int genotypeLength;
    private static int startEnergy;
    private static int moveEnergy;
    private static int plantEnergy;
    private static int breedableEnergy;
    private static int loosableEnergy;
    private static int maxGenesToMutate;
    private static int minGenesToMutate;

    public static int getStartEnergy() {
        return startEnergy;
    }

    public static void setStartEnergy(int startEnergy) {
        Animal.startEnergy = startEnergy;
    }
    public static void setGenotypeLength(int genLength){
        Animal.genotypeLength = genLength;
    }

    public static int getBreedableEnergy() {
        return breedableEnergy;
    }

    public static void setBreedableEnergy(int breedableEnergy) {
        Animal.breedableEnergy = breedableEnergy;
    }

    public static int getLoosableEnergy() {
        return loosableEnergy;
    }

    public static void setLoosableEnergy(int loosableEnergy) {
        Animal.loosableEnergy = loosableEnergy;
    }

    public static void setMoveEnergy(int moveEnergy) {
        Animal.moveEnergy = moveEnergy;
    }

    public static int getPlantEnergy() {
        return plantEnergy;
    }

    public static void setPlantEnergy(int plantEnergy) {
        Animal.plantEnergy = plantEnergy;
    }

    public boolean isOutOfEnergy() {
        return energy < 0;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    public void giveEnergy(int energyGiven) {
        energy += energyGiven;
    }

    public void subtractMoveEnergy() {
        energy -= moveEnergy;
    }

    public static void setMaxGenesToMutate(int maxGenesToMutate){
        Animal.maxGenesToMutate = maxGenesToMutate;
    }

    public static void setMinGenesToMutate(int minGenesToMutate){
        Animal.minGenesToMutate = minGenesToMutate;
    }
    @Override
    public String getResourcePath() {
        return "src/main/resources/animal.png";
    }

    public double getEnergySaturation() {
        return (double) energy / (double) startEnergy;
    }

    public String toString() {
        return Integer.toString(energy);
    }

    public Vector2d getPosition() {
        return new Vector2d(this.position.x, this.position.y);
    }

    public int[] getGenotype() {
        return genotype;
    }
    

    public Animal(Animal parent1, Animal parent2, int dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
        parent1.childrenCounter++;
        parent2.childrenCounter++;
        this.isOffspringOfTrackedAnimal = parent1.isOffspringOfTrackedAnimal || parent2.isOffspringOfTrackedAnimal || parent1.isTracked || parent2.isTracked;
        this.position = new Vector2d(parent1.getPosition().x, parent1.getPosition().y);
        Random rn = new Random();
        this.direction = MapDirection.values()[rn.nextInt(8)];
        this.map = parent1.map;
        this.energy = parent1.energy / loosableEnergy + parent2.energy / loosableEnergy;
        parent1.energy -= parent1.energy / loosableEnergy;
        parent2.energy -= parent2.energy / loosableEnergy;
        boolean doesP1GetRightSide = rn.nextBoolean();
        int howManyGenesDoesP1Give = (genotypeLength * parent1.energy) / (parent1.energy + parent2.energy);
        this.genotype = new int[genotypeLength];
        if (!doesP1GetRightSide) {
            System.arraycopy(parent1.genotype, 0, this.genotype, 0, howManyGenesDoesP1Give);
            System.arraycopy(parent2.genotype, howManyGenesDoesP1Give, this.genotype, howManyGenesDoesP1Give, genotypeLength - howManyGenesDoesP1Give);
        } else {
            System.arraycopy(parent2.genotype, 0, this.genotype, 0, genotypeLength - howManyGenesDoesP1Give);
            System.arraycopy(parent1.genotype, genotypeLength - howManyGenesDoesP1Give, this.genotype, genotypeLength - howManyGenesDoesP1Give, howManyGenesDoesP1Give);
        }
        mutateGenes();
    }

    public Animal(Animal animalToCopy, Vector2d position, int dayOfBirth) {
        Random rn = new Random();
        this.dayOfBirth = dayOfBirth;
        this.position = position;
        this.direction = MapDirection.values()[rn.nextInt(8)];
        this.genotype = animalToCopy.genotype;
        this.energy = startEnergy;
        this.map = animalToCopy.map;
    }

    public Animal(AbstractWorldMap map, Vector2d initialPos, int dayOfBirth) {
        Random rn = new Random();
        this.dayOfBirth = dayOfBirth;
        this.position = initialPos;
        this.direction = MapDirection.values()[rn.nextInt(8)];
        this.map = map;
        this.energy = startEnergy;
        this.genotype = new int[genotypeLength];
        for (int i = 0; i < genotypeLength; i++) {
            genotype[i] = rn.nextInt(8);
        }
    }

    private void mutateGenes(){
        if (maxGenesToMutate == 0 || maxGenesToMutate < minGenesToMutate){
            return;
        }
        else{
            Random rn = new Random();
            int posToMutate;
            Boolean mutation;
            int range = rn.nextInt(maxGenesToMutate-minGenesToMutate) + minGenesToMutate;
            for (int i = 0; i < range; i++){
                posToMutate = rn.nextInt(genotypeLength);
                mutation = rn.nextBoolean();
                if (mutation){
                    switch(this.genotype[posToMutate]){
                        case 0 -> this.genotype[posToMutate] = 7;
                        default -> this.genotype[posToMutate]--;
                    }
                }
                else{
                    switch(this.genotype[posToMutate]){
                        case 7 -> this.genotype[posToMutate] = 0;
                        default -> this.genotype[posToMutate]++;
                }
            }
        }
    }
}


    public void move(int numOfDays) {
        Vector2d moveVector = new Vector2d(0, 0);
        int decide = new Random().nextInt(100);

        if(decide <= 20){
            int direction = genotype[new Random().nextInt(genotype.length)];
            switch (direction) {
                case 0 -> moveVector = moveVector.add(this.direction.toUnitVector());
                case 4 -> moveVector = moveVector.subtract(this.direction.toUnitVector());
                default -> this.direction = this.direction.turnRightBy(direction);
            }
            positionChanged(this.position, map.positionAfterMove(position, moveVector, this), this);
            this.position = map.positionAfterMove(position, moveVector, this);

        }

        else{

        int direction = genotype[(numOfDays - this.getDayOfBirth()) % genotype.length];
        switch (direction) {
            case 0 -> moveVector = moveVector.add(this.direction.toUnitVector());
            case 4 -> moveVector = moveVector.subtract(this.direction.toUnitVector());
            default -> this.direction = this.direction.turnRightBy(direction);
        }
        positionChanged(this.position, map.positionAfterMove(position, moveVector, this), this);
        this.position = map.positionAfterMove(position, moveVector, this);
    }
}

    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition, Vector2d newPosition, IMapElement object) {
        for (IPositionChangeObserver observer : observers) {
            observer.positionChanged(oldPosition, newPosition, object);
        }
    }

    public int getChildrenCounter() {
        return childrenCounter;
    }

    public void setIsTracked(boolean isTracked) {
        this.isTracked = isTracked;
        if (!isTracked) {
            this.isOffspringOfTrackedAnimal = false;
        }
    }

    public boolean isOffspringOfTrackedAnimal() {
        return isOffspringOfTrackedAnimal;
    }

    public int getDayOfBirth() {
        return dayOfBirth;
    }
}


//TODO : pole z ilością genów do mutacji, funkcja do mutacji genów