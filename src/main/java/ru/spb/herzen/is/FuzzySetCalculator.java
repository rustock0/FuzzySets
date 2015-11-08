package ru.spb.herzen.is;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of methods to work with Fuzzy Sets.
 * @author Evgeny Mironenko
 */
public class FuzzySetCalculator {

    private final Map<Double, Double> set;

    private List<Double> core;
    private List<Double> transitionPoint;
    private Double height;
    private List<Double> support;

    public FuzzySetCalculator(Map<Double, Double> set) {
        this.set = set;
    }

    /**
     * Calculate a fuzzy set.
     */
    public void calculate() {
        core = getCore();
        transitionPoint = getTransitionPoint();
        height = getHeight();
    }

    /**
     * Returns a core of the current fuzzy set.
     * @return see description.
     */
    public List<Double> getCore() {
        if (core == null) {
            core = new ArrayList<>(set.entrySet().stream().filter(entry -> entry.getValue() == 1.0).map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        }
        return core;
    }

    /**
     * Returns a transition point of the current fuzzy set.
     * @return see description.
     */
    public List<Double> getTransitionPoint() {
        if (transitionPoint == null) {
            transitionPoint = new ArrayList<>(set.entrySet().stream().filter(entry -> entry.getValue() == 0.5).map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        }
        return transitionPoint;
    }

    /**
     * Returns a height "supremum(set)" of the current fuzzy set.
     * @return see description.
     */
    public double getHeight() {
        if (height == null) {
            height = 0.0;
            for (Double value : set.values()) {
                if (value > height) {
                    height = value;
                }
            }
        }
        return height;
    }

    /**
     * Returns a support of the current fuzzy set.
     * @return see description.
     */
    public List<Double> getSupport() {
        if (support == null) {
            support = new ArrayList<>(set.entrySet().stream().filter(entry -> entry.getValue() > 0.0).map(Map.Entry::getKey)
                .collect(Collectors.toList()));
        }
        return support;
    }

    /**
     * Normalizes current fuzzy set, if it's subnormal. Replace the old set by the new one and returns it.
     * @return a normalized fuzzy set.
     */
    public Map<Double, Double> normalize() {
        if (getHeight() != 1.0) {
            for (Map.Entry<Double, Double> entry : set.entrySet()) {
                set.put(entry.getKey(), entry.getValue() / height);
            }
            height = 1.0;
        }
        return set;
    }
}