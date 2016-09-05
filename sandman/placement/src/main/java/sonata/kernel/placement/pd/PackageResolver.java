package sonata.kernel.placement.pd;

import org.apache.log4j.Logger;

public class PackageResolver {
	final static Logger logger = Logger.getLogger(PackageResolver.class);
    private String name;
    private String credentials;

    public String getName() {
    	logger.info("Name "+ name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredentials() {
    	logger.info("Credentials "+ credentials);
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
