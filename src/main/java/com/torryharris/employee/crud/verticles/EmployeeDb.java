package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EmployeeDb extends AbstractVerticle {

  private static final Logger LOGGER = LogManager.getLogger(EmployeeDb.class);
  private final Dao<Employee> employeeDao;
  private Object id;


  public EmployeeDb(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    vertx.eventBus().consumer("getEmployees", message -> {
//    Response response = (Response) message.body();
//    System.out.println("Custom message received: "+response.getHeaders());

      employeeDao.getAll()
        .future()
        .onSuccess(employees -> {
            message.reply(Json.encode(employees));

          }
        );

    });

    //////////////////////////////////////////////////////

    vertx.eventBus().consumer("get", message -> {
      Response response = new Response();
      String msg = ((String) message.body());
      String id = (String) message.body();
      employeeDao.get(id)
        .future()
        .onSuccess(employees -> {

          if (employees.isPresent()) {
            Employee employee = employees.get();
            response.setStatusCode(200).setResponseBody(Json.encode(employee));
          } else {
            response.setStatusCode(400).setResponseBody(Utils.getErrorResponse("Employee Id not found").encode());
          }
          message.reply(response);
        })
        .onFailure(throwable -> {
            LOGGER.catching(throwable);
          }
        );

    });





    //////////////////////////////////////////////////////

    vertx.eventBus().consumer("put",message -> {
//      Response response = new Response();
      String msg=((String) message.body());
      Employee emp = Json.decodeValue(msg,Employee.class);
      LOGGER.info(emp);
      employeeDao.update(emp);
      message.reply(Json.encode(emp));
    });

    /////////////////////////////////////////////////////

    vertx.eventBus().consumer("delete",message -> {
      String id= (String) message.body();
      employeeDao. delete(id);
            message.reply("Deleted successfully");

    });


    //////////////////////////////////////////////////////

vertx.eventBus().consumer("insert",message ->{
  String msg=((String) message.body());
  Employee emp = Json.decodeValue(msg,Employee.class);
  LOGGER.info(msg);
  employeeDao.save(emp);
});

/////////////////////////////////////////////////////////




  }

}
