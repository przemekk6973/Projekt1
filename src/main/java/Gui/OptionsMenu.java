package Gui;

import Map.AbstractWorldMap;
import Objects.Animal;
import Simulation.SimulationEngine;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class OptionsMenu {

    private List<TextField> menuTextFields;

    OptionsMenu() {
    }

    public int getStartAnimalsCount() {
        return Integer.parseInt(menuTextFields.get(5).getText());
    }

    public void getParamsFromMenuTextFields() {
        AbstractWorldMap.setHeight(Integer.parseInt(menuTextFields.get(0).getText()));
        AbstractWorldMap.setWidth(Integer.parseInt(menuTextFields.get(1).getText()));
        Animal.setStartEnergy(Integer.parseInt(menuTextFields.get(2).getText()));
        Animal.setMoveEnergy(Integer.parseInt(menuTextFields.get(3).getText()));
        Animal.setPlantEnergy(Integer.parseInt(menuTextFields.get(4).getText()));
        SimulationEngine.setMoveDelayMs(Integer.parseInt(menuTextFields.get(6).getText()));
        Animal.setGenotypeLength(Integer.parseInt(menuTextFields.get(7).getText()));
        SimulationEngine.setDailyGrassAmount(Integer.parseInt(menuTextFields.get(8).getText()));
        Animal.setBreedableEnergy(Integer.parseInt(menuTextFields.get(9).getText()));
        Animal.setLoosableEnergy(Integer.parseInt(menuTextFields.get(10).getText()));
        Animal.setMinGenesToMutate(Integer.parseInt(menuTextFields.get(11).getText()));
        Animal.setMaxGenesToMutate(Integer.parseInt(menuTextFields.get(12).getText()));

        AbstractWorldMap.setJungleRatio(Double.parseDouble(menuTextFields.get(13).getText()));

        AbstractWorldMap.calculateJungleSize();
    }

    public int addParamFieldsToMenu(GridPane gridPaneOfEverything) {
        int currentRow = 0;
        String[] intParamNames = {"Width: ", "Height: ", "Start energy: ", "Move energy: ", "Plant energy: ", "Amount of animals: ", "Day interval: ", "Genotype Length", "Daily Grass Growth", "Energy To Breed", "Energy Loss (energy = energy - energy / Loss", "Min Genes To Mutate","Max Genes To Mutate"};
        String[] intParamTextAfterTextField = {" pixels", " pixels", "", "", "", "", " ms", "", "", "","","",""};
        menuTextFields = new ArrayList<>();
        Integer[] intParamsDefaults = {30, 30, 100, 1, 100, 20, 50, 32, 1, 10, 4, 0, 5};
        for (int i = 0; i < 13; i++) {
            TextField intParamTextField = new TextField(intParamsDefaults[i].toString());
            gridPaneOfEverything.add(new Label(intParamNames[i]), 0, currentRow);
            gridPaneOfEverything.add(intParamTextField, 1, currentRow);
            gridPaneOfEverything.add(new Label(intParamTextAfterTextField[i]), 2, currentRow++);
            menuTextFields.add(intParamTextField);
        }
        TextField jungleParamTextField = new TextField("0.1");
        gridPaneOfEverything.add(new Label("Jungle ratio"), 0, currentRow);
        gridPaneOfEverything.add(jungleParamTextField, 1, currentRow++);
        menuTextFields.add(jungleParamTextField);
        return currentRow;
    }

}
