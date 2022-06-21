package com.sensors.web.Sensors.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class DatabaseVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("the-host")
      .setDatabase("the-db")
      .setUser("user")
      .setPassword("secret");

// Pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

// Create the pooled client
    SqlClient client = PgPool.client(connectOptions, poolOptions);
  }
}
