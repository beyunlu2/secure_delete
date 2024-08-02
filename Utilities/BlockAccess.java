package com.Server.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;

public class BlockAccess {

    public static boolean makeFolderUnreadableForOthers(File folder) {
        Path folderPath = folder.toPath();

        try {
            // Get the current user principal
            UserPrincipal currentUser = folderPath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(System.getProperty("user.name"));

            UserPrincipal everyone = FileSystems.getDefault()
                                                .getUserPrincipalLookupService()
                                                .lookupPrincipalByName("Everyone");

            // Get the current ACL of the folder
            AclFileAttributeView aclAttr = Files.getFileAttributeView(folderPath, AclFileAttributeView.class);
            List<AclEntry> acl = aclAttr.getAcl();

            // Create a new ACL entry that denies all permissions to others
            AclEntry denyOthersEntry = AclEntry.newBuilder()
                    .setType(AclEntryType.DENY)
                    .setPrincipal(everyone)
                    .setPermissions(
                        AclEntryPermission.READ_DATA,
                        AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.APPEND_DATA,
                        AclEntryPermission.READ_ATTRIBUTES,
                        AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.READ_ACL,
                        AclEntryPermission.WRITE_ACL,
                        AclEntryPermission.DELETE,
                        AclEntryPermission.WRITE_OWNER)
                    .build();

            // Add the deny entry to the current ACL
            acl.add(1, denyOthersEntry); // Adding at the end

            // Create a new ACL entry that allows full access for the current user
            AclEntry allowCurrentUserEntry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)
                    .setPrincipal(currentUser)
                    .setPermissions(
                        AclEntryPermission.READ_DATA, 
                        AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.APPEND_DATA,
                        AclEntryPermission.READ_ATTRIBUTES,
                        AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.READ_ACL,
                        AclEntryPermission.WRITE_ACL,
                        AclEntryPermission.DELETE,
                        AclEntryPermission.WRITE_OWNER,
                        AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.READ_DATA)
                    .build();

            // Add the new ACL entry to the current ACL
            acl.add(0, allowCurrentUserEntry); // Adding at the beginning to give precedence

            // Set the modified ACL back to the file
            aclAttr.setAcl(acl);

            return true;
        } catch (IOException e) {
            System.err.println("Error changing folder permissions: " + e.getMessage());
            return false;
        }
    }
}
