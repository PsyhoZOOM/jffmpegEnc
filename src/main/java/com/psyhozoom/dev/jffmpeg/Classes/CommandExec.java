package com.psyhozoom.dev.jffmpeg.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class CommandExec {

  private String command;
  private String args;

  private Runtime runtime;
  private BufferedReader bufferedReader;
  private Process process;
  ProcessBuilder processBuilder;

  /**
   * Execute command line
   *
   * @param command example ffmpeg
   * @param args -f codecs
   */
  public CommandExec(String command, String args) {
    this.command = command;
    this.args = args;
  }

  public JSONObject getCodecs() {
    JSONObject object = new JSONObject();
    runtime = Runtime.getRuntime();
    try {
      processBuilder = new ProcessBuilder("bash", "-c", this.command + " " + this.args);
      process = processBuilder.start();
    } catch (IOException e) {
      e.printStackTrace();
    }

    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String read;
    try {

      int i = 0;
      while ((read = bufferedReader.readLine()) != null) {
        //we dont need thrash of ffmpeg help
        if (read.contains("D. =") || read.contains(".E =") || read.contains("--") || read.contains("File formats:"))
          continue;
        read = read.trim();
        String[] encM = read.split(" ");
        String enc  = encM[1];
        System.out.println(enc);
        if(enc.isEmpty())
          continue;

        object.put(String.valueOf(i), enc);
        i++;

      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return object;

  }

  public JSONObject getFormats() {
    return getCodecs();
  }

  public JSONObject getVCodecs() {
    return getCodecs();
  }

  public JSONObject getACodecs() {
    return getCodecs();
  }


}
