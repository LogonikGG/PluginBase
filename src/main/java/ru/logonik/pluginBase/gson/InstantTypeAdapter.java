package ru.logonik.pluginBase.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantTypeAdapter implements JsonDeserializer<Instant>, JsonSerializer<Instant> {
  @Override
  public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return Instant.parse(json.getAsString());
  }

  @Override
  public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(instant.toString());
  }
}