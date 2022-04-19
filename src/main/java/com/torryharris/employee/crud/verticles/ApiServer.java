package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.EmployeeCodec;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ApiServer extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ApiServer.class);
  private static Router router;
  private EmployeeDb employeeDb;
  private String id;



  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    EventBus eventBus = getVertx().eventBus();

    // Register codec for custom message
    eventBus.registerDefaultCodec(Response.class, new EmployeeCodec());

    employeeDb = new EmployeeDb(vertx);
    router = Router.router(vertx);

    List<Employee> employees = new ArrayList<>();
    router.route().handler(BodyHandler.create(false));

//    Response localMessage = new Response(200, "a0000001", "Local message!");


    router.get("/employees")
      .handler(routingContext ->
        vertx.eventBus().request("getEmployees",null,message->{
          if(message.succeeded()){
          String  response= (String) message.result().body();
//            System.out.println("Received reply: " + response.getHeaders());

       routingContext.response().putHeader("content-type" ,"application/json").end(message.result().body().toString());
        }
        else{
          System.out.println("unsuccesful");
        }
//          System.out.println("employe details:"+employees);

        })
      );


    router.get("/employees/:id")
      .handler(routingContext -> {
          String id = routingContext.request().getParam("id");
          vertx.eventBus().request("get", id, message -> {
            if(message.succeeded()){
              Response response =(Response)  message.result().body();
              System.out.println("response is"+response);
              routingContext.response().putHeader("content-type" ,"application/json")
                .end(response.getResponseBody());
            }
            else{
              HttpServerResponse serverResponse = routingContext.response();
              ReplyException exception = (ReplyException) message.cause();
//              serverResponse.setStatusCode(exception.failureCode());
              serverResponse.setStatusCode(400);
              serverResponse.putHeader("content-type","application/json")
                .end(Utils.getErrorResponse(exception.getMessage()).encode());
            }

          });
        }
      );
///////////////////////////////////////////////////////////////////
    router.get("/workers/:id")
      .handler(routingContext -> {
          String id = routingContext.request().getParam("id");
          vertx.eventBus().request("getbyid", id, message -> {
            if(message.succeeded()){
              Response response =(Response)  message.result().body();
              System.out.println("response is"+response);
              routingContext.response().putHeader("content-type" ,"application/json")
                .end(response.getResponseBody());
            }
            else{
              HttpServerResponse serverResponse = routingContext.response();
              ReplyException exception = (ReplyException) message.cause();
//              serverResponse.setStatusCode(exception.failureCode());
              serverResponse.setStatusCode(400);
              serverResponse.putHeader("content-type","application/json")
                .end(Utils.getErrorResponse(exception.getMessage()).encode());
            }

          });
        }
      );



    router.post("/employees")
      .handler(routingContext ->{
        Employee employee= Json.decodeValue(routingContext.getBody(),Employee.class);
        System.out.println(employee);
          vertx.eventBus().request("insert",(Json.encode(employee)),reply->{
//        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
        HttpServerResponse serverResponse=routingContext.response();
        employees.add(employee);
        routingContext.response().putHeader("content-type","application/json").end(Json.encode(employee));

          });
        }
      );

    router.put("/employees/:id")
      .handler(routingContext -> {
        Employee employee= Json.decodeValue(routingContext.getBody(),Employee.class);
        System.out.println(employee);
          vertx.eventBus().request("put", (Json.encode(employee)), message -> {
//        HttpServerResponse serverResponse = routingContext.response();
            if(message.succeeded()){
              HttpServerResponse serverResponse = routingContext.response();
              serverResponse.putHeader("content-type", "application/json")
                  .end(message.result().body().toString());
//              routingContext.response().putHeader("content-type" ,"application/json").end(message.result().body().toString());
            }
            else{
              HttpServerResponse serverResponse = routingContext.response();
              ReplyException exception = (ReplyException) message.cause();
//              serverResponse.setStatusCode(exception.failureCode());
              serverResponse.setStatusCode(400);
              serverResponse.putHeader("content-type","application/json")
                  .end(Utils.getErrorResponse(exception.getMessage()).encode());
            }

          });
        }
      );

    router.delete("/employees/:id")
      .handler(routingContext ->{
          String id= routingContext.request().getParam("id");
          HttpServerResponse serverResponse = routingContext.response();
          vertx.eventBus().request("delete",id,message -> {
            if(message.succeeded()){
              System.out.println(message.result().body());
              System.out.println("employee with id" +id);
              serverResponse.end(" Employee deleted successfully...");
            }
            else {
              serverResponse.setStatusCode(400).end("employe with id" +id+  "Does not exist");

            }
          });
        } );


    router.post("/login")
      .handler(routingContext->{
        String authuser= routingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
        authuser= authuser.substring(6);
        String Str= new String(Base64.getDecoder().decode(authuser));
         String[] val = Str.split(":");
          System.out.println(val[0]);
        System.out.println(val[1]);
        String username=val[0];
        String password= val[1];
        HttpServerResponse serverResponse = routingContext.response();
//        employeeDb.login(username,password).future()
//          .onSuccess(response->sendResponse(routingContext,response));
//        System.out.println(routingContext.getBodyAsJson());
//        JsonObject json = new JsonObject()
//          .put("message","Employee login success");
//        routingContext.response()
//          .putHeader("content-type","application/json;charset=UTF8")
//          .end(json.encodePrettily());
        serverResponse.end(" Successful login...");

      });

    HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
    vertx.createHttpServer(options)
      .exceptionHandler(logger::catching)
      .requestHandler(router)
      .listen(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.HTTP_SERVER_PORT)))
      .onSuccess(httpServer -> {
        logger.info("Server started on port {}", httpServer.actualPort());
        startPromise.tryComplete();
      })
      .onFailure(startPromise::tryFail);
  }

  private void sendResponse(RoutingContext routingContext, Response response) {
    response.getHeaders().stream()
      .forEach(entry -> routingContext.response().putHeader(entry.getKey(), entry.getValue().toString()));
    routingContext.response().setStatusCode(response.getStatusCode())
      .end(response.getResponseBody());
  }
}
