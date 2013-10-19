package com.darylteo.vertx.sentinel;

import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {
  @Override
  public void start() {
    try (Project root = new Project()) {

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {

    }
  }

  @Override
  public void stop() {
    super.stop();
  }
}
