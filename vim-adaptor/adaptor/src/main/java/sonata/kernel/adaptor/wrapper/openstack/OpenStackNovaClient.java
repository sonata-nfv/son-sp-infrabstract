package sonata.kernel.adaptor.wrapper.openstack;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Bruno Vidalenc on 06/01/16.
 * 
 * This class wraps a Nova Client written in python when instantiated the onnection details of the
 * OpenStack instance should be provided
 */
public class OpenStackNovaClient {

  /**
   * 
   */
  private static final String ADAPTOR_NOVA_API_PY = "/adaptor/nova-api.py";

  /**
   * 
   */
  private static final String PYTHON2_7 = "python2.7";

  private String url; // url of the OpenStack Client

  private String userName; // OpenStack Client user

  private String password; // OpenStack Client password

  private String tenantName; // OpenStack tenant name


  /**
   * Construct a new Openstack Nova Client.
   *
   * @param url of the OpenStack endpoint
   * @param userName to log into the OpenStack service
   * @param password to log into the OpenStack service
   * @param tenantName to log into the OpenStack service
   */
  public OpenStackNovaClient(String url, String userName, String password, String tenantName) {
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.tenantName = tenantName;
  }


  /**
   * Get the flavors.
   *
   * @return the flavors
   */
  public ArrayList<Flavor> getFlavors() {

    String string = null;
    Flavor flavor = null;
    String flavorName = null;
    int cpu;
    int ram;
    int disk;
    ArrayList<Flavor> flavors = new ArrayList<Flavor>();
    String[] flavorString;

    System.out.println("Getting flavors");

    try {
      // Call the python client for the flavors of the openstack instance
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_NOVA_API_PY,
          "--configuration", url, userName, password, tenantName, "--flavors");
      Process process = processBuilder.start();

      System.out.println("The available flavors are:");

      // Read the flavors
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      while ((string = stdInput.readLine()) != null) {
        System.out.println(string);
        flavorString = string.split(" ");
        flavorName = flavorString[0];
        cpu = Integer.parseInt(flavorString[2]);
        ram = Integer.parseInt(flavorString[4]);
        disk = Integer.parseInt(flavorString[6]);
        flavor = new Flavor(flavorName, cpu, ram, disk);
        flavors.add(flavor);
      }
      stdInput.close();

    } catch (Exception e) {
      System.out
          .println("Runtime error getting openstack flavors" + " error message: " + e.getMessage());
    }

    return flavors;

  }


  @Override
  public String toString() {
    return "OpenStackNovaClient{" + "url='" + url + '\'' + ", userName='" + userName + '\''
        + ", password='" + password + '\'' + ", tenantName='" + tenantName + '\'' + '}';
  }

}
