package com.psyhozoom.dev.jffmpeg;

import com.psyhozoom.dev.jffmpeg.Process.Transcoder;
import com.psyhozoom.dev.jffmpeg.Services.Server;

public class Main {
  public static void main(String args[]) {

    // Transcoder transcoder = new
    // Transcoder("http://iptv1.yuvideo.net:4010/udp/239.255.4.98:9008",
    // "h264_nvenc","aac", "copy", "192k", "2000k", "udp://239.100.100.100:9000");
    // transcoder.startTranscoding();

    Server server = new Server();
    server.startServer();

  }

}
