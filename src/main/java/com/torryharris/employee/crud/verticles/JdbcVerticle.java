package com.torryharris.employee.crud.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support

public class JdbcVerticle extends AbstractVerticle {

  DeploymentOptions options = new DeploymentOptions().setWorker(true);

//    Class.forName("oracle.jdbc.driver.OracleDriver");


}
