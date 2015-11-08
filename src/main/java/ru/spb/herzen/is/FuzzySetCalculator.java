package ru.spb.herzen.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of methods to work with Fuzzy Sets.
 *
 * @author Evgeny Mironenko
 */
public class FuzzySetCalculator {

    private final Map<Double, Double> set;

    private List<Double> core;
    private List<Double> transitionPoint;
    private Double height;
    private List<Double> support;
    private List<Integer> nearestClearSet;
    private Double entropy;

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
     *
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
     *
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
     *
     * @return see description.
     */
    public double getHeight() {
        if (height == null) {
            height = 0.0;
            for (Double mu : set.values()) {
                if (mu > height) {
                    height = mu;
                }
            }
        }
        return height;
    }

    /**
     * Returns a support of the current fuzzy set.
     *
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
     *
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

    /**
     * Returns a Hamming distance between the current fuzzy set and the specified another set.
     *
     * @param anotherSet another fuzzy set.
     * @return see description.
     */
    public double getHammingDistance(final Map<Double, Double> anotherSet) {
        double distance = 0.0;
        Iterator<Double> setIter = set.values().iterator();
        Iterator<Double> anotherSetIter = anotherSet.values().iterator();
        while (setIter.hasNext() && anotherSetIter.hasNext()) {
            distance += Math.abs(setIter.next() - anotherSetIter.next());
        }
        for (Iterator<Double> lastIter = setIter.hasNext() ? setIter : anotherSetIter; lastIter.hasNext(); ) {
            distance += lastIter.next();
        }
        return distance;
    }

    /**
     * Returns an Euclidean distance between the current fuzzy set and the specified another set.
     *
     * @param anotherSet another fuzzy set.
     * @return see description.
     */
    public double getEuclideanDistance(final Map<Double, Double> anotherSet) {
        double distance = 0.0;
        Iterator<Double> setIter = set.values().iterator();
        Iterator<Double> anotherSetIter = anotherSet.values().iterator();
        while (setIter.hasNext() && anotherSetIter.hasNext()) {
            distance += Math.pow(setIter.next() - anotherSetIter.next(), 2);
        }
        for (Iterator<Double> lastIter = setIter.hasNext() ? setIter : anotherSetIter; lastIter.hasNext(); ) {
            distance += lastIter.next();
        }
        return Math.sqrt(distance);
    }

    /**
     * Returns a nearest clear set for the current fuzzy set.
     *
     * @return see description.
     */
    public List<Integer> getNearestClearSet() {
        if (nearestClearSet == null) {
            nearestClearSet = new ArrayList<>();
            for (Double mu : set.values()) {
                if (mu > 0.5) {
                    nearestClearSet.add(1);
                } else {
                    nearestClearSet.add(0);
                }
            }
        }
        return nearestClearSet;
    }

    /**
     * Returns an entropy of the current fuzzy set.
     *
     * @return see description.
     */
    public double getEntropy() {
        if (entropy == null) {
            entropy = 0.0;
            double p;
            for (Double mu : set.values()) {
                p = mu / set.values().stream().mapToDouble(Double::doubleValue).sum();
                if (p == 0.0) {
                    continue;
                }
                entropy += p * Math.log(p) / Math.log(2);
            }
            entropy = (-1 * entropy) / (Math.log(6) / Math.log(2));
        }
        return entropy;
    }
}
