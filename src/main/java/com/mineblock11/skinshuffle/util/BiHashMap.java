/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

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