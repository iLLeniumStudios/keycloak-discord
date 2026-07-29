package org.keycloak.social.discord.mappers;

import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.KeycloakOIDCIdentityProviderFactory;
import org.keycloak.broker.oidc.OIDCIdentityProviderFactory;
import org.keycloak.broker.oidc.mappers.AbstractClaimMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.social.discord.DiscordIdentityProviderFactory;

import br.com.luizcarlosvianamelo.keycloak.broker.oidc.mappers.ClaimToGroupMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClaimToRoleMapper syncs a claim's string array into the realm's ROLES, the role-shaped sibling of
 * ClaimToGroupMapper. Point it at "discord-groups" to turn a Discord role mapping into Keycloak roles rather
 * than group membership.
 *
 * Scoping is what keeps it safe to run alongside anything else that grants roles: only roles matching the
 * configured prefix are ever revoked, so a role granted by an administrator, another mapper or a client scope
 * is untouched. With no prefix set the mapper only ever ADDS, because a mapper that could revoke every role in
 * the realm on a claim it failed to read is not one worth having.
 */
public class ClaimToRoleMapper extends AbstractClaimMapper {

    private static final Logger logger = Logger.getLogger(ClaimToRoleMapper.class);

    public static final String PROVIDER_ID = "discord-role-idp-mapper";

    private static final String[] COMPATIBLE_PROVIDERS = {
            KeycloakOIDCIdentityProviderFactory.PROVIDER_ID,
            OIDCIdentityProviderFactory.PROVIDER_ID,
            DiscordIdentityProviderFactory.PROVIDER_ID
    };

    private static final String CLAIM_NAME = "claim";
    private static final String ROLE_PREFIX = "role_prefix";
    private static final String CREATE_ROLES = "create_roles";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    static {
        ProviderConfigProperty claim = new ProviderConfigProperty();
        claim.setName(CLAIM_NAME);
        claim.setLabel("Claim");
        claim.setHelpText("Name of the claim holding the role names, as a string array. For the Discord "
                + "provider's own role mapping this is \"discord-groups\". Nested claims are referenced with "
                + "a '.'.");
        claim.setType(ProviderConfigProperty.STRING_TYPE);
        claim.setDefaultValue("discord-groups");
        CONFIG_PROPERTIES.add(claim);

        ProviderConfigProperty prefix = new ProviderConfigProperty();
        prefix.setName(ROLE_PREFIX);
        prefix.setLabel("Managed role prefix");
        prefix.setHelpText("Only realm roles starting with this prefix are managed: they are granted when the "
                + "claim names them and revoked when it stops. Roles without the prefix are never touched, so "
                + "this mapper cannot remove a role granted by an administrator or another mapper. LEAVE EMPTY "
                + "to only ever add roles and never revoke.");
        prefix.setType(ProviderConfigProperty.STRING_TYPE);
        CONFIG_PROPERTIES.add(prefix);

        ProviderConfigProperty create = new ProviderConfigProperty();
        create.setName(CREATE_ROLES);
        create.setLabel("Create missing roles");
        create.setHelpText("Create a realm role the claim names but the realm does not have. Off by default: "
                + "a typo in a Discord role mapping otherwise silently populates the realm with roles.");
        create.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        create.setDefaultValue("false");
        CONFIG_PROPERTIES.add(create);
    }

    @Override
    public String[] getCompatibleProviders() {
        return COMPATIBLE_PROVIDERS;
    }

    @Override
    public String getDisplayCategory() {
        return "Role Importer";
    }

    @Override
    public String getDisplayType() {
        return "Claim to Role";
    }

    @Override
    public String getHelpText() {
        return "Sync a claim's values into the realm's roles. Use \"discord-groups\" to turn the Discord "
                + "provider's role mapping into Keycloak roles instead of groups.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
        return true;
    }

    @Override
    public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
                              IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        syncRoles(realm, user, mapperModel, context);
    }

    @Override
    public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
                                   IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        syncRoles(realm, user, mapperModel, context);
    }

    private void syncRoles(RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel,
                           BrokeredIdentityContext context) {
        String claimName = mapperModel.getConfig().get(CLAIM_NAME);
        if (claimName == null || claimName.isBlank()) {
            return;
        }
        String prefix = mapperModel.getConfig().get(ROLE_PREFIX);
        boolean createRoles = Boolean.parseBoolean(mapperModel.getConfig().get(CREATE_ROLES));

        Set<String> wanted = new LinkedHashSet<>(ClaimToGroupMapper.getClaimValue(context, claimName));

        for (String roleName : wanted) {
            RoleModel role = realm.getRole(roleName);
            if (role == null) {
                if (!createRoles) {
                    logger.debugf("Realm [%s]: role [%s] from claim [%s] does not exist and creation is off",
                            realm.getName(), roleName, claimName);
                    continue;
                }
                role = realm.addRole(roleName);
            }
            if (!user.hasRole(role)) {
                user.grantRole(role);
            }
        }

        // Revoking is opt-in via the prefix, and bounded by it. Without one this mapper only adds — a claim
        // that failed to parse would otherwise strip every role the user has.
        if (prefix == null || prefix.isBlank()) {
            return;
        }
        Set<RoleModel> stale = user.getRealmRoleMappingsStream()
                .filter(role -> role.getName().startsWith(prefix))
                .filter(role -> !wanted.contains(role.getName()))
                .collect(Collectors.toCollection(HashSet::new));
        for (RoleModel role : stale) {
            user.deleteRoleMapping(role);
        }
    }
}
