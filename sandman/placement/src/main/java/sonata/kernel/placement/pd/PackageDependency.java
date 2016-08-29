package sonata.kernel.placement.pd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackageDependency {

    private String group;
    private String name;
    private String version;
    private String credentials;
    @JsonProperty("verification_key")
    private String verificationKey;


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }
}
