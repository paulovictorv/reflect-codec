package br.com.goclip.reflectcodec.creator;

import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.CategoryRental;
import br.com.goclip.reflectcodec.jacksoninterop.polimorphism.model.Link;
import br.com.goclip.reflectcodec.module.MongoModuleGuice;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import name.falgout.jeffrey.testing.junit5.IncludeModule;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IncludeModule({MongoModuleGuice.class})
public class DecodeTest {

    @Inject  private MongoDatabase mongoDatabase;

    MongoCollection<Link> linkCollection;
    List<Link> links;

    @BeforeEach
    void beforeEach() {
        Link link = new CategoryRental("24", Link.Type.CATEGORY_RENTAL);
        linkCollection = mongoDatabase.getCollection("links", Link.class);
        linkCollection.deleteMany(new Document());
        linkCollection.insertOne(link);
        links = linkCollection.find().into(new ArrayList<>());
    }

    @Test
    void itShouldReturnPeople() {
        Link link = links.get(0);
        assertThat(links.size()).isOne();
    }
}
