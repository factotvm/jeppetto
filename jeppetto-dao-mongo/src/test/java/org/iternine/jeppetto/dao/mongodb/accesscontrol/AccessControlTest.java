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

package org.iternine.jeppetto.dao.mongodb.accesscontrol;


import org.iternine.jeppetto.dao.NoSuchItemException;
import org.iternine.jeppetto.dao.SimpleAccessControlContext;
import org.iternine.jeppetto.testsupport.MongoDatabaseProvider;
import org.iternine.jeppetto.testsupport.TestContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;


public class AccessControlTest {

    //-------------------------------------------------------------
    // Variables - Private - Static
    //-------------------------------------------------------------

    private TestContext testContext;
    private AccessControllableObjectDAO accessControllableObjectDAO;
    private SettableAccessControlContextProvider accessControlContextProvider;
    private SimpleAccessControlContext accessControlContext1;
    private SimpleAccessControlContext accessControlContext2;
    private SimpleAccessControlContext accessControlContext3;
    private SimpleAccessControlContext accessControlContext4;


    //-------------------------------------------------------------
    // Methods - Set-Up / Tear-Down
    //-------------------------------------------------------------

    @Before
    public void setUp() {
        testContext = new TestContext("AccessControlTest.spring.xml",
                                      "MongoDAOTest.properties",
                                      new MongoDatabaseProvider());

        accessControllableObjectDAO = (AccessControllableObjectDAO) testContext.getBean("accessControllableObjectDAO");
        accessControlContextProvider = (SettableAccessControlContextProvider) accessControllableObjectDAO.getAccessControlContextProvider();

        accessControlContext1 = new SimpleAccessControlContext();
        accessControlContext1.setAccessId("001");
        accessControlContext1.setRole("User");

        accessControlContext2 = new SimpleAccessControlContext();
        accessControlContext2.setAccessId("002");
        accessControlContext2.setRole("User");

        accessControlContext3 = new SimpleAccessControlContext();
        accessControlContext3.setAccessId("003");
        accessControlContext3.setRole("Administrator");

        accessControlContext4 = new SimpleAccessControlContext();   // No accessId or role
    }


    @After
    public void tearDown() {
        if (testContext != null) {
            testContext.close();
        }
    }


    //-------------------------------------------------------------
    // Methods - Test Cases
    //-------------------------------------------------------------

    @Test
    public void authorizedAccessAttempt()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        AccessControllableObject resultObject = accessControllableObjectDAO.findById(accessControllableObject.getId());

        Assert.assertEquals(resultObject.getId(), accessControllableObject.getId());
    }


    @Test(expected = NoSuchItemException.class)
    public void unauthorizedAccessAttempt()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        accessControlContextProvider.setCurrent(accessControlContext2);

        accessControllableObjectDAO.findById(accessControllableObject.getId());
    }


    @Test(expected = NoSuchItemException.class)
    public void unauthorizedAccessAttemptWithEmptyAccessControlContext()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        accessControlContextProvider.setCurrent(accessControlContext4);

        accessControllableObjectDAO.findById(accessControllableObject.getId());
    }


    @Test(expected = NoSuchItemException.class)
    public void unauthorizedAccessAttemptWithEmptyACLAndEmptyAccessControlContext()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);
        accessControllableObjectDAO.revokeAccess(accessControllableObject.getId(), accessControlContext1.getAccessId());

        accessControlContextProvider.setCurrent(accessControlContext4);

        accessControllableObjectDAO.findById(accessControllableObject.getId());
    }


    @Test(expected = NoSuchItemException.class)
    public void cantAccessCreatedObjectWithEmptyEmptyAccessControlContext()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext4);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        accessControllableObjectDAO.findById(accessControllableObject.getId());
    }


    @Test
    public void grantedAccessAttempt()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        accessControllableObjectDAO.grantAccess(accessControllableObject.getId(), accessControlContext2.getAccessId());

        accessControlContextProvider.setCurrent(accessControlContext2);

        AccessControllableObject resultObject = accessControllableObjectDAO.findById(accessControllableObject.getId());

        Assert.assertEquals(resultObject.getId(), accessControllableObject.getId());
        Assert.assertEquals(2, accessControllableObjectDAO.getAccessIds(resultObject.getId()).size());
    }


    @Test
    public void getList() {
        accessControlContextProvider.setCurrent(accessControlContext1);

        for (int i = 0; i < 10; i++) {
            accessControllableObjectDAO.save(new AccessControllableObject());
        }

        accessControlContextProvider.setCurrent(accessControlContext2);

        for (int i = 0; i < 5; i++) {
            accessControllableObjectDAO.save(new AccessControllableObject());
        }

        accessControlContextProvider.setCurrent(accessControlContext1);

        Iterable<AccessControllableObject> simpleObjectsAvailableToUser1 = accessControllableObjectDAO.findAll();

        String randomSimpleObjectId = null;
        int count = 0;

        for (AccessControllableObject accessControllableObject : simpleObjectsAvailableToUser1) {
            if (randomSimpleObjectId == null) {
                randomSimpleObjectId = accessControllableObject.getId();
            }
            count++;
        }

        Assert.assertEquals(10, count);

        accessControlContextProvider.setCurrent(accessControlContext2);

        Iterable<AccessControllableObject> simpleObjectsAvailableToUser2 = accessControllableObjectDAO.findAll();

        int count2 = 0;
        //noinspection UnusedDeclaration
        for (AccessControllableObject accessControllableObject : simpleObjectsAvailableToUser2) {
            count2++;
        }

        Assert.assertEquals(5, count2);
        for (AccessControllableObject accessControllableObject : simpleObjectsAvailableToUser2) {
            Assert.assertNotSame(randomSimpleObjectId, accessControllableObject.getId());
        }
    }


    @Test
    public void allowedRoleAccessAttempt()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        accessControlContextProvider.setCurrent(accessControlContext3);

        AccessControllableObject resultObject = accessControllableObjectDAO.findById(accessControllableObject.getId());

        Assert.assertEquals(resultObject.getId(), accessControllableObject.getId());
    }


    @Test
    public void verifyOrderByWorks() {
        accessControlContextProvider.setCurrent(accessControlContext1);

        for (int i = 0; i < 10; i++) {
            accessControllableObjectDAO.save(new AccessControllableObject());
        }

        List<AccessControllableObject> orderedItems = accessControllableObjectDAO.findByOrderById();

        Assert.assertEquals(10, orderedItems.size());

        String lastId = null;
        for (AccessControllableObject orderedItem : orderedItems) {
            if (lastId != null) {
                Assert.assertTrue("lastId is not less than thisId: " + lastId + " !< " + orderedItem.getId(),
                                  lastId.compareTo(orderedItem.getId()) < 0);
            }

            lastId = orderedItem.getId();
        }
    }


    @Test
    public void associationAccessAttempt()
            throws NoSuchItemException {
        accessControlContextProvider.setCurrent(accessControlContext1);

        AccessControllableObject accessControllableObject = new AccessControllableObject();

        RelatedObject relatedObject = new RelatedObject();
        relatedObject.setValue("foo");

        accessControllableObject.setRelatedObjects(Collections.singleton(relatedObject));

        accessControllableObjectDAO.save(accessControllableObject);

        accessControllableObject = new AccessControllableObject();

        accessControllableObjectDAO.save(accessControllableObject);

        List<AccessControllableObject> resultObjects = accessControllableObjectDAO.findByHavingRelatedObjectsWithValue("foo");

        Assert.assertEquals(1, resultObjects.size());
    }


    @Test
    public void checkAnnotationQueryWorks() {
        accessControlContextProvider.setCurrent(accessControlContext1);

        for (int i = 1; i < 10; i++) {
            accessControllableObjectDAO.save(new AccessControllableObject(i));
        }

        accessControlContextProvider.setCurrent(accessControlContext2);

        for (int i = 2; i < 10; i++) {
            accessControllableObjectDAO.save(new AccessControllableObject(i));
        }

        accessControlContextProvider.setCurrent(accessControlContext1);

        Assert.assertEquals(3, accessControllableObjectDAO.getByIntValueLessThan(4).size());
        Assert.assertEquals(2, accessControllableObjectDAO.getByIntValueLessThanSpecifyingContext(4, accessControlContext2).size());
        Assert.assertEquals(5, accessControllableObjectDAO.getByIntValueLessThanUsingAdministratorRole(4).size());
        Assert.assertEquals(0, accessControllableObjectDAO.getByIntValueLessThanUsingBogusRole(4).size());
    }
}
