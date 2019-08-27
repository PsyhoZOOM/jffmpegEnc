package com.psyhozoom.dev.jffmpeg.Process;

import com.psyhozoom.dev.jffmpeg.Classes.FFLog;
import com.psyhozoom.dev.jffmpeg.Classes.Streams;
import com.psyhozoom.dev.jffmpeg.Classes.StreamsOut;
import com.psyhozoom.dev.jffmpeg.Classes.SystemStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Transcoder {

//  private boolean running = false;

  private Process process;
  private Streams stream;
  private int id;
  private String[] messages = new String[50];
  private BooleanProperty running = new SimpleBooleanProperty(true);
  private FFLog ffLog = new FFLog();

  public Transcoder() {
  }


  public BooleanProperty getRunning() {
    return running;
  }

  public BooleanProperty runningProperty() {
    return running;
  }


  public String getBitrate() {
    return String.format("%s %s",ffLog.getBitrate(), ffLog.getTime());
  }

  public boolean startTranscoding(String params) {
    BufferedReader input = null;
    ProcessBuilder processBuilder = null;
    running.setValue(true);
    int i = 0; //50 lines of messages

    try {
      //    process = Runtime.getRuntime().exec(params);
      //process = Runtime.getRuntime().exec("ffprobe http://iptv1.yuvideo.net:4010/udp/239.255.4.98:9008 ");
      processBuilder = new ProcessBuilder("bash", "-c", params);
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();
      input = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;


      while (process.isAlive()  || running.getValue()){

        if (!process.isAlive()){
          running.setValue(false);
          break;
        }
        line = input.readLine();
        if (i > 49) {
          i = 0;
        }
        messages[i] = line;
       // System.out.println(line);
        showLogBw(line);
        i++;
      }

      running.setValue(false);
    } catch (IOException e) {
      running.setValue(false);
      e.printStackTrace();
    }finally {
      try {
        input.close();
        System.out.println(messages[i]);
        running.setValue(false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private void showLogBw(String line) {
    String[] splited = line.split(" ");
    for (String s : splited){
      if (s.contains("bitrate")) {
        System.out.println(s);
        this.ffLog.setBitrate(s);
      }
      if (s.contains("out_time=")){
        this.ffLog.setTime(s);
      }
    }
  }

  public boolean stopTranscoding() {
    process.destroy();

    return true;
  }

  public void setStream(Streams stream) {
    this.stream = stream;
    this.id = stream.getId();
  }

  public int getId() {
    return id;
  }

  public Streams getStream() {
    return stream;
  }

  public String[] getMessages() {
    return messages;
  }

  public void startTranscodingStream() {
    String src = stream.getSource();
    String codec = new String();
    for (StreamsOut streamsOut : stream.getStreamsOuts()) {
      String scale = "";
      if (streamsOut.isScale()) {
        scale = String.format("%sx%s", streamsOut.getScaleW(), streamsOut.getScaleH());
      }
      codec += String.format(
          "-c:v %s -b:v %s -vf scale=%s -c:a %s -b:a %s -c:s copy -minrate %s -maxrate %s -muxrate %s -f %s %s ",
          streamsOut.getVCodec(),
          streamsOut.getVBitrate(),
          scale,
          streamsOut.getACodec(),
          streamsOut.getABitrate(),
          streamsOut.getMinRate(),
          streamsOut.getMaxRate(),
          streamsOut.getMuxRate(),
          streamsOut.getFormat(),
          streamsOut.getDest()
      );
    }
 //   src = String.format("http://iptv1.yuvideo.net:4010/udp/%s", stream.getSource());
    String in = String.format("ffmpeg -loglevel quiet -i %s %s -progress - ", stream.getSource(), codec);

 //   System.out.println(in);

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        startTranscoding(in);

      }
    });
    thread.start();


  }
}
