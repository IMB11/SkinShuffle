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

import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

public class BiHashMap<K1, K2, V> {
    private final HashMap<Pair<K1, K2>, V> internalMap = new HashMap<>();

    public void put(K1 key1, K2 key2, V value) {
       internalMap.put(new Pair<>(key1, key2), value);
    }

    public int getSize() {
        return internalMap.size();
    }

    public Boolean containsKey(K1 key1) {
        return internalMap.keySet().stream().anyMatch(pair -> pair.getA().equals(key1));
    }

    public V get(K1 key1, K2 key2) {
        return internalMap.get(new Pair<>(key1, key2));
    }
    public boolean containsKeys(K1 key1, K2 key2) {
        return internalMap.keySet().stream().anyMatch(pair -> pair.getA().equals(key1) && pair.getB().equals(key2));
    }

    public void clear() {
        internalMap.clear();
    }

}