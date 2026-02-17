package bi.meteorite.license;

/**
 * Stub exception for expired license versions.
 */
public class LicenseVersionExpiredException extends LicenseException {

    public LicenseVersionExpiredException() {
        super("License version expired - this is a stub implementation");
    }

    public LicenseVersionExpiredException(String message) {
        super(message);
    }
}
