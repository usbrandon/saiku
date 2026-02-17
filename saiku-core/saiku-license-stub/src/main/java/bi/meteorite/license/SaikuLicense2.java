package bi.meteorite.license;

/**
 * Stub Saiku License v2 implementation.
 * Extends SaikuLicense with additional fields for v2 format.
 */
public class SaikuLicense2 extends SaikuLicense {

    private String memoryLimit;
    private String processorLimit;

    public SaikuLicense2() {
        super();
        setLicenseType("COMMUNITY_V2");
    }

    public SaikuLicense2(String licenseString) throws LicenseException {
        super(licenseString);
        setLicenseType("COMMUNITY_V2");
    }

    public String getMemoryLimit() {
        return memoryLimit != null ? memoryLimit : "UNLIMITED";
    }

    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getProcessorLimit() {
        return processorLimit != null ? processorLimit : "UNLIMITED";
    }

    public void setProcessorLimit(String processorLimit) {
        this.processorLimit = processorLimit;
    }
}
