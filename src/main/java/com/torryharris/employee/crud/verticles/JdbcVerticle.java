package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.* ;




public class JdbcVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(JdbcVerticle.class);
  public void start(Promise<Void> startPromise) throws Exception {

    vertx.eventBus().consumer("getbyid", message -> {
      Response response = new Response();
      String msg = ((String) message.body());
      LOGGER.info(msg);
        Utils.getConnection(Long.parseLong(msg));


    });
  }
}
