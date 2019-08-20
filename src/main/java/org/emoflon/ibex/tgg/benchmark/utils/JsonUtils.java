package org.emoflon.ibex.tgg.benchmark.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

/**
 * JSON helper methods.
 *
 * @author Andre Lehmann
 */
public abstract class JsonUtils {

    public static String jsonToString(JsonObject jsonObject) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);

        try (JsonWriter jsonWriter = writerFactory.createWriter(out)) {
            jsonWriter.writeObject(jsonObject);
            jsonWriter.close();
        } catch (JsonException e) {
            throw e;
        }

        String result = "";
        try {
            result = out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return result;
    }

    /**
     * Saves a {@link JsonObject} to a file.
     * 
     * @param jsonObject the JsonObject to save
     * @param dest       the file destination
     * @throws IOException if the saving failed
     */
    public static void saveJsonToFile(JsonObject jsonObject, Path dest) throws IOException {
        try {
            Files.createDirectories(dest.getParent());
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        Path tmpFile = Paths.get(dest + ".tmp");
        try (OutputStream out = Files.newOutputStream(tmpFile, StandardOpenOption.CREATE)) {
            // write tmp file
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(out);
            jsonWriter.writeObject(jsonObject);
            jsonWriter.close();

            // replace old preferences file
            Files.move(tmpFile, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Loads a JSON file.
     * 
     * @param path the path of the file
     * @return the content as a {@link JsonObject}
     * @throws JsonException if the file content is not a valid JSON structure
     * @throws IOException   if loading of the file failed
     */
    public static JsonObject loadJsonFile(Path path) throws JsonException, IOException {
        if (Files.exists(path)) {
            JsonObject jsonObject;
            try (InputStream in = Files.newInputStream(path)) {
                try (JsonReader reader = Json.createReader(in)) {
                    jsonObject = reader.readObject();
                } catch (JsonException e) {
                    throw e;
                }
            } catch (IOException e) {
                throw e;
            }

            return jsonObject;
        }

        return null;
    }

    /**
     * Add a key to a 'static' JsonObject. Apparently the JSON implementation of
     * Java isn't so great. See
     * http://www.adam-bien.com/roller/abien/entry/how_to_add_an_attribute
     * 
     * @param source the source JsonObject
     * @param key    the new key to be added
     * @param value  the value for the new key
     * @return the JsonObject with the key added
     */
    public static JsonObject addKey(JsonObject source, String key, JsonValue value) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(key, value);
        source.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
        return builder.build();
    }
}