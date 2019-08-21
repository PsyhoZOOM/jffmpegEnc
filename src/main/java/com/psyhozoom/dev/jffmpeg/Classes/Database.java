package com.psyhozoom.dev.jffmpeg.Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  public Connection conn;
  private String url;
  boolean databaseConnected = false;

  public boolean initDatabases() {
    url = "jdbc:sqlite:/home/zoom/public_html/zfSite/skeleton-application/data/data.db";
    try {
      conn = DriverManager.getConnection(url);
      databaseConnected = true;
    } catch (SQLException e) {
      databaseConnected = false;
      e.printStackTrace();
    }
    return databaseConnected;
  }
}
