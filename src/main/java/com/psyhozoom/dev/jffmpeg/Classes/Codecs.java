package com.psyhozoom.dev.jffmpeg.Classes;

import org.json.JSONObject;

public class Codecs {
  JSONObject VCodecs;
  JSONObject ACodecs;
  JSONObject OUTFormat;
  JSONObject SCodecs;



  /**
   * Filter command ffmpeg -codecs | grep EV.L
   * @return JSONObject
   */
  private JSONObject getVCodecs() {
    CommandExec exec = new CommandExec("ffmpeg ", "-codecs | grep EV.L");
    return exec.getVCodecs();
  }

  private JSONObject getACodecs(){
    CommandExec exec = new CommandExec("ffmpeg ", "-codecs | grep EA.L");
    return exec.getACodecs();
  }

  private JSONObject getFormats(){
    CommandExec exec = new CommandExec("ffmpeg ", "-formats");
    return exec.getFormats();
  }

  public JSONObject getAllACodecs(){
    return getACodecs();
  }

  public JSONObject getAllVCodecs() {
    return getVCodecs();
  }

  public JSONObject getVideoCodecs() {
    return getVCodecs();
  }

  public JSONObject getAudioCodecs(){
    return getACodecs();
  }

  public JSONObject getOUTFormat(){
    return getFormats();
  }
}
