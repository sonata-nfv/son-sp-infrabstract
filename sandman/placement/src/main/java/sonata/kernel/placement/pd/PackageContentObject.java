package sonata.kernel.placement.pd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackageContentObject {

    private String name;
    @JsonProperty("content-type")
    private String contentType;
    private String md5;
    private String sealed;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSealed() {
        return sealed;
    }

    public void setSealed(String sealed) {
        this.sealed = sealed;
    }
}
