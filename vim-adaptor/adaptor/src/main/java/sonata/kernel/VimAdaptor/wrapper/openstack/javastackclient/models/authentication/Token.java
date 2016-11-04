package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.authentication;

import java.util.List;


public class Token {

  private String id;
  private Tenant tenant;
  private String issued_at;
  private String expires;
  private List<String> audit_ids;


  public Tenant getTenant() {
    return this.tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIssued_at() {
    return issued_at;
  }

  public void setIssued_at(String issued_at) {
    this.issued_at = issued_at;
  }

  public String getExpires() {
    return expires;
  }

  public void setExpires(String expires) {
    this.expires = expires;
  }

  public List<String> getAudit_ids() {
    return audit_ids;
  }

  public void setAudit_ids(List<String> audit_ids) {
    this.audit_ids = audit_ids;
  }
}
