package com.psyhozoom.dev.jffmpeg.Process;

import com.psyhozoom.dev.jffmpeg.Classes.Database;
import com.psyhozoom.dev.jffmpeg.Classes.Streams;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        updateStatus(transcoder.getId(), true, transcoder.getPid());

        transcoder.getRunning().addListener(new ChangeListener<Boolean>() {
          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
              Boolean newValue) {
            if (newValue == false){
              System.out.println("STOPPED: " + transcoder.getId());
              transcoder.stopTranscoding();
              transcoderArrayList.remove(transcoder);
              updateStatus(transcoder.getId(),false, 0);
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

  private void updateStatus(int id, boolean status, int pid) {
    PreparedStatement ps;
    String query = "UPDATE streams set enc_status=?, pid=? WHERE id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setBoolean(1, status);
      ps.setInt(2, pid);
      ps.setInt(3, id);
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
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
        if (transcoder.getId() == id) {
          transcoder.stopTranscoding();
          updateStatus(transcoder.getId(), false, transcoder.getPid());
        }
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

  public JSONObject getActiveStreams(){
    JSONObject streams = new JSONObject();
    for (Transcoder transcoder : this.transcoderArrayList){
      streams.put(String.valueOf(transcoder.getId()), transcoder.getStream().getName());
    }

    return streams;
  }
}
