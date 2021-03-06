/*
 * Copyright (c) 2011 Jeppetto and Jonathan Thompson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iternine.jeppetto.dao.mongodb.projections;


import com.mongodb.DBObject;


/**
 * Map reduce job that computes a field sum.
 */
class SumMapReduceCommand
        extends AverageMapReduceCommand {

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------

    public SumMapReduceCommand(DBObject query, String field) {
        super(query, field);
    }


    //-------------------------------------------------------------
    // Override - AverageMapReduceCommand
    //-------------------------------------------------------------

    @Override
    protected Double transformToValue(DBObject dbObject) {
        DBObject valueObject = (DBObject) dbObject.get("value");
        Number t = (Number) valueObject.get("total");

        return t.doubleValue();
    }
}
