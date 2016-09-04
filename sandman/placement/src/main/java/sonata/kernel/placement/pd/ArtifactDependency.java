package sonata.kernel.placement.pd;

import org.apache.log4j.Logger;

import sonata.kernel.placement.TranslatorCore;

public class ArtifactDependency {

	final Logger logger = Logger.getLogger(ArtifactDependency.class);
	private String name;
    private String url;
    private String md5;
    private String credentials;


    public String getName() {
    	logger.info("Artifact Dependency Name" + name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
    	logger.info("Artifact Dependency Url" + url);
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
    	logger.info("Artifact Dependency MD5 hash" + md5);
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCredentials() {
    	logger.info("Artifact Dependency credentials" + credentials);
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
