package com.psyhozoom.dev.jffmpeg.Process;

import com.psyhozoom.dev.jffmpeg.Classes.Database;
import com.psyhozoom.dev.jffmpeg.Classes.Streams;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.json.JSONObject;

public class StreamThreads {
  private Database db;
  private ArrayList<Transcoder> transcoderArrayList = new ArrayList<>();
  private boolean error;
  private String errorMSG;

  public StreamThreads(Database db) {
    this.db = db;
  }

  /**
   * Check stream for state streaming or not
   * @param id id of stream
   * @return boolean stream state
   */
  public boolean checkStreamState(int id){
    for (Transcoder transcoder : transcoderArrayList){
      if (transcoder.getId()  == id)
        return true;
    }
    return false;
  }

  /**
   * Start streaming
   * @var Stream stream
   */
  public void startStream(Streams stream){

    boolean state = false;
    state = checkStreamState(stream.getId());
    if (state) {
      setError(true);
      setErrorMSG("Stream already started");
      System.out.println(getErrorMSG());
      return;
    }

    Transcoder transcoder = new Transcoder();
    transcoder.setStream(stream);
    transcoderArrayList.add(transcoder);

        transcoder.startTranscodingStream();
        transcoder.getRunning().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
            if (newValue == false){
              System.out.println("STOPPED: " + transcoder.getId());
              transcoder.stopTranscoding();
              transcoderArrayList.remove(transcoder);
            }
          }
        });



    state = checkStreamState(stream.getId());
    if (!state){
      setErrorMSG("Can't start stream. Check log for errors!");
      setError(true);
      System.out.println(getErrorMSG());
    }

  }

  /**
   * Stop stream
   * @param id id of stream
   * @return boolean is stream successfully stopped
   */
  public boolean stopStream(int id){



    boolean state = checkStreamState(id);
    if (state){
      for (Transcoder transcoder: transcoderArrayList){
        if (transcoder.getId() == id)
          transcoder.stopTranscoding();
      }
    }
    return state;
  }


  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public String getErrorMSG() {
    return errorMSG;
  }

  public void setErrorMSG(String errorMSG) {
    this.errorMSG = errorMSG;
  }

  public JSONObject getBw(int streamID) {
    JSONObject object = new JSONObject();
    for (Transcoder transcoder : transcoderArrayList){
      if (transcoder.getId() == streamID) {
        object.put("bw", transcoder.getBitrate());
      }else{
        object.put("bw", "0kbit/s");
      }

    }
    return object;
  }
}
