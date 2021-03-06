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

package org.iternine.jeppetto;


import org.iternine.jeppetto.dao.annotation.AccessControl;
import org.iternine.jeppetto.dao.annotation.AccessControlRule;
import org.iternine.jeppetto.dao.annotation.AccessControlType;

import java.util.Set;
import java.util.HashSet;


@AccessControl(rules = {
    @AccessControlRule(type = AccessControlType.Role,  value = "Administrator")
} )
public class SimpleObject {

    //-------------------------------------------------------------
    // Variables - Private
    //-------------------------------------------------------------

    private String id;
    private int intValue;
    private Set<RelatedObject> relatedObjects;


    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    public SimpleObject() {
    }


    public SimpleObject(int intValue) {
        this.intValue = intValue;
    }


    //-------------------------------------------------------------
    // Methods - Getter/Setter
    //-------------------------------------------------------------

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public int getIntValue() {
        return intValue;
    }


    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }


    public void addRelatedObject(RelatedObject relatedObject) {
        if (relatedObjects == null) {
            relatedObjects = new HashSet<RelatedObject>();
        }

        relatedObjects.add(relatedObject);
    }


    public Set<RelatedObject> getRelatedObjects() {
        return relatedObjects;
    }


    public void setRelatedObjects(Set<RelatedObject> relatedObjects) {
        this.relatedObjects = relatedObjects;
    }
}
