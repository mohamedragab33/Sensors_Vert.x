package com.sensors.web.Sensors.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class SensorVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(SensorVerticle.class);
    private static final int httpPort =Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT","8080"));
    private final String uuid= UUID.randomUUID().toString();
    private double temperature = 21.0;
    private final Random random = new Random();


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.setPeriodic(5000 , this :: updateTemperature);
      Router router = Router.router(vertx);
     router.get("/data").handler(this::viewData);
    vertx.createHttpServer().requestHandler(router).listen(httpPort)
      .onSuccess(ok -> {
          LOG.info("Http server Running at : http://172.0.0.1: {}", httpPort);
          startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void viewData(RoutingContext routingContext) {
    LOG.info("Processing HTTP Request from : {}",routingContext.request().remoteAddress());
    JsonObject payLoad = createPayload();
    routingContext.response()
      .putHeader("Content-Type","application/json")
      .setStatusCode(200)
      .end(payLoad.encode());

  }

  private JsonObject createPayload() {
    return new JsonObject()
      .put("UUID", uuid)
      .put("Temperature", temperature)
      .put("TimeStamp", System.currentTimeMillis());
  }

  private void updateTemperature(Long id) {
    temperature = temperature + (random.nextGaussian() /2.0d);
    LOG.info("Updating Temperature to {}",temperature);
    vertx.eventBus().publish("Temperature.Updates",createPayload());


  }
}
