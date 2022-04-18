package com.torryharris.employee.crud.model;

import com.torryharris.employee.crud.verticles.ApiServer;
import com.torryharris.employee.crud.verticles.EmployeeDb;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class EmployeeCodec  implements MessageCodec<Response, Response> {


  private int position;

  @Override
  public void encodeToWire(Buffer buffer, Response response) {

    JsonObject jsonToEncode = new JsonObject();
    jsonToEncode.put("statusCode", response.getStatusCode());
    jsonToEncode.put("responseBody", response.getResponseBody());
    jsonToEncode.put("headers", response.getHeaders());

    // Encode object to string
    String jsonToStr = jsonToEncode.encode();

    // Length of JSON: is NOT characters count
    int length = jsonToStr.getBytes().length;

    // Write data into given buffer
    buffer.appendInt(length);
    buffer.appendString(jsonToStr);


  }

  @Override
  public Response decodeFromWire(int pos, Buffer buffer) {
    int _pos = position;

    // Length of JSON
    int length = buffer.getInt(_pos);

    // Get JSON string by it`s length
    // Jump 4 because getInt() == 4 bytes
    String jsonStr = buffer.getString(_pos+=4, _pos+=length);
    JsonObject contentJson = new JsonObject(jsonStr);

    // Get fields
    int statusCode = contentJson.getInteger("statusCode");
    String responseBody = contentJson.getString("responseBody");
    JsonObject headers = contentJson.getJsonObject("headers");

    // We can finally create custom message object
    return new Response(statusCode, responseBody, headers);
  }

  @Override
  public Response transform(Response response) {
    return response;
  }

  @Override
  public String name() {
    return this.getClass().getSimpleName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
