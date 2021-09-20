/*
 * MIT License
 *
 * Copyright (c) 2020 Airbyte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.airbyte.integrations.io.airbyte.integration_tests.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import io.airbyte.commons.json.Jsons;
import io.airbyte.db.mongodb.MongoDatabase;
import io.airbyte.integrations.standardtest.source.TestDestinationEnv;
import java.util.List;
import org.bson.Document;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDbSourceStandaloneAcceptanceTest extends MongoDbSourceAbstractAcceptanceTest {

  private MongoDBContainer mongoDBContainer;

  @Override
  protected void setupEnvironment(TestDestinationEnv environment) throws Exception {
    mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
    mongoDBContainer.start();

    final JsonNode instanceConfig = Jsons.jsonNode(ImmutableMap.builder()
        .put("host", mongoDBContainer.getHost())
        .put("port", mongoDBContainer.getFirstMappedPort())
        .build());

    config = Jsons.jsonNode(ImmutableMap.builder()
        .put("instance_type", instanceConfig)
        .put("database", DATABASE_NAME)
        .put("auth_source", "admin")
        .put("tls", false)
        .build());

    String connectionString = String.format("mongodb://%s:%s/",
        mongoDBContainer.getHost(),
        mongoDBContainer.getFirstMappedPort());

    database = new MongoDatabase(connectionString, DATABASE_NAME);

    MongoCollection<Document> collection = database.createCollection(COLLECTION_NAME);
    var doc1 = new Document("id", "0001").append("name", "Test");
    var doc2 = new Document("id", "0002").append("name", "Mongo");
    var doc3 = new Document("id", "0003").append("name", "Source");

    collection.insertMany(List.of(doc1, doc2, doc3));
  }

  @Override
  protected void tearDown(TestDestinationEnv testEnv) throws Exception {
    database.close();
    mongoDBContainer.close();
  }

}
