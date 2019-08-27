package com.psyhozoom.dev.jffmpeg.Classes;


import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import oshi.hardware.NetworkIF;
import oshi.hardware.Networks;
import oshi.hardware.platform.linux.LinuxNetworks;
import sun.util.resources.cldr.en.CalendarData_en_MU;

public class SystemStatus {

  public static void getNetInfo(){

    Enumeration<NetworkInterface> networkInterfaces = null;
    try {
      networkInterfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      e.printStackTrace();
    }




    for (NetworkInterface netIf : Collections.list(networkInterfaces)){
      NetworkIF networkIF = new NetworkIF();
      networkIF.setNetworkInterface(netIf);
      networkIF.updateAttributes();
      String name = networkIF.getName();
      Long in = networkIF.getBytesRecv();
      Long out = networkIF.getBytesSent();

      String msg = String.format("Interface: %s, Bytes In: %d Out: %d", name, in, out);
      System.out.println(msg);
    }
  }

}
