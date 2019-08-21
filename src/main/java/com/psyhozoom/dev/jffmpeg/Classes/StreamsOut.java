package com.psyhozoom.dev.jffmpeg.Classes;

public class StreamsOut {
  int id;
  private String VCodec;
  private String ACodec;
  private String VBitrate;
  private String ABitrate;
  private String SCodec;
  private String MinRate;
  private String MaxRate;
  private String MuxRate;
  private boolean scale =false;
  private String scaleW;
  private String scaleH;
  private String format;
  private String dest;
  private int streamID;


  public int getStreamID() {
    return streamID;
  }

  public void setStreamID(int streamID) {
    this.streamID = streamID;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getVCodec() {
    return VCodec;
  }

  public void setVCodec(String VCodec) {
    this.VCodec = VCodec;
  }

  public String getACodec() {
    return ACodec;
  }

  public void setACodec(String ACodec) {
    this.ACodec = ACodec;
  }

  public String getVBitrate() {
    return VBitrate;
  }

  public void setVBitrate(String VBitrate) {
    this.VBitrate = VBitrate;
  }

  public String getABitrate() {
    return ABitrate;
  }

  public void setABitrate(String ABitrate) {
    this.ABitrate = ABitrate;
  }

  public String getSCodec() {
    return SCodec;
  }

  public void setSCodec(String SCodec) {
    this.SCodec = SCodec;
  }

  public String getMinRate() {
    return MinRate;
  }

  public void setMinRate(String minRate) {
    MinRate = minRate;
  }

  public String getMaxRate() {
    return MaxRate;
  }

  public void setMaxRate(String maxRate) {
    MaxRate = maxRate;
  }

  public String getMuxRate() {
    return MuxRate;
  }

  public void setMuxRate(String muxRate) {
    MuxRate = muxRate;
  }

  public boolean isScale() {
    return scale;
  }

  public void setScale(boolean scale) {
    this.scale = scale;
  }

  public String getScaleW() {
    return scaleW;
  }

  public void setScaleW(String scaleW) {
    this.scaleW = scaleW;
  }

  public String getScaleH() {
    return scaleH;
  }

  public void setScaleH(String scaleH) {
    this.scaleH = scaleH;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getDest() {
    return dest;
  }

  public void setDest(String dest) {
    this.dest = dest;
  }
}
