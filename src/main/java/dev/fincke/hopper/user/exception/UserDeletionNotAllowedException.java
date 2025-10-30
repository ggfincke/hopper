package dev.fincke.hopper.user.exception;

import dev.fincke.hopper.api.error.ConflictException;

import java.util.UUID;

// Raised when a user cannot be removed because prerequisites (such as disabling the account) are not met
public class UserDeletionNotAllowedException extends ConflictException
{
    // ID of the user that cannot be deleted
    private final UUID userId;

    public UserDeletionNotAllowedException(UUID userId, String reason)
    {
        super("Cannot delete user " + userId + ": " + reason);
        this.userId = userId;
    }

    public UUID getUserId()
    {
        return userId;
    }
}
