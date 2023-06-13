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

package com.mineblock11.skinshuffle.client.gui.cursed;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public record CursedRegistry<T>(RegistryKey<? extends Registry<T>> registryKey, Identifier defaultId, T defaultValue) implements Registry<T>, RegistryEntryOwner<T> {

    @Override
    public RegistryKey<? extends Registry<T>> getKey() {
        return registryKey;
    }

    @Nullable
    @Override
    public Identifier getId(T value) {
        return defaultId;
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T entry) {
        return Optional.empty();
    }

    @Override
    public int getRawId(@Nullable T value) {
        return 0;
    }

    @Nullable
    @Override
    public T get(int index) {
        return defaultValue;
    }

    @Override
    public int size() {
        return 1;
    }

    @Nullable
    @Override
    public T get(@Nullable RegistryKey<T> key) {
        return defaultValue;
    }

    @Nullable
    @Override
    public T get(@Nullable Identifier id) {
        return defaultValue;
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return Lifecycle.experimental();
    }

    @Override
    public Lifecycle getLifecycle() {
        return Lifecycle.experimental();
    }

    @Override
    public Set<Identifier> getIds() {
        return Set.of(defaultId);
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntrySet() {
        return Set.of();
    }

    @Override
    public Set<RegistryKey<T>> getKeys() {
        return Set.of();
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getRandom(Random random) {
        return Optional.empty();
    }

    @Override
    public boolean containsId(Identifier id) {
        return true;
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        return true;
    }

    @Override
    public Registry<T> freeze() {
        return this;
    }

    @Override
    public RegistryEntry.Reference<T> createEntry(T value) {
        return RegistryEntry.Reference.intrusive(this, value);
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getEntry(int rawId) {
        return Optional.empty();
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {
        return Optional.of(RegistryEntry.Reference.standAlone(this, key));
    }

    @Override
    public RegistryEntry<T> getEntry(T value) {
        return RegistryEntry.of(value);
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return null;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
        return Optional.empty();
    }

    @Override
    public RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag) {
        return RegistryEntryList.of(this, tag);
    }

    @Override
    public Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries() {
        return Stream.empty();
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return Stream.empty();
    }

    @Override
    public void clearTags() {

    }

    @Override
    public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {

    }

    @Override
    public RegistryEntryOwner<T> getEntryOwner() {
        return this;
    }

    @Override
    public RegistryWrapper.Impl<T> getReadOnlyWrapper() {
        return new RegistryWrapper.Impl<T>() {
            @Override
            public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
                return CursedRegistry.this.registryKey;
            }

            @Override
            public Lifecycle getLifecycle() {
                return Lifecycle.experimental();
            }

            @Override
            public Stream<RegistryEntry.Reference<T>> streamEntries() {
                return Stream.empty();
            }

            @Override
            public Stream<RegistryEntryList.Named<T>> streamTags() {
                return Stream.empty();
            }

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return Optional.empty();
            }

            @Override
            public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
                return Optional.empty();
            }
        };
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }
}
