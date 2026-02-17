package bi.meteorite.license;

import java.io.Serializable;
import java.util.Date;

/**
 * Stub Saiku license implementation.
 * Returns a perpetual community edition license.
 */
public class SaikuLicense implements ILicense, Serializable {

    private static final long serialVersionUID = 1L;

    private String name = "Community Edition";
    private String email = "community@saiku.org";
    private String hostname = "*";
    private String licenseType = "COMMUNITY";
    private Date expiration = null; // Never expires
    private int userLimit = Integer.MAX_VALUE;
    private String version = "UNLIMITED";

    public SaikuLicense() {
    }

    public SaikuLicense(String licenseString) throws LicenseException {
        // Stub: ignore license string, always return community license
    }

    /**
     * Stub: Community license is always valid, so validation is a no-op.
     */
    public void validate(Date currentDate, String currentVersion,
                         boolean b1, boolean b2, boolean b3, boolean b4)
            throws LicenseException {
        // Community edition: always valid
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getLicenseType() {
        return licenseType;
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }

    @Override
    public int getUserLimit() {
        return userLimit;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isExpired() {
        return false; // Community license never expires
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
