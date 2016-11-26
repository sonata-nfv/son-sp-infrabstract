package sonata.kernel.placement.pd;


import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.placement.TranslatorCore;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PackageDescriptor {
	final static Logger logger = Logger.getLogger(TranslatorCore.class);

    @JsonProperty("descriptor_version")
    private String descriptorVersion;
    private String schema;

    private String name;
    private String version;
    private String vendor;

    private String maintainer;

    private String description;
    private String md5;
    private String signature;
    private String sealed;
    @JsonProperty("entry_service_template")
    private String entryServiceTemplate;

    @JsonProperty("package_content")
    private ArrayList<PackageContentObject> packageContent;

    @JsonProperty("package_resolvers")
    private ArrayList<PackageResolver> packageResolvers;

    @JsonProperty("package_dependencies")
    private ArrayList<PackageDependency> packageDependencies;

    @JsonProperty("artifact_dependencies")
    private ArrayList<ArtifactDependency> artifactDependencies;


    public String getDescriptorVersion() {
    	logger.info("Descriptor version "+ descriptorVersion);
        return descriptorVersion;
    }

    public void setDescriptorVersion(String descriptorVersion) {
        this.descriptorVersion = descriptorVersion;
    }

    public String getSchema() {
    	logger.info("Schema "+ schema);
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

    public String getVendor() {
    	logger.info("Vendor "+ vendor);
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getMaintainer() {
    	logger.info("Maintainer "+ maintainer);
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getDescription() {
    	logger.info("Description "+ description);
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMd5() {
    	logger.info("MD5 hash "+ md5);
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSignature() {
    	logger.info("Signature "+ signature);
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSealed() {
    	logger.info("Sealed "+ sealed);
        return sealed;
    }

    public void setSealed(String sealed) {
        this.sealed = sealed;
    }

    public String getEntryServiceTemplate() {
    	logger.info("Entry service template "+ entryServiceTemplate);
        return entryServiceTemplate;
    }

    public void setEntryServiceTemplate(String entryServiceTemplate) {
        this.entryServiceTemplate = entryServiceTemplate;
    }

    public ArrayList<PackageContentObject> getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(ArrayList<PackageContentObject> packageContent) {
        this.packageContent = packageContent;
    }

    public ArrayList<PackageResolver> getPackageResolvers() {
        return packageResolvers;
    }

    public void setPackageResolvers(ArrayList<PackageResolver> packageResolvers) {
        this.packageResolvers = packageResolvers;
    }

    public ArrayList<PackageDependency> getPackageDependencies() {
        return packageDependencies;
    }

    public void setPackageDependencies(ArrayList<PackageDependency> packageDependencies) {
        this.packageDependencies = packageDependencies;
    }

    public ArrayList<ArtifactDependency> getArtifactDependencies() {
        return artifactDependencies;
    }

    public void setArtifactDependencies(ArrayList<ArtifactDependency> artifactDependencies) {
        this.artifactDependencies = artifactDependencies;
    }
}
