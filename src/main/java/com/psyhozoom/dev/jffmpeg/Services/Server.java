package com.psyhozoom.dev.jffmpeg.Services;

import com.psyhozoom.dev.jffmpeg.Classes.Database;
import com.psyhozoom.dev.jffmpeg.Classes.SystemStatus;
import com.psyhozoom.dev.jffmpeg.Process.StreamThreads;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;

public class Server {
  private ServerSocket socket;
  private Database db;
  private StreamThreads streamThreads;
  private SystemStatus systemStatus;


  public Server() {
    db = new Database();
    db.initDatabases();
    streamThreads = new StreamThreads(db);
    systemStatus = new SystemStatus();
    systemStatus.start();
  }

  public void startServer(){
    try {
      socket = new ServerSocket(10020);
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true){
      try {
        System.out.println("listening..");
        ServerWorker serverWorker = new ServerWorker(socket.accept(), streamThreads, systemStatus, db);
        Thread th = new Thread(serverWorker);
        th.start();
        System.out.println(LocalDateTime.now() + " client Connected");
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }


}
