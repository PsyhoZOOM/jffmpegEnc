package com.psyhozoom.dev.jffmpeg.Classes;

import com.psyhozoom.dev.jffmpeg.Process.StreamThreads;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Streams {

  private ArrayList<StreamsOut> streamsOuts = new ArrayList<>();
  private int id;
  private String source;
  private String name;
  private Database db;


  private boolean error;
  private String errorMsg;

  public Streams(Database db) {
    this.db = db;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<StreamsOut> getStreamsOuts() {
    return streamsOuts;
  }

  public void setStreamsOuts(ArrayList<StreamsOut> streamsOuts) {
    this.streamsOuts = streamsOuts;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void saveStream(JSONObject stream) {
    Streams streamMain = new Streams(db);
    streamMain.setName(stream.getString("streamName"));
    streamMain.setSource(stream.getString("streamSource"));

    //check if source exist. Save if not
    if (checkSourceIfExist(streamMain.getSource())) {
      error = true;
      errorMsg = "Source Exists";
      return;
    }
    for (Object arr : stream.getJSONArray("streams")) {

      JSONObject obj = (JSONObject) arr;
      StreamsOut streamsOut = new StreamsOut();
      try {
        streamsOut.setVCodec(obj.getString("VCodec"));
        streamsOut.setACodec(obj.getString("ACodec"));
        streamsOut.setVBitrate(obj.getString("VBitRate"));
        streamsOut.setABitrate(obj.getString("ABitRate"));
        streamsOut.setSCodec(obj.getString("SCodec"));
        streamsOut.setMinRate(obj.getString("MinRate"));
        streamsOut.setMaxRate(obj.getString("MaxRate"));
        streamsOut.setMuxRate(obj.getString("MuxRate"));
        streamsOut.setFormat(obj.getString("Format"));
        streamsOut.setDest(obj.getString("Dest"));
        if (obj.has("Scale")) {
          streamsOut.setScale(true);
          streamsOut.setScaleW(obj.getString("ScaleW"));
          streamsOut.setScaleH(obj.getString("ScaleH"));

        } else {
          streamsOut.setScale(false);
          streamsOut.setScaleH("0");
          streamsOut.setScaleW("0");
        }
      } catch (JSONException ex) {
        ex.printStackTrace();
      } finally {
        System.out.println(stream);
      }

      streamMain.getStreamsOuts().add(streamsOut);

    }

    saveStreams(streamMain);

  }

  private boolean checkSourceIfExist(String source) {
    PreparedStatement ps = null;
    ResultSet rs;
    String query = "SELECT name FROM streams WHERE name=?";
    boolean exist = false;
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, source);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        exist = true;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return exist;
  }

  private void saveStreams(Streams streamMain) {
    int streamID = 0;
    PreparedStatement ps = null;
    String query = "INSERT INTO streams (name, src, desc) VALUES (?,?,?)";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, streamMain.getName());
      ps.setString(2, streamMain.getSource());
      ps.setString(3, "");
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    //get saved id of stream
    streamID = getIdOfStream(streamMain.getSource());

    //if id is 0 that mean it is error. return back and show user error message
    if (streamID == 0) {
      return;
    }

    for (StreamsOut streams : streamMain.getStreamsOuts()) {
      query = "INSERT INTO streamsOut (dst, vcodec, acodec, scodec, vbitrate, abitrate, muxrate, minrate, maxrate, format, streamID, scale) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

      try {
        ps = db.conn.prepareStatement(query);
        ps.setString(1, streams.getDest());
        ps.setString(2, streams.getVCodec());
        ps.setString(3, streams.getACodec());
        ps.setString(4, streams.getSCodec());
        ps.setString(5, streams.getVBitrate());
        ps.setString(6, streams.getABitrate());
        ps.setString(7, streams.getMuxRate());
        ps.setString(8, streams.getMinRate());
        ps.setString(9, streams.getMaxRate());
        ps.setString(10, streams.getFormat());
        ps.setInt(11, streamID);
        ps.setString(12, String.format("%sx%s", streams.getScaleW(), streams.getScaleH()));
        ps.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }


  }

  private int getIdOfStream(String source) {
    PreparedStatement ps = null;
    ResultSet rs;
    String query = "SELECT id from streams WHERE name=?";
    int id = 0;
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, source);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        rs.next();
        id = rs.getInt("id");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return id;
  }

  public JSONObject getAllStreamsOut() {
    JSONObject object = new JSONObject();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT * FROM streams";

    try {
      ps = db.conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          object.put(rs.getString("name"), getArrayStreams(rs.getInt("id"), db));

        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return object;
  }

  private JSONArray getArrayStreams(int id, Database db) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    JSONArray jsonArray = new JSONArray();
    String query = "SELECT * FROM streamsOut WHERE streamID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          JSONObject object = new JSONObject();
          object.put("id", rs.getInt("id"));
          object.put("dst", rs.getString("dst"));
          object.put("vcodec", rs.getString("vcodec"));
          object.put("acodec", rs.getString("acodec"));
          object.put("scodec", rs.getString("scodec"));
          object.put("vbitarate", rs.getString("vbitrate"));
          object.put("abitrate", rs.getString("abitrate"));
          object.put("muxrate", rs.getString("muxrate"));
          object.put("minrate", rs.getString("minrate"));
          object.put("maxrate", rs.getString("maxrate"));
          object.put("format", rs.getString("format"));
          object.put("scale", rs.getString("scale"));
          object.put("streamID", rs.getInt("streamID"));
          object.put("uniqueID", id);
          jsonArray.put(object);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return jsonArray;
  }

  public void startStream(int uniqueID, Database db,
      StreamThreads streamThreads) {
    this.db = db;

    getStream(uniqueID);
    streamThreads.startStream(this);

    /*
    getStream(getIdOfStream(uniqueID));
    Transcoder transcoder = new Transcoder();
    transcoder.setStream(this);
    transcoder.startTranscodingStream();
    */
  }

  public boolean isError() {
    return error;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  /**
   * returning stream with their stremsout
   *
   * @param id id of stream
   */
  public void getStream(int id) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT * FROM streams WHERE id=?";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        rs.next();
        setId(rs.getInt("id"));
        setSource(rs.getString("src"));
        setName(rs.getString("name"));
        setStreamsOuts(getStreamOut(id));

      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

  private ArrayList<StreamsOut> getStreamOut(int id) {
    ArrayList<StreamsOut> streamsOutArrayList = new ArrayList<>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String query = "SELECT * FROM streamsOut WHERE streamID=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, id);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          StreamsOut streamsOut = new StreamsOut();
          streamsOut.setId(rs.getInt("id"));
          streamsOut.setDest(rs.getString("dst"));
          streamsOut.setVCodec(rs.getString("vcodec"));
          streamsOut.setACodec(rs.getString("acodec"));
          streamsOut.setSCodec(rs.getString("scodec"));
          streamsOut.setVBitrate(rs.getString("vbitrate"));
          streamsOut.setABitrate(rs.getString("abitrate"));
          streamsOut.setMuxRate(rs.getString("muxrate"));
          streamsOut.setMinRate(rs.getString("minrate"));
          streamsOut.setMaxRate(rs.getString("maxrate"));
          streamsOut.setFormat(rs.getString("format"));
          streamsOut.setStreamID(rs.getShort("streamID"));
          String[] scale = rs.getString("scale").split("x");
          if (scale.length > 0) {
            streamsOut.setScale(true);
            streamsOut.setScaleW(scale[0]);
            streamsOut.setScaleH(scale[1]);
          } else {
            streamsOut.setScale(false);
          }
          streamsOutArrayList.add(streamsOut);

        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        ps.close();
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return streamsOutArrayList;
  }

  public void stopStream(int uniqueID,
      StreamThreads streamThreads) {
    streamThreads.stopStream(uniqueID);
  }

  public JSONObject getAllStreams() {
    JSONObject object = new JSONObject();
    PreparedStatement ps = null;
    ResultSet rs;
    String query =  "SELECT * FROM streams";

    try {
      ps = db.conn.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.isBeforeFirst()){
        int i =0;
        while (rs.next()) {
          JSONObject streams = new JSONObject();
          streams.put("id", rs.getInt("id"));
          streams.put("name", rs.getString("name"));
          streams.put("src", rs.getString("src"));
          streams.put("desc", rs.getString("desc"));
          object.put(String.valueOf(i), streams);
          i++;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      setError(true);
      setErrorMsg(e.getMessage());
    }finally {
      if (ps == null) {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return object;
  }

  public JSONObject getStreamsOutOfSrc(int streamID) {
    JSONObject object = new JSONObject();
    PreparedStatement ps;
    ResultSet rs;
    String query = "SELECT * FROM streamsOut WHERE streamID=?";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setInt(1, streamID);
      rs=ps.executeQuery();
      if (rs.isBeforeFirst()){
        int i=0;
        while (rs.next()) {
          JSONObject stream = new JSONObject();
          stream.put("id", rs.getInt("id"));
          stream.put("dst", rs.getString("dst"));
          stream.put("vcodec", rs.getString("vcodec"));
          stream.put("acodec", rs.getString("acodec"));
          stream.put("scodec", rs.getString("scodec"));
          stream.put("vbitrate", rs.getString("vbitrate"));
          stream.put("abitrate", rs.getString("abitrate"));
          stream.put("muxrate", rs.getString("muxrate"));
          stream.put("minrate", rs.getString("minrate"));
          stream.put("maxrate", rs.getString("maxrate"));
          stream.put("format", rs.getString("format"));
          stream.put("streamID", rs.getString("streamID"));
          stream.put("scale", rs.getString("scale"));
          object.put(String.valueOf(i), stream);
          i++;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return object;
  }

  public void addStreamOutToStreams(JSONObject object, int sourceID){
    PreparedStatement ps;
    String query = "INSERT INTO streamsOut (dst, vcodec, acodec, scodec, vbitrate, abitrate, muxrate, minrate, maxrate, format, streamID, scale) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, object.getString("Dest"));
      ps.setString(2, object.getString("VCodec"));
      ps.setString(3, object.getString("ACodec"));
      ps.setString(4, object.getString("SCodec"));
      ps.setString(5, object.getString("VBitRate"));
      ps.setString(6, object.getString("ABitRate"));
      ps.setString(7, object.getString("MuxRate"));
      ps.setString(8, object.getString("MinRate"));
      ps.setString(9, object.getString("MaxRate"));
      ps.setString(10, object.getString("Format"));
      ps.setInt(11, sourceID);
      ps.setString(12, object.getString("ScaleW")+"x"+object.getString("ScaleH"));
      ps.executeUpdate();
    } catch (SQLException e) {
      setErrorMsg(e.getMessage());
      setError(true);
      e.printStackTrace();
    }

  }

  public void updateStreamOut(JSONObject dataObj) {
    PreparedStatement ps = null;
    String query = "UPDATE streamsOut set dst=?, vcodec=?, acodec=?, scodec=?, vbitrate=?, abitrate=?, muxrate=?, minrate=?, maxrate=?, format=?, scale=? WHERE id=?";

    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, dataObj.getString("dst"));
      ps.setString(2, dataObj.getString("vcodec"));
      ps.setString(3, dataObj.getString("acodec"));
      ps.setString(4, dataObj.getString("scodec"));
      ps.setString(5, dataObj.getString("vbitrate"));
      ps.setString(6, dataObj.getString("abitrate"));
      ps.setString(7, dataObj.getString("muxrate"));
      ps.setString(8, dataObj.getString("minrate"));
      ps.setString(9, dataObj.getString("maxrate"));
      ps.setString(10, dataObj.getString("format"));
      String scale="";
      if(dataObj.getBoolean("scale")){
        String w, h;
        w = dataObj.getString("scaleW");
        h = dataObj.getString("scaleH");
        scale = w + "x" + h;

      }
      ps.setString(11, scale);
      ps.setInt(12, dataObj.getInt("streamOutID"));
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }finally {
      if(ps!=null) {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    if (dataObj.has("src")){
      updateSrc(dataObj.getInt("streamID"), dataObj.getString("src"));
    }
  }

  private void updateSrc(int streamID, String src) {
    PreparedStatement ps = null;
    String query = "UPDATE streams set src=? where id=?";
    try {
      ps = db.conn.prepareStatement(query);
      ps.setString(1, src);
      ps.setInt(2, streamID);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }finally {
      if(ps!=null){
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
