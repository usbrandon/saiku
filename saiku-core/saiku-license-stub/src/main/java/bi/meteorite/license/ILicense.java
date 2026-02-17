package bi.meteorite.license;

import java.io.Serializable;
import java.util.Date;

/**
 * Stub interface for license information.
 */
public interface ILicense extends Serializable {

    String getName();

    String getEmail();

    String getHostname();

    String getLicenseType();

    Date getExpiration();

    int getUserLimit();

    String getVersion();

    boolean isExpired();
}
