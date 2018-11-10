/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */
package org.neo4j.ogm.drivers.embedded.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.neo4j.ogm.drivers.embedded.types.adapter.PointToEmbeddedPointAdapter;
import org.neo4j.ogm.drivers.embedded.types.adapter.EmbeddedPointToPointAdapter;
import org.neo4j.ogm.types.NativeTypes;
import org.neo4j.ogm.types.spatial.CartesianPoint2d;
import org.neo4j.ogm.types.spatial.CartesianPoint3d;
import org.neo4j.ogm.types.spatial.GeographicPoint2d;
import org.neo4j.ogm.types.spatial.GeographicPoint3d;

/**
 * @author Michael J. Simons
 */
class EmbeddedNativeTypes implements NativeTypes {
    private final Map<Class<?>, Function> nativeToMappedApdater;
    private final Map<Class<?>, Function> mappedToNativeAdapter;

    EmbeddedNativeTypes() {

        this.nativeToMappedApdater = new HashMap<>();
        this.mappedToNativeAdapter = new HashMap<>();

        this.addSpatialFeatures();
    }

    private final void addSpatialFeatures() {
        Class<?> pointClass = null;
        try {
            pointClass = Class.forName("org.neo4j.values.storable.PointValue", false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return;
        }

        this.nativeToMappedApdater.put(pointClass, new EmbeddedPointToPointAdapter());

        PointToEmbeddedPointAdapter pointToEmbeddedPointAdapter = new PointToEmbeddedPointAdapter();
        this.mappedToNativeAdapter.put(CartesianPoint2d.class, pointToEmbeddedPointAdapter);
        this.mappedToNativeAdapter.put(CartesianPoint3d.class, pointToEmbeddedPointAdapter);
        this.mappedToNativeAdapter.put(GeographicPoint2d.class, pointToEmbeddedPointAdapter);
        this.mappedToNativeAdapter.put(GeographicPoint3d.class, pointToEmbeddedPointAdapter);
    }

    public boolean supportsAsNativeType(Class<?> clazz) {
        return mappedToNativeAdapter.containsKey(clazz);
    }

    @Override
    public Function<Object, Object> getNativeToMappedTypeAdapter(Class<?> clazz) {

        return nativeToMappedApdater.getOrDefault(clazz, Function.identity());

    }

    @Override
    public Function<Object, Object> getMappedToNativeTypeAdapter(Class<?> clazz) {

        return Optional.ofNullable(clazz)
            .map(mappedToNativeAdapter::get)
            .orElseGet(Function::identity);
    }
}
