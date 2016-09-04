package sonata.kernel.placement.pd;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.placement.TranslatorCore;

public class PackageDependency {

	final static Logger logger = Logger.getLogger(TranslatorCore.class);
    private String group;
    private String name;
    private String version;
    private String credentials;
    @JsonProperty("verification_key")
    private String verificationKey;


    public String getGroup() {
    	logger.info("Group "+ group);
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
    	logger.info("Name "+ name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
    	logger.info("Version "+ version);
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCredentials() {
    	logger.info("Credentials "+ credentials);
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getVerificationKey() {
    	logger.info("Verfication Key "+ verificationKey);
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }
}
