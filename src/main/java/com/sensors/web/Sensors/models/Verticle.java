package com.sensors.web.Sensors.models;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Verticle {
  private String uuid= UUID.randomUUID().toString();
  private double temperature;

 public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }

}
