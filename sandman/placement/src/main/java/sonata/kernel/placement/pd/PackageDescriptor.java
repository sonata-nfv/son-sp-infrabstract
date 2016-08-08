package sonata.kernel.placement.pd;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class PackageDescriptor {

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
        return descriptorVersion;
    }

    public void setDescriptorVersion(String descriptorVersion) {
        this.descriptorVersion = descriptorVersion;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSealed() {
        return sealed;
    }

    public void setSealed(String sealed) {
        this.sealed = sealed;
    }

    public String getEntryServiceTemplate() {
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
