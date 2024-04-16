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
    def itemRole = itemRoleMap.getRole('test')
    if (itemRole == null) {
        // Create item role if it doesn't exist
        Set<Permission> itemPermissions = new HashSet<>();
        // Add specific permissions for the item role
        // itemPermissions.add(...);

        itemRole = new Role("test", itemPermissions)
        itemRoleMap.addRole(itemRole)
    }

    // Check if 'ram' is assigned to the item role
    boolean ramAssigned = false
    itemRole.permissions.each { permissionEntry ->
        if (permissionEntry.authorizationType == AuthorizationType.USER && permissionEntry.principal == 'ram') {
            ramAssigned = true
            return
        }
    }

    if (!ramAssigned) {
        // Assign item role to user 'ram'
        itemRoleMap.assignRole(itemRole, new PermissionEntry(AuthorizationType.USER, 'ram'))
    }

    // Save the changes
    jenkins.save()
} else {
    println("Authorization strategy is not RoleBasedAuthorizationStrategy.")
}
