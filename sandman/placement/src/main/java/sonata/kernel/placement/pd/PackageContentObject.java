package sonata.kernel.placement.pd;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.placement.TranslatorCore;

public class PackageContentObject {

	final static Logger logger = Logger.getLogger(PackageContentObject.class);
    private String name;
    @JsonProperty("content-type")
    private String contentType;
    private String md5;
    private String sealed;


    public String getName() {
    	logger.info("Name "+ name);
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
    	logger.info("Content Type" + contentType);
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMd5() {
    	logger.info("MD5 hash "+ md5);
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSealed() {
    	logger.info("Sealed "+ sealed);
        return sealed;
    }

    public void setSealed(String sealed) {
        this.sealed = sealed;
    }
}
