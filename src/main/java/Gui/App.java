package Gui;

import Map.AbstractWorldMap;
import Map.PortalMap;
import Map.RoundMap;
import Simulation.IDayChangeObserver;
import Simulation.LineCharts;
import Simulation.SimulationEngine;
import Simulation.StatisticsEngine;
import animalmanagement.AnimalTracker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application implements IDayChangeObserver {

    private GridPane gridPaneOfEverything;
    private Stage primaryStage;
    private SimulationEngine leftMapEngine;
    private SimulationEngine rightMapEngine;
    private SimulationVisualizer leftMapSimulationVisualizer;
    private SimulationVisualizer rightMapSimulationVisualizer;
    private OptionsMenu optionsMenu;

    private final static int windowWidth = 1120;
    private final static int windowHeight = 640;
    public final static int simulationGripPaneWidth = 400;
    public final static int simulationGripPaneHeight = 400;
    private final static int plotsColumnWidth = 320;

    @Override
    public void init() {
        optionsMenu = new OptionsMenu();
        gridPaneOfEverything = new GridPane();
    }

    private void renderMenu() {
        int lowestGripPaneElement = optionsMenu.addParamFieldsToMenu(gridPaneOfEverything);
        Button startButton = new Button("Start!");
        gridPaneOfEverything.add(startButton, 0, lowestGripPaneElement);
        startButton.setOnAction(actionEvent -> startASimulation());
        Platform.runLater(primaryStage::show);
    }

    private void addGripPaneConstraints() {
        gridPaneOfEverything.setMaxHeight(windowHeight);
        gridPaneOfEverything.setMaxWidth(windowWidth);
    }

    private void startASimulationEngine(SimulationEngine engine, SimulationVisualizer simulationVisualizer, boolean isLeft) {
        int columnIndex = 0;
        if (!isLeft) {
            columnIndex = 2;
        }
        Button pauseButton = new Button("Pause");
        VBox middleVBox = new VBox(StatisticsEngine.getLineChart(LineCharts.aliveAnimalsCounter), StatisticsEngine.getLineChart(LineCharts.grassCounter),
                StatisticsEngine.getLineChart(LineCharts.avgEnergy), StatisticsEngine.getLineChart(LineCharts.avgAnimalsLiveSpan),
                StatisticsEngine.getLineChart(LineCharts.avgAnimalsChildrenNumber));
        middleVBox.setMaxWidth(plotsColumnWidth);

        VBox leftSideVBox;
        Button showDominantGenotypeButton = new Button("Show dominant genotype animals");
        Button getDataToFileButton = new Button("Get data to file");

        Label mapNameLabel = new Label("Round map");
        mapNameLabel.setTextFill(Color.rgb(243, 98, 45));
        if (!isLeft) {
            mapNameLabel.setText("Portal map");
            mapNameLabel.setTextFill(Color.rgb(251, 167, 27));
        }
        gridPaneOfEverything.add(mapNameLabel, columnIndex, 0);
        GridPane.setHalignment(mapNameLabel, HPos.CENTER);
        leftSideVBox = new VBox(simulationVisualizer.getSimulationGridPane(), new HBox(pauseButton, showDominantGenotypeButton, getDataToFileButton), new Label("Dominant genotype:"),
                engine.statisticsEngine.getGenotypeLabel(),
                new Label("Tracker:"), simulationVisualizer.getObservedAnimalVBox());
        gridPaneOfEverything.add(leftSideVBox, columnIndex, 1);
        gridPaneOfEverything.add(middleVBox, 1, 1);
        addGripPaneConstraints();

        engine.addPositionObserver(simulationVisualizer);
        engine.addDayObserver(this);

        getDataToFileButton.setDisable(true);
        showDominantGenotypeButton.setDisable(true);

        pauseButton.setOnAction(actionEvent -> {
            engine.pausePlayButtonPressed();
            if (engine.isPaused()) {
                pauseButton.setText("Play");
                getDataToFileButton.setDisable(false);
                showDominantGenotypeButton.setDisable(false);
            } else {
                pauseButton.setText("Pause");
                getDataToFileButton.setDisable(true);
                showDominantGenotypeButton.setDisable(true);
            }
        });

        getDataToFileButton.setOnAction(actionEvent -> {
            if (engine.isPaused()) {
                engine.statisticsEngine.getStatsToFile();
            }
        });

        showDominantGenotypeButton.setOnAction(actionEvent -> {
            if (engine.isPaused()) {
                simulationVisualizer.markDominantGenotypes(engine.statisticsEngine.getDominantGenotypesPositions());
            }
        });

    }

    private void startASimulation() {
        optionsMenu.getParamsFromMenuTextFields();
        gridPaneOfEverything.getChildren().clear();

        AnimalTracker leftAnimalTracker = new AnimalTracker();
        AnimalTracker rightAnimalTracker = new AnimalTracker();

        AbstractWorldMap leftMap = new RoundMap();
        AbstractWorldMap rightMap = new PortalMap();

        leftMapSimulationVisualizer = new SimulationVisualizer(leftMap, leftAnimalTracker);
        rightMapSimulationVisualizer = new SimulationVisualizer(rightMap, rightAnimalTracker);

        leftMapEngine = new SimulationEngine(leftMap, optionsMenu.getStartAnimalsCount(), leftAnimalTracker, "RoundMap");
        rightMapEngine = new SimulationEngine(rightMap, optionsMenu.getStartAnimalsCount(), rightAnimalTracker, "PortalMap");

        startASimulationEngine(leftMapEngine, leftMapSimulationVisualizer, true);
        startASimulationEngine(rightMapEngine, rightMapSimulationVisualizer, false);

        Thread leftEngineThread = new Thread(leftMapEngine);
        leftEngineThread.start();

        Thread rightEngineThread = new Thread(rightMapEngine);
        rightEngineThread.start();
    }


    public void start(Stage primaryStage) {
        Scene scene = new Scene(gridPaneOfEverything, windowWidth, windowHeight);
        primaryStage.setScene(scene);
        this.primaryStage = primaryStage;
        Platform.runLater(primaryStage::show);
        Platform.runLater(this::renderMenu);
    }

    @Override
    public void newDayHasCome() {
        Platform.runLater(this::updateView);
    }

    private void updateView() {
        Platform.runLater(primaryStage::show);
        if (!leftMapEngine.isPaused()) {
            Platform.runLater(leftMapSimulationVisualizer);
        }
        if (!rightMapEngine.isPaused()) {
            Platform.runLater(rightMapSimulationVisualizer);
        }

    }

}
