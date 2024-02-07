package hu.bearmaster.springtutorial.boot.security.service;

import hu.bearmaster.springtutorial.boot.security.model.dto.UserDto;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    private final MutableAclService aclService;

    private final PermissionFactory permissionFactory;

    public PermissionService(MutableAclService aclService, PermissionFactory permissionFactory) {
        this.aclService = aclService;
        this.permissionFactory = permissionFactory;
    }

    public void addPermission(UserDto user, Long subjectId, Class<?> subjectType, String permissionName) {
        Sid sid = new PrincipalSid(user.getEmail());
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(subjectType, subjectId);
        addPermission(sid, objectIdentity, permissionName);
    }

    public void addPermission(String role, Long subjectId, Class<?> subjectType, String permissionName) {
        Sid sid = new GrantedAuthoritySid(role);
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(subjectType, subjectId);
        addPermission(sid, objectIdentity, permissionName);
    }

    public void removePermission(UserDto user, Long subjectId, Class<?> subjectType, String permissionName) {
        Sid sid = new PrincipalSid(user.getEmail());
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(subjectType, subjectId);
        removePermission(sid, objectIdentity, permissionName);
    }

    public void removePermission(String role, Long subjectId, Class<?> subjectType, String permissionName) {
        Sid sid = new GrantedAuthoritySid(role);
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(subjectType, subjectId);
        removePermission(sid, objectIdentity, permissionName);
    }

    private void addPermission(Sid sid, ObjectIdentity objectIdentity, String permissionName) {
        MutableAcl acl = getAcl(objectIdentity, sid).orElseGet(() -> aclService.createAcl(objectIdentity));
        createOrUpdateAce(acl, sid, permissionName);
        aclService.updateAcl(acl);
    }

    private void removePermission(Sid sid, ObjectIdentity objectIdentity, String permissionName) {
        Optional<MutableAcl> aclCandidate = getAcl(objectIdentity, sid);
        if (aclCandidate.isPresent()) {
            MutableAcl acl = aclCandidate.get();
            int index = getAceIndexForSid(acl.getEntries(), sid);
            if (index >= 0) {
                Permission permission = new CumulativePermission()
                        .set(acl.getEntries().get(index).getPermission())
                        .clear(permissionFactory.buildFromName(permissionName));
                acl.updateAce(index, permission);
                aclService.updateAcl(acl);
            }
        }
    }

    private Optional<MutableAcl> getAcl(ObjectIdentity objectIdentity, Sid sid) {
        try {
            return Optional.ofNullable((MutableAcl) aclService.readAclById(objectIdentity, List.of(sid)));
        } catch (NotFoundException nfe) {
            return Optional.empty();
        }
    }

    private void createOrUpdateAce(MutableAcl acl, Sid sid, String permissionName) {
        int index = getAceIndexForSid(acl.getEntries(), sid);
        if (index >= 0) {
            Permission permission = new CumulativePermission()
                    .set(acl.getEntries().get(index).getPermission())
                    .set(permissionFactory.buildFromName(permissionName));
            acl.updateAce(index, permission);
        } else {
            Permission permission = new CumulativePermission()
                    .set(permissionFactory.buildFromName(permissionName));
            acl.insertAce(acl.getEntries().size(), permission, sid, true);
        }
    }

    private int getAceIndexForSid(List<AccessControlEntry> aceEntryList, Sid sid) {
        for (int i = 0; i < aceEntryList.size(); i++) {
            AccessControlEntry ace = aceEntryList.get(i);
            if (ace.getSid().equals(sid) && ace.isGranting()) {
                return i;
            }
        }
        return -1;
    }
}
