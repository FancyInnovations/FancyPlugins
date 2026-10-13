package de.oliver.fancyvisuals.config;

public class FancyVisualsConfig {

    private int amountWorkerThreads;

    public FancyVisualsConfig() {
        this.amountWorkerThreads = 4;
    }

    public void load() {
        
    }

    public int getAmountWorkerThreads() {
        return amountWorkerThreads;
    }

}
