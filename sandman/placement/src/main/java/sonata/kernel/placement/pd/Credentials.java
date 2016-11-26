package sonata.kernel.placement.pd;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {
	final Logger logger = Logger.getLogger(Credentials.class);
    // username_and_password case
    protected String username;
    protected String password;


    // public_private_key case
    @JsonProperty("private_key")
    protected String privateKey;
    // also the password attribute already defined above


    public String getUsername() {
    	logger.info("Credentials Username" + username);
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
