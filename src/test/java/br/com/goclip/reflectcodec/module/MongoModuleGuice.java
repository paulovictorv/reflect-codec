package br.com.goclip.reflectcodec.module;

import br.com.goclip.reflectcodec.AppCodecProvider;
import br.com.goclip.reflectcodec.enumcodec.EnumCodecProvider;
import ch.rasc.bsoncodec.time.InstantInt64Codec;
import ch.rasc.bsoncodec.time.LocalDateStringCodec;
import ch.rasc.bsoncodec.time.LocalDateTimeDateCodec;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.codecs.GridFSFileCodecProvider;
import com.mongodb.client.model.geojson.codecs.GeoJsonCodecProvider;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.LocalTimeCodec;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoModuleGuice implements Module {

    @Override
    public void configure(Binder binder) {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                fromProviders(asList(new ValueCodecProvider(),
                        new BsonValueCodecProvider(),
                        new DBRefCodecProvider(),
                        new DBObjectCodecProvider(),
                        new DocumentCodecProvider(new DocumentToDBRefTransformer()),
                        new IterableCodecProvider(new DocumentToDBRefTransformer()),
                        new MapCodecProvider(new DocumentToDBRefTransformer()),
                        new GeoJsonCodecProvider(),
                        new GridFSFileCodecProvider())),
                CodecRegistries.fromCodecs(
                        new InstantInt64Codec(),
                        new LocalDateStringCodec(),
                        new LocalDateTimeDateCodec(),
                        new LocalTimeCodec()
                ),
                CodecRegistries.fromProviders(
                        new EnumCodecProvider(),
                        new AppCodecProvider("br.com.goclip.reflectcodec")
                )
        );

        Optional<String> mayUrl = Optional.ofNullable(System.getenv("MONGO_URL"));
        MongoClientURI uri = new MongoClientURI(mayUrl.orElse("mongodb://localhost:27017/codec"));
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(uri.getDatabase()).withCodecRegistry(codecRegistry);
        binder.bind(MongoDatabase.class).toInstance(mongoDatabase);
    }
}
