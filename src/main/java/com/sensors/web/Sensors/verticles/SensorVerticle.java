package com.sensors.web.Sensors.verticles;

import com.sensors.web.Sensors.exceptions.ErrorDetails;
import com.sensors.web.Sensors.models.Verticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SensorVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(SensorVerticle.class);
    private static final int httpPort =Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT","8080"));
   private final HashMap<String,Verticle> verticals = new HashMap<>();
    private final Random random = new Random();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.setPeriodic(5000 , this :: synchTemperature);
      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create()).failureHandler(errorContext -> {
        if(errorContext.response().ended()){
          return;
        }
        LOG.error("Error",errorContext.failure());
        errorContext.response()
          .setStatusCode(500)
          .end(new JsonObject()
            .put("Message","Some thing went wrong : ( ").toBuffer());

      });
      String path = "/data/:id";
     router.get("/data/all").handler(this::getAllTempData);
     router.get(path).handler(this::getTempWithId);
      router.put(path).handler(this::updateTempById);
     router.delete(path);



    vertx.createHttpServer().requestHandler(router).listen(httpPort)
      .onSuccess(ok -> {
          LOG.info("Http server Running at : http://172.0.0.1: {}", httpPort);
          startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void updateTempById(RoutingContext routingContext) {
    String tempId = routingContext.pathParam("id");
    LOG.info("Update Data for {} " , tempId);
    JsonObject json =routingContext.getBodyAsJson();
    var tempForUpdate = json.mapTo(Verticle.class);
    verticals.replace(tempId,tempForUpdate);
   // verticals.put(tempId,tempForUpdate);
    routingContext.response().end(json.toBuffer());


  }

  private void getTempWithId(RoutingContext routingContext) {

      final String reqID= routingContext.request().getParam("id");
      LOG.info("GET DATA FOR ID {}",reqID);
      var responseVerticle = Optional.ofNullable(verticals.get(reqID));
      if (responseVerticle.isEmpty()){
        ErrorDetails errorReq = new ErrorDetails(404,new Date(),"Resourse Not found for this Id ", " "+reqID);
        routingContext.response().
          setStatusCode(errorReq.getStatusCode()).
          putHeader("Content-type","application/json")
          .end(errorReq.toJSonObject().toBuffer());
      }
      final JsonObject response = responseVerticle.get().toJsonObject();

      routingContext.
        response().putHeader("Content-type","application/json").setStatusCode(200)
        .end(response.toBuffer());

  }

  private void getAllTempData(RoutingContext routingContext) {
    LOG.info("Processing HTTP Request from : {}",routingContext.normalizedPath());
    JsonArray payLoad = createPayload();
    routingContext.response()
      .putHeader("Content-Type","application/json")
      .setStatusCode(200)
      .end(payLoad.encode());
  }

  private JsonArray createPayload() {
    JsonArray response = new JsonArray();
    for (Map.Entry<String, Verticle> v: verticals.entrySet() ) {
        response.add(new JsonObject().put("uuid",v.getValue().getUuid())
        .put("temperature",v.getValue().getTemperature())
        .put("timestamp",new Date()));
    }
   return response;
  }

  private void synchTemperature(Long id) {
     Verticle verticle = new Verticle() ;
     verticle.setTemperature(verticle.getTemperature() + (random.nextGaussian() /2.0d));
    LOG.info("Updating Temperature to {}",verticle.getTemperature());
    verticals.put(verticle.getUuid(),verticle);
    vertx.eventBus().publish("Temperature.Updates",createPayload());
  }
}
