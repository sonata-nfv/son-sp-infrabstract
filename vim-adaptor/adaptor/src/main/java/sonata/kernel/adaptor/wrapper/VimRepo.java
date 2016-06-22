/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */

package sonata.kernel.adaptor.wrapper;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class VimRepo {


  private final static String configFilePath = "/etc/son-mano/postgres.config";
  private Properties prop;

  /**
   * Create the a VimRepo that read from the config file, connect to the database, and if needed
   * creates the tables.
   * 
   */
  public VimRepo() {
    this.prop = this.parseConfigFile();

    Connection connection = null;
    Statement findDatabaseStmt = null;
    PreparedStatement findTablesStmt = null;
    Statement createDatabaseStmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String dbUrl = "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
        + prop.getProperty("repo_port") + "/" + "postgres";
    String user = prop.getProperty("user");
    String pass = prop.getProperty("pass");
    System.out.println("[VimRepo] Connecting to postgresql at " + dbUrl);
    boolean errors = false;
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(dbUrl, user, pass);
      boolean isDatabaseSet = false;
      System.out.println("[VimRepo] Connection opened successfully. Listing databases...");
      String sql;
      sql = "SELECT datname FROM pg_catalog.pg_database;";
      findDatabaseStmt = connection.createStatement();
      rs = findDatabaseStmt.executeQuery(sql);
      while (rs.next()) {
        String datname = rs.getString("datname");
        if (datname.equals("vimregistry") || datname.equals("VIMREGISTRY")) {
          isDatabaseSet = true;
        }
      }
      rs.close();

      if (!isDatabaseSet) {
        System.out.println("[VimRepo] Database not set. Creating database...");
        sql = "CREATE DATABASE vimregistry;";
        stmt = connection.createStatement();
        stmt.execute(sql);
        sql = "GRANT ALL PRIVILEGES ON DATABASE vimregistry TO " + user + ";";
        createDatabaseStmt = connection.createStatement();

        System.out.println("[VimRepo] Statement:" + createDatabaseStmt.toString());
        createDatabaseStmt.execute(sql);
      } else {
        System.out.println("[VimRepo] Database already set.");
      }
      connection.close();

      // reconnect to the new database;

      dbUrl = "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
          + prop.getProperty("repo_port") + "/" + "vimregistry";
      System.out.println("[VimRepo] Connecting to the new database: " + dbUrl);
      connection = DriverManager.getConnection(dbUrl, user, pass);


      boolean isEnvironmentSet = false;
      sql = "SELECT * FROM pg_catalog.pg_tables WHERE tableowner=?;";
      findTablesStmt = connection.prepareStatement(sql);
      findTablesStmt.setString(1, user);
      rs = findTablesStmt.executeQuery();
      while (rs.next()) {
        String tablename = rs.getString("tablename");
        if (tablename.equals("vim") || tablename.equals("VIM") || tablename.equals("instances")
            || tablename.equals("INSTANCES")) {
          isEnvironmentSet = true;
          break;
        }
      }
      if (stmt != null) {
        stmt.close();
      }
      if (!isEnvironmentSet) {
        stmt = connection.createStatement();
        sql = "CREATE TABLE vim " + "(UUID TEXT PRIMARY KEY NOT NULL," + " TYPE TEXT NOT NULL,"
            + " VENDOR TEXT NOT NULL," + " ENDPOINT TEXT NOT NULL," + " USERNAME TEXT NOT NULL,"
            + " TENANT TEXT NOT NULL," + " TENANT_EXT_NET TEXT NOT NULL," + " TENANT_EXT_ROUTER TEXT NOT NULL," + " PASS TEXT,"
            + " AUTHKEY TEXT);";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE instances " + "(INSTANCE_UUID TEXT PRIMARY KEY NOT NULL,"
            + " VIM_UUID TEXT NOT NULL," + " VIM_NAME TEXT NOT NULL);";
        stmt.executeUpdate(sql);

      }

    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      errors = true;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      errors = true;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());

      }
    }
    if (!errors) {
      System.out.println("[VimRepo] Environment created successfully");
    } else {
      System.out.println("[VimRepo] Errors creating the environment");
    }
    return;
  }


  /**
   * Write the wrapper record into the repository with the specified UUID.
   * 
   * @param uuid the UUID of the wrapper to store
   * @param record the WrapperRecord object with the information on the wrapper to store
   * 
   * @return true for process success
   */
  public boolean writeVimEntry(String uuid, WrapperRecord record) {
    boolean out = true;

    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      String sql =
          "INSERT INTO VIM (UUID, TYPE, VENDOR, ENDPOINT, USERNAME, TENANT, TENANT_EXT_NET, TENANT_EXT_ROUTER, PASS, AUTHKEY) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, uuid);
      stmt.setString(2, record.getConfig().getWrapperType());
      stmt.setString(3, record.getConfig().getVimVendor());
      stmt.setString(4, record.getConfig().getVimEndpoint().toString());
      stmt.setString(5, record.getConfig().getAuthUserName());
      stmt.setString(6, record.getConfig().getTenantName());
      stmt.setString(7, record.getConfig().getTenantExtNet());
      stmt.setString(8, record.getConfig().getTenantExtRouter());
      stmt.setString(9, record.getConfig().getAuthPass());
      stmt.setString(10, record.getConfig().getAuthKey());

      stmt.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;
      }
    }
    System.out.println("[VimRepo] Records created successfully");

    return out;
  }

  /**
   * Remove the wrapper identified by the specified UUID from the repository.
   * 
   * @param uuid the UUID of the wrapper to remove
   * 
   * @return true for process success
   */
  public boolean removeVimEntry(String uuid) {
    boolean out = true;
    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      String sql = "DELETE from VIM where UUID=?;";
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, uuid);
      stmt.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;

      }
    }
    System.out.println("Operation done successfully");
    return out;

  }

  /**
   * update the wrapper record into the repository with the specified UUID.
   * 
   * @param uuid the UUID of the wrapper to update
   * @param record the WrapperRecord object with the information on the wrapper to store
   * 
   * @return true for process success
   */
  public boolean updateVimEntry(String uuid, WrapperRecord record) {
    boolean out = true;

    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);


      String sql =
          "UPDATE VIM set (TYPE, VENDOR, ENDPOINT, USERNAME, TENANT, TENANT_EXT_NET, TENANT_EXT_ROUTER, PASS, AUTHKEY) "
              + "VALUES (?,?,?,?,?,?,?,?,?) WHERE UUID=?;";

      stmt = connection.prepareStatement(sql);
      stmt.setString(1, record.getConfig().getWrapperType());
      stmt.setString(2, record.getConfig().getVimVendor());
      stmt.setString(3, record.getConfig().getVimEndpoint().toString());
      stmt.setString(4, record.getConfig().getAuthUserName());
      stmt.setString(5, record.getConfig().getTenantName());
      stmt.setString(6, record.getConfig().getTenantExtNet());
      stmt.setString(7, record.getConfig().getTenantExtRouter());
      stmt.setString(8, record.getConfig().getAuthPass());
      stmt.setString(9, record.getConfig().getAuthKey());
      stmt.setString(10, uuid);


      stmt.executeUpdate(sql);
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;

      }
    }
    System.out.println("[VimRepo] Records created successfully");

    return out;
  }

  /**
   * Retrieve the wrapper record with the specified UUID from the repository.
   * 
   * @param uuid the UUID of the wrapper to retrieve
   * 
   * @return the WrapperRecord representing the wrapper, null if the wrapper is not registere in the
   *         repository
   */
  public WrapperRecord readVimEntry(String uuid) {

    WrapperRecord output = null;

    Connection connection = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      stmt = connection.prepareStatement("SELECT * FROM VIM WHERE UUID=?;");
      stmt.setString(1, uuid);
      rs = stmt.executeQuery();

      if (rs.next()) {
        String wrapperType = rs.getString("TYPE");
        String vendor = rs.getString("VENDOR");
        String urlString = rs.getString("ENDPOINT");
        String user = rs.getString("USERNAME");
        String pass = rs.getString("PASS");
        String tenant = rs.getString("TENANT");
        String key = rs.getString("AUTHKEY");
        String tenantExtNet = rs.getString("TENANT_EXT_NET");
        String tenantExtRouter = rs.getString("TENANT_EXT_ROUTER");
        WrapperConfiguration config = new WrapperConfiguration();
        config.setUuid(uuid);
        config.setWrapperType(wrapperType);
        config.setVimVendor(vendor);
        config.setVimEndpoint(urlString);
        config.setTenantName(tenant);
        config.setAuthUserName(user);
        config.setAuthPass(pass);
        config.setAuthKey(key);
        config.setTenantExtNet(tenantExtNet);
        config.setTenantExtRouter(tenantExtRouter);

        Wrapper wrapper = WrapperFactory.createWrapper(config);
        output = new WrapperRecord(wrapper, config, null);


      } else {
        output = null;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        output = null;

      }
    }
    System.out.println("Operation done successfully");
    return output;

  }


  /**
   * List the compute VIMs stored in the repository.
   * 
   * @return an arraylist of String with the UUID of the registered VIMs, null if error occurs
   */
  public ArrayList<String> getComputeVims() {
    ArrayList<String> out = new ArrayList<String>();

    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      rs = stmt.executeQuery("SELECT * FROM VIM WHERE TYPE='compute';");
      while (rs.next()) {
        String uuid = rs.getString("UUID");
        out.add(uuid);
      }

    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = null;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = null;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());

      }
    }
    System.out.println("Operation done successfully");
    return out;
  }

  /**
   * Get the UUID used by the VIM to identify the given service instance.
   * 
   * @param instanceUuid the instance UUID of the service to retrieve
   * 
   * @return the uuid used by the VIM to identify the service instance
   * 
   */
  public String getServiceVimUuid(String instanceUuid) {

    String output = null;

    Connection connection = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      stmt = connection.prepareStatement("SELECT VIM_UUID FROM INSTANCES WHERE INSTANCE_UUID=?;");
      stmt.setString(1, instanceUuid);
      rs = stmt.executeQuery();

      if (rs.next()) {

        output = rs.getString("VIM_UUID");

      } else {
        output = null;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        output = null;

      }
    }
    if (output != null) {
      System.out.println("Operation done successfully");
    }
    return output;

  }

  /**
   * Get the UUID used by the VIM to identify the given service instance.
   * 
   * @param instanceUuid the instance UUID of the service to remove
   * 
   * @return the logical name used by the VIM to identify the service instance
   * 
   */
  public String getServiceVimName(String instanceUuid) {

    String output = null;

    Connection connection = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      stmt = connection.prepareStatement("SELECT VIM_NAME FROM INSTANCES WHERE INSTANCE_UUID=?;");
      stmt.setString(1, instanceUuid);
      rs = stmt.executeQuery();

      if (rs.next()) {

        output = rs.getString("VIM_NAME");

      } else {
        output = null;
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      output = null;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        output = null;

      }
    }
    if (output != null) {
      System.out.println("Operation done successfully");
    }
    return output;

  }

  /**
   * Write the instance record into the repository.
   * 
   * @param instanceUuid the uuid of the instance in the NSD
   * @param vimUuid the uuid used by the VIM to identify the stack
   * @param vimName the name used by the VIM to identify the stack
   * 
   * @return true for process success
   */
  public boolean writeInstanceEntry(String instanceUuid, String vimUuid, String vimName) {
    boolean out = true;

    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      String sql =
          "INSERT INTO INSTANCES (INSTANCE_UUID, VIM_UUID, VIM_NAME) " + "VALUES (?, ?, ?);";
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, instanceUuid);
      stmt.setString(2, vimUuid);
      stmt.setString(3, vimName);
      stmt.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;
      }
    }
    if (!out) {
      System.out.println("[VimRepo] Records created successfully");
    }

    return out;
  }

  /**
   * Update the instance record into the repository.
   * 
   * @param instanceUuid the uuid of the instance in the NSD
   * @param vimUuid the uuid used by the VIM to identify the stack
   * @param vimName the name used by the VIM to identify the stack
   * 
   * @return true for process success
   */
  public boolean updateInstanceEntry(String instanceUuid, String vimUuid, String vimName) {
    boolean out = true;

    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      String sql =
          "UPDATE INSTANCES set (VIM_UUID, VIM_NAME) " + "VALUES (?, ?) WHERE INSTANCE_UUID=?;";
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, vimUuid);
      stmt.setString(2, vimName);
      stmt.setString(3, instanceUuid);
      stmt.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;
      }
    }
    if (!out) {
      System.out.println("[VimRepo] Records created successfully");
    }

    return out;
  }


  /**
   * delete the instance record into the repository.
   * 
   * @param instanceUuid the uuid of the instance in the NSD
   * 
   * @return true for process success
   */
  public boolean removeInstanceEntry(String instanceUuid) {
    boolean out = true;

    Connection connection = null;
    PreparedStatement stmt = null;
    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + prop.getProperty("repo_host") + ":"
                  + prop.getProperty("repo_port") + "/" + "vimregistry",
              prop.getProperty("user"), prop.getProperty("pass"));
      connection.setAutoCommit(false);

      String sql = "DELETE FROM INSTANCES WHERE INSTANCE_UUID=?;";
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, instanceUuid);
      stmt.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      out = false;
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        out = false;
      }
    }
    if (!out) {
      System.out.println("[VimRepo] Records created successfully");
    }

    return out;
  }

  private Properties parseConfigFile() {
    Properties prop = new Properties();
    try {
      InputStreamReader in =
          new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8"));

      JSONTokener tokener = new JSONTokener(in);

      JSONObject jsonObject = (JSONObject) tokener.nextValue();

      String repoUrl = jsonObject.getString("repo_host");
      String repoPort = jsonObject.getString("repo_port");
      String user = jsonObject.getString("user");
      String pass = jsonObject.getString("pass");
      prop.put("repo_host", repoUrl);
      prop.put("repo_port", repoPort);
      prop.put("user", user);
      prop.put("pass", pass);
    } catch (FileNotFoundException e) {
      System.err.println("Unable to load Postregs Config file");
    }

    return prop;
  }

}
