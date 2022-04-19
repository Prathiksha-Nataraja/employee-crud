package com.torryharris.employee.crud.util;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class Utils {
  private static final Logger LOGGER = LogManager.getLogger(Utils.class);



  /**
   * Get an error response object with the given error message
   * @param errorMessage error message to set
   * @return {@link JsonObject}
   */
  public static JsonObject getErrorResponse(String errorMessage) {
    return new JsonObject()
      .put("error", new JsonObject().put("message", errorMessage));
  }

  public static List<Employee> getConnection(long id)  {

    List<Employee> employees = new ArrayList<>();
    Employee emp=new Employee();
    Connection connection = null;
    try{
      Class.forName("org.mariadb.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/employee1","root","Root@123");
      Statement stmt = connection.createStatement();
      ResultSet result  = stmt.executeQuery("SELECT * FROM `employee` where id="+id);
      while (result.next()){
        emp.setId(result.getLong("id"));
        emp.setName(result.getString("name"));
        emp.setDesignation(result.getString("designation"));
        emp.setSalary(result.getDouble("salary"));
        emp.setUsername(result.getString("username"));
        emp.setPassword(result.getString("password"));
        employees.add(emp);
        System.out.println("id " +id);
      LOGGER.info(employees);
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return employees;

  }




}
