package sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.authenticationv3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationDataV3 {

  Token token;

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }
}
