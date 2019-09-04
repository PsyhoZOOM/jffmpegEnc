package com.psyhozoom.dev.jffmpeg.Classes;


import java.net.NetworkInterface;
import java.net.SocketException;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.NetworkIF;

public class SystemStatus {
  private String net;
  private String cpu;
  private String mem;

  private boolean error;
  private String errorMSG;

  int SIZE = 1024*2;



  private void getCpuInfo(){
    SystemInfo systemInfo = new SystemInfo();
    double v = systemInfo.getOperatingSystem().getProcess(664).calculateCpuPercent();
    this.cpu= String.valueOf(v);
  }

  private  void getMemInfo(){
    SystemInfo systemInfo= new SystemInfo();
    long total = systemInfo.getHardware().getMemory().getTotal();
    long available = systemInfo.getHardware().getMemory().getAvailable();
    this.mem = String.format("%d/%d", (total-available)/SIZE, total/SIZE);
  }

  private void getNetInfo() throws SocketException {


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
      this.net= String.format("Interface: %s, Bytes In: %d Out: %d", name, in, out);
 //     System.out.println(net);
  }

  public  void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true){
          try {
            getNetInfo();
            getCpuInfo();
            getMemInfo();
          } catch (SocketException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }


  public String getNet() {
    return net;
  }

  public void setNet(String net) {
    this.net = net;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public String getMem() {
    return mem;
  }

  public void setMem(String mem) {
    this.mem = mem;
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

  public JSONObject getSysInfo(){
    JSONObject object = new JSONObject();
    object.put("net", this.getNet());
    object.put("cpu", this.getCpu());
    object.put("mem", this.getMem());
    return  object;
  }
}
