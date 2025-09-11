package dev.fincke.hopper.user;

import dev.fincke.hopper.user.dto.PasswordChangeRequest;
import dev.fincke.hopper.user.dto.UserCreateRequest;
import dev.fincke.hopper.user.dto.UserResponse;
import dev.fincke.hopper.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST controller handling user management API endpoints
@RestController
@RequestMapping("/api/users")
public class UserController
{
    
    // * Dependencies
    
    // Spring will inject service dependency
    private final UserService userService;
    
    // * Constructor
    
    // Constructor injection for UserService
    public UserController(UserService userService)
    {
        this.userService = userService;
    }
    
    // * Core CRUD Endpoints
    
    // POST /api/users - create new user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request)
    {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // PUT /api/users/{id} - update existing user
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request)
    {
        return userService.updateUser(id, request);
    }
    
    // GET /api/users/{id} - get user by ID
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id)
    {
        return userService.findById(id);
    }
    
    // GET /api/users - list all users
    @GetMapping
    public List<UserResponse> getAllUsers()
    {
        return userService.findAll();
    }
    
    // DELETE /api/users/{id} - delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Authentication Query Endpoints
    
    // GET /api/users/username/{username} - find user by username
    @GetMapping("/username/{username}")
    public UserResponse getByUsername(@PathVariable String username)
    {
        return userService.findByUsername(username);
    }
    
    // GET /api/users/email/{email} - find user by email
    @GetMapping("/email/{email}")
    public UserResponse getByEmail(@PathVariable String email)
    {
        return userService.findByEmail(email);
    }
    
    // GET /api/users/identifier/{identifier} - find user by username or email
    @GetMapping("/identifier/{identifier}")
    public UserResponse getByUsernameOrEmail(@PathVariable String identifier)
    {
        return userService.findByUsernameOrEmail(identifier);
    }
    
    // * Password Management Endpoints
    
    // PUT /api/users/{id}/password - change user password
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, 
                                              @Valid @RequestBody PasswordChangeRequest request)
    {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
    
    // PUT /api/users/{id}/password/reset - reset user password (admin operation)
    @PutMapping("/{id}/password/reset")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id, 
                                             @RequestParam String newPassword)
    {
        userService.resetPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }
    
    // POST /api/users/{id}/password/verify - verify user password
    @PostMapping("/{id}/password/verify")
    public ResponseEntity<Boolean> verifyPassword(@PathVariable UUID id, 
                                                 @RequestParam String password)
    {
        boolean valid = userService.verifyPassword(id, password);
        return ResponseEntity.ok(valid);
    }
    
    // * Account Management Endpoints
    
    // PUT /api/users/{id}/lock - lock user account
    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockAccount(@PathVariable UUID id)
    {
        userService.lockAccount(id);
        return ResponseEntity.ok().build();
    }
    
    // PUT /api/users/{id}/unlock - unlock user account
    @PutMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockAccount(@PathVariable UUID id)
    {
        userService.unlockAccount(id);
        return ResponseEntity.ok().build();
    }
    
    // PUT /api/users/{id}/enable - enable user account
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableAccount(@PathVariable UUID id)
    {
        userService.enableAccount(id);
        return ResponseEntity.ok().build();
    }
    
    // PUT /api/users/{id}/disable - disable user account
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableAccount(@PathVariable UUID id)
    {
        userService.disableAccount(id);
        return ResponseEntity.ok().build();
    }
    
    // * Role Management Endpoints
    
    // PUT /api/users/{id}/roles/{roleType} - add role to user
    @PutMapping("/{id}/roles/{roleType}")
    public ResponseEntity<Void> addRole(@PathVariable UUID id, @PathVariable RoleType roleType)
    {
        userService.addRole(id, roleType);
        return ResponseEntity.ok().build();
    }
    
    // DELETE /api/users/{id}/roles/{roleType} - remove role from user
    @DeleteMapping("/{id}/roles/{roleType}")
    public ResponseEntity<Void> removeRole(@PathVariable UUID id, @PathVariable RoleType roleType)
    {
        userService.removeRole(id, roleType);
        return ResponseEntity.ok().build();
    }
    
    // GET /api/users/roles/{roleType} - find users with specific role
    @GetMapping("/roles/{roleType}")
    public List<UserResponse> getUsersByRole(@PathVariable RoleType roleType)
    {
        return userService.findByRole(roleType);
    }
    
    // * Search Endpoints
    
    // GET /api/users/search/username/{username} - search users by username
    @GetMapping("/search/username/{username}")
    public List<UserResponse> searchByUsername(@PathVariable String username)
    {
        return userService.searchByUsername(username);
    }
    
    // GET /api/users/search/email/{email} - search users by email
    @GetMapping("/search/email/{email}")
    public List<UserResponse> searchByEmail(@PathVariable String email)
    {
        return userService.searchByEmail(email);
    }
    
    // GET /api/users/enabled - get enabled users
    @GetMapping("/enabled")
    public List<UserResponse> getEnabledUsers()
    {
        return userService.findEnabledUsers();
    }
    
    // GET /api/users/locked - get locked users
    @GetMapping("/locked")
    public List<UserResponse> getLockedUsers()
    {
        return userService.findLockedUsers();
    }
    
    // * Utility Endpoints
    
    // GET /api/users/exists/username/{username} - check if username exists
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username)
    {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    // GET /api/users/exists/email/{email} - check if email exists
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email)
    {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    // POST /api/users/{id}/validate - validate user data
    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateUser(@PathVariable UUID id)
    {
        userService.validateUser(id);
        return ResponseEntity.ok().build();
    }
}