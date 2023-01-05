package Simulation;

import Core.IPositionChangeObserver;

public interface IEngine {

    void addDayObserver(IDayChangeObserver observer);
    void addPositionObserver(IPositionChangeObserver observer);

    void removeDayObserver(IDayChangeObserver observer);
    void removePositionObserver(IPositionChangeObserver observer);
}