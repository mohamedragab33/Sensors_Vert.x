package com.sensors.web.Sensors.models;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Temerature {
  private String uuid= UUID.randomUUID().toString();
  private double temperature = 21.0;

 public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }

}
