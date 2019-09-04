package com.psyhozoom.dev.jffmpeg.Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
      flushTables();
    } catch (SQLException e) {
      databaseConnected = false;
      e.printStackTrace();
    }
    return databaseConnected;
  }

  private void flushTables() {
    PreparedStatement ps;
    try {
      ps = conn.prepareStatement("UPDATE streams set enc_status=false");
      ps.executeUpdate();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
