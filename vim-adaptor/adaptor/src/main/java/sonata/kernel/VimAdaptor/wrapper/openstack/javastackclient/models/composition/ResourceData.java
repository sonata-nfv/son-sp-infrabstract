package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.composition;


public class ResourceData<T> {
  Resource<T> resource;

  public Resource<T> getResource() {
    return resource;
  }

  public void setResource(Resource<T> resource) {
    this.resource = resource;
  }

}
