package com.fancyinnovations.fancyeconomy.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DistributedWorkload<T> implements Runnable {

    private final String workloadName;
    private final Consumer<T> action;
    private final Predicate<T> escapeCondition;
    private final List<LinkedList<Supplier<T>>> suppliedValueMatrix;
    private final int distributionSize;
    private final boolean runAsync;
    private int currentPosition;

    public DistributedWorkload(String workloadName, Consumer<T> action, Predicate<T> escapeCondition, int distributionSize, boolean runAsync) {
        this.workloadName = workloadName;
        this.action = action;
        this.escapeCondition = escapeCondition;
        this.distributionSize = distributionSize;
        this.runAsync = runAsync;
        this.currentPosition = 0;
        this.suppliedValueMatrix = new ArrayList<>(distributionSize);
        for (int i = 0; i < distributionSize; i++) {
            this.suppliedValueMatrix.add(new LinkedList<>());
        }
    }

    public void addValue(Supplier<T> valueSupplier) {
        List<Supplier<T>> smallestList = suppliedValueMatrix.get(0);

        for (int i = 1; i < distributionSize; i++) {
            if (smallestList.size() == 0) {
                break;
            }

            if (suppliedValueMatrix.get(i).size() < smallestList.size()) {
                smallestList = suppliedValueMatrix.get(i);
            }
        }

        smallestList.add(valueSupplier);
    }

    private void proceedPosition() {
        currentPosition++;
        if (currentPosition == distributionSize) {
            currentPosition = 0;
        }
    }

    private boolean executeThenCheck(Supplier<T> valueSupplier) {
        T value = valueSupplier.get();
        action.accept(value);
        return escapeCondition.test(value);
    }

    @Override
    public void run() {
        if (runAsync) {
            new Thread(this::runWorkload).start();
        } else {
            runWorkload();
        }
    }

    public void runAll() {
        new Thread(() -> {
            long time = System.currentTimeMillis();

            for (LinkedList<Supplier<T>> valueMatrix : suppliedValueMatrix) {
                for (Supplier<T> supplier : valueMatrix) {
                    executeThenCheck(supplier);
                }
            }

            time = System.currentTimeMillis() - time;

//            Bukkit.getLogger().info("Executed all workloads of '" + workloadName + "' in " + time + "ms");

        }).start();
    }

    private void runWorkload() {
        long startTime = System.currentTimeMillis();

        List<Supplier<T>> suppliers = suppliedValueMatrix.get(currentPosition);
        int amount = suppliers.size();

        suppliers.removeIf(this::executeThenCheck);

        proceedPosition();

        long time = System.currentTimeMillis() - startTime;
        if (amount > 0) {
//            Bukkit.getLogger().info("DistributedWorkload '" + workloadName + "' round " + (currentPosition) + "/" + distributionSize + " with " + amount + " suppliers took " + time + "ms");
        }
    }


    public List<T> getAllSuppliers() {
        List<T> allSuppliers = new ArrayList<>();
        for (LinkedList<Supplier<T>> ll : suppliedValueMatrix) {
            ll.stream().map(Supplier::get).forEach(allSuppliers::add);
        }

        return allSuppliers;
    }
}