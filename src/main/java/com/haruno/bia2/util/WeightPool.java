package com.haruno.bia2.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;

public class WeightPool<T> {
    private final Random random;
    private final Map<T,Integer> weightMap = new LinkedHashMap<>();
    private final Map<T,Integer> disabledMap = new HashMap<>();

    public WeightPool(Random random){
        this.random = random;
    }

    public WeightPool() {
        this(new Random());
    }

    public void put(T t,int weight){
        if(weight > 0)
            weightMap.put(t,weight);
        else
            throw new IllegalArgumentException("Weight must be greater than zero");
    }

    public T roll(){
        long totalWeight = 0;
        for(Integer weight:weightMap.values())
            totalWeight += weight;
        long randomIndex = random.nextLong(totalWeight);
        totalWeight = 0;

        for(Entry<T,Integer> entry:weightMap.entrySet()){
            totalWeight += entry.getValue();
            if(randomIndex < totalWeight)
                return entry.getKey();
        }

        return null;
    }

    public boolean isEmpty(){
        return weightMap.isEmpty();
    }

    public Integer remove(T t){
        if(weightMap.containsKey(t))
            return weightMap.remove(t);
        throw new IllegalArgumentException("No such Key!");
    }

    public Integer setWeight(T t, Integer weight){
        if(weight > 0)
            return weightMap.replace(t, weight);
        throw new IllegalArgumentException("Weight must be greater than zero!");
    }

    public void disable(T t){
        if(weightMap.containsKey(t)) {
            Integer disabledWeight = weightMap.replace(t, 0);
            disabledMap.put(t, disabledWeight);
        }else throw new NoSuchElementException("No Key Founded!");
    }

    public boolean enable(T t){
        if(disabledMap.containsKey(t)){
            Integer disabledWeight = disabledMap.get(t);
            weightMap.replace(t, disabledWeight);
            return true;
        }
        return false;
    }

    public void clear(){
        weightMap.clear();
        disabledMap.clear();
    }
}
