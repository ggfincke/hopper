package dev.fincke.hopper.platform.platform.exception;

import java.util.UUID;

// Raised when a platform cannot be removed because other records still depend on it
public class PlatformDeletionNotAllowedException extends RuntimeException
{
    // ID of the platform that cannot be deleted
    private final UUID platformId;

    public PlatformDeletionNotAllowedException(UUID platformId, String reason)
    {
        super("Cannot delete platform " + platformId + ": " + reason);
        this.platformId = platformId;
    }

    public UUID getPlatformId()
    {
        return platformId;
    }
}
