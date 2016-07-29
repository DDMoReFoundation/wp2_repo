/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.convertertoolbox.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eu.ddmore.convertertoolbox.TestPropertyMockingApplicationContextInitializer;
import eu.ddmore.convertertoolbox.domain.hal.LinkRelation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestInstanceConfiguration.class, RestClientConfiguration.class}
, initializers = TestPropertyMockingApplicationContextInitializer.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class RelationsControllerIntegrationTest {
    private static final String URL = "http://localhost";

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldReturnRelationsPageWithAllRequiredRelations() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
            URL + ":" + port + "/relations", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        for(LinkRelation relation : LinkRelation.values()) {
            assertContainsRelation(entity, relation);
        }
    }

    private void assertContainsRelation(ResponseEntity<String> entity, LinkRelation relation) {
        assertTrue(String.format("Required relation %s was not found in the page body %s", relation.getRelation(), entity.getBody()),
                entity.getBody().contains(relation.getRelation()));
    }

}