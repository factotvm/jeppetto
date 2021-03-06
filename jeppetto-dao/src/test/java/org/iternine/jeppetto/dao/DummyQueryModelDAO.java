/*
 * Copyright (c) 2011 Jeppetto and Jonathan Thompson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iternine.jeppetto.dao;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DummyQueryModelDAO<T, ID>
        implements QueryModelDAO<T, ID> {

    //-------------------------------------------------------------
    // Variables - Private
    //-------------------------------------------------------------

    private Class<T> entityClass;


    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    protected DummyQueryModelDAO(Class<T> entityClass, Map<String, Object> daoProperties) {
        this.entityClass = entityClass;
    }


    //-------------------------------------------------------------
    // Implementation - QueryModel
    //-------------------------------------------------------------

    @Override
    public T findUniqueUsingQueryModel(QueryModel queryModel)
            throws NoSuchItemException {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Iterable<T> findUsingQueryModel(QueryModel queryModel) {
        List<T> result = new ArrayList<T>();

        try {
            result.add(entityClass.newInstance());
            result.add(entityClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    @Override
    public Object projectUsingQueryModel(QueryModel queryModel) {
        return null;
    }


    @Override
    public Condition buildCondition(String conditionField,
                                    ConditionType conditionType,
                                    Iterator argsIterator) {
        return null;
    }


    @Override
    public Projection buildProjection(String projectionField,
                                      ProjectionType projectionType,
                                      Iterator argsIterator) {
        return null;
    }


    @Override
    public T findById(ID id)
            throws NoSuchItemException {
        return findUniqueUsingQueryModel(null);
    }


    @Override
    public Iterable<T> findAll() {
        return findUsingQueryModel(null);
    }


    @Override
    public void save(T object) {
    }


    @Override
    public void delete(T object) {
    }


    @Override
    public void deleteById(ID id) {
    }


    @Override
    public void flush() {
    }
}
