package com.mineblock11.skinshuffle.util;

import java.util.HashMap;
import java.util.Map;

public class BiHashMap<K1, K2, V> {

    private final Map<K1, Map<K2, V>> mMap;
    private int sizeOfMap;

    public BiHashMap() {
        mMap = new HashMap<>();
        sizeOfMap = 0;
    }

    public void put(K1 key1, K2 key2, V value) {
        Map<K2, V> map;
        if (mMap.containsKey(key1)) {
            map = mMap.get(key1);
        } else {
            map = new HashMap<>();
            mMap.put(key1, map);
        }
        map.put(key2, value);
        sizeOfMap++;
    }

    public int getSize() {
        return sizeOfMap;
    }

    public Boolean containsKey(K1 key1) {
        return mMap.containsKey(key1);
    }

    public Boolean containsKey(K1 key1, K2 key2) {
        Map<K2, V> map;
        if (mMap.containsKey(key1)) {
            map = mMap.get(key1);
            return map.containsKey(key2);
        } else
            return false;
    }
    public V get(K1 key1, K2 key2) {
        if (mMap.containsKey(key1)) {
            return mMap.get(key1).get(key2);
        } else {
            return null;
        }
    }
    public boolean containsKeys(K1 key1, K2 key2) {
        return mMap.containsKey(key1) && mMap.get(key1).containsKey(key2);
    }

    public void clear() {
        mMap.clear();
    }

}