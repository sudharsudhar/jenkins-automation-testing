import com.michelin.cio.hudson.plugins.rolestrategy.*
import hudson.security.*
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType
import jenkins.model.Jenkins

Jenkins jenkins = Jenkins.get()
def authStrategy = jenkins.getAuthorizationStrategy()

// Check if RoleBasedAuthorizationStrategy is configured
if (authStrategy instanceof RoleBasedAuthorizationStrategy) {
    def itemRoleMap = authStrategy.getRoleMap(RoleType.Project)

    // Check if the item role already exists
    def itemRole = itemRoleMap.getRole('testproject')
    if (itemRole == null) {
        // Create item role if it doesn't exist
        Set<Permission> itemPermissions = new HashSet<>();
        // Add specific permissions for the item role
        // itemPermissions.add(...);

        itemRole = new Role("testproject", itemPermissions)
        itemRoleMap.addRole(itemRole)
    }

    // Check if 'testuser1' is assigned to the item role
    boolean Assigned = false
    itemRole.permissions.each { permissionEntry ->
        if (permissionEntry.authorizationType == AuthorizationType.USER && permissionEntry.principal == 'testuser1') {
            Assigned = true
            return
        }
    }

    if (!Assigned) {
        // Assign item role to user 'testuser1'
        itemRoleMap.assignRole(itemRole, new PermissionEntry(AuthorizationType.USER, 'testuser1'))
    }

    // Save the changes
    jenkins.save()
} else {
    println("Authorization strategy is not RoleBasedAuthorizationStrategy.")
}
