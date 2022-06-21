package com.sensors.web.Sensors;

import com.sensors.web.Sensors.verticles.SensorVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(SensorVerticle.class);
  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(SensorVerticle.class.getName(),
      new DeploymentOptions().setInstances(1))
      .onSuccess(s -> LOG.info("Deployment Success"));
       LOG.info("Deploying : {}", SensorVerticle.class.getName());
/*          vertx.eventBus()
         .<JsonArray>consumer("Temperature.Updates", message -> {
        LOG.info(" >>> {}", message.body().encodePrettily());
});*/


  }

}
