package sonata.kernel.WimAdaptor.wrapper;


public enum WimVendor {
  VTN("VTN"),MOCK("MOCK");

  private final String name;

  WimVendor(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  private String getName() {
    return this.toString();
  }

  public static WimVendor getByName(String name) {
    for (WimVendor vendor : values()) {
      if (vendor.getName().toUpperCase().equals(name.toUpperCase())) {
        return vendor;
      }
    }

    throw new IllegalArgumentException(name + " is not a valid ComputeVimVendor");
  }
}
