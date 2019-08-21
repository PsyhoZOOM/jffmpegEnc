package com.psyhozoom.dev.jffmpeg.Services;

import com.psyhozoom.dev.jffmpeg.Classes.Codecs;
import com.psyhozoom.dev.jffmpeg.Classes.Database;
import com.psyhozoom.dev.jffmpeg.Classes.Streams;
import com.psyhozoom.dev.jffmpeg.Classes.StreamsOut;
import com.psyhozoom.dev.jffmpeg.Process.StreamThreads;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.json.JSONObject;

public class ServerWorker implements Runnable {

  private final Socket socket;
  private final Database db;
  private final StreamThreads streamThreads;
  private InputStreamReader isr;
  private BufferedReader bfr;
  private OutputStreamWriter osw;
  private BufferedWriter bfw;

  private String data;
  private JSONObject dataObj;
  private boolean isRunning = false;

  public ServerWorker(Socket socket, StreamThreads streamThreads,
      Database db) {
    this.socket = socket;
    this.db = db;
    this.streamThreads = streamThreads;
    isRunning = true;
    initReaderWriter();
  }

  private void initReaderWriter() {
    try {
      isr = new InputStreamReader(socket.getInputStream());
      bfr = new BufferedReader(isr);
      osw = new OutputStreamWriter(socket.getOutputStream());
      bfw = new BufferedWriter(osw);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {

    while (isRunning) {
      try {
        if ((data = bfr.readLine()) == null) {
          isRunning = false;
          break;
        }
        worker(data);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Client disconnected");
  }

  private void send(JSONObject data) {
    try {
      bfw.write(data.toString());
      bfw.newLine();
      bfw.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void worker(String data) {
    dataObj = new JSONObject(data);

    //if data has no action in json object then close worker
    if (!dataObj.has("action")) {
      isRunning = false;
      return;
    }

    switch (dataObj.getString("action")) {
      case "getVideoCodecs": {
        Codecs codecs = new Codecs();
        send(codecs.getVideoCodecs());
        break;
      }
      case "getAudioCodecs": {
        Codecs codecs = new Codecs();
        send(codecs.getAudioCodecs());
        break;
      }

      case "getFormat": {
        Codecs codecs = new Codecs();
        send(codecs.getOUTFormat());
        break;
      }

      case "saveNewStream": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        streams.saveStream(dataObj);
        if (streams.isError()) {
          object.put("ERROR", streams.getErrorMsg());
        }

        send(object);
        break;
      }

      case "getAllStreams": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        object = streams.getAllStreams();
        if (streams.isError()) {
          object.put("ERROR", streams.getErrorMsg());
        }

        send(object);
        break;
      }

      case "getAllStreamsOut": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        object = streams.getAllStreamsOut();
        if (streams.isError()) {
          object.put("ERROR", streams.getErrorMsg());
        }
        send(object);
        break;
      }

      case "getStreamOut": {
        JSONObject object = new JSONObject();
        StreamsOut streamsOut = new StreamsOut();
      }

      case "startStream": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        streams.startStream(dataObj.getInt("uniqueID"), this.db, streamThreads);
        if (streams.isError()) {
          object.put("ERROR", streams.getErrorMsg());
        }
        send(object);
        break;
      }

      case "stopStream": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        streams.stopStream(dataObj.getInt("uniqueID"), streamThreads);
        if (streams.isError()) {
          object.put("ERROR", streams.getErrorMsg());
        }
        send(object);
        break;
      }

      case "getStreamsOut": {
        JSONObject object = new JSONObject();
        Streams stream = new Streams(db);
        object = stream.getStreamsOutOfSrc(dataObj.getInt("streamID"));

        if (object.has("ERROR")){
          object.put("ERROR", stream.getErrorMsg());
        }
        send(object);
        break;
      }

      case "addStreamOutToSource": {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        //object of args, main streamID
        streams.addStreamOutToStreams(dataObj.getJSONObject("stream"), dataObj.getInt("streamID"));
        if (streams.isError())
          object.put("ERROR", streams.getErrorMsg());

        send(object);

        break;
      }

      case "getInfo" : {
        JSONObject object = new JSONObject();
         object = streamThreads.getBw(dataObj.getInt("streamID"));
        send(object);
        break;

      }

      case "updateStreamOut" : {
        JSONObject object = new JSONObject();
        Streams streams = new Streams(db);
        streams.updateStreamOut(dataObj);
        if (streams.isError())
          object.put("ERROR", streams.getErrorMsg());

        send(object);
        break;
      }

    }

    System.out.println(dataObj.getString("action"));

  }
}
