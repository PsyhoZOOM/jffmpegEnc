package com.psyhozoom.dev.jffmpeg.Classes;


import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Enumeration;
import oshi.hardware.NetworkIF;

public class SystemStatus {

  public static void getNetInfo() throws SocketException {


    NetworkIF networkIF = new NetworkIF();

      networkIF.setNetworkInterface(NetworkInterface.getByIndex(2));

      long in = 0;
      long out = 0;

        networkIF.updateAttributes();
        in = networkIF.getBytesRecv();
        out = networkIF.getBytesSent();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    networkIF.updateAttributes();
    in = networkIF.getBytesRecv() - in;
    out = networkIF.getBytesSent() - out;


      String name = networkIF.getName();
      String msg = String.format("Interface: %s, Bytes In: %d Out: %d", name, in, out);
      System.out.println(msg);
  }

  public static void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          try {
            SystemStatus.getNetInfo();
          } catch (SocketException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }
}
