/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.social.discord;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class DiscordIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public static final String ALLOWED_GUILDS = "allowedGuilds";
    public static final String MAPPED_ROLES = "mappedRoles";
    public static final String PROMPT = "prompt";
    public static final String PROMPT_NONE = "promptNone";
    public static final String SKIP_EMAIL_SCOPE = "skipEmailScope";
    public static final String ACCEPT_UNVERIFIED_EMAIL = "acceptUnverifiedEmail";
    public static final String ADDITIONAL_SCOPES = "additionalScopes";
    public static final String STORE_GUILDS = "storeGuilds";

    public DiscordIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public DiscordIdentityProviderConfig() {
    }

    public String getAllowedGuilds() {
        return getConfig().get(ALLOWED_GUILDS);
    }

    /**
     * Whether Discord should skip its consent prompt for users who already authorised the application.
     *
     * Reads the VALUE, not merely the key's presence: the admin console writes "false" when the toggle is
     * turned off rather than removing the entry, so a presence check stays true forever once it has been
     * enabled once.
     */
    public boolean isPromptNone() {
        return Boolean.parseBoolean(getConfig().get(PROMPT_NONE));
    }

    public void setAllowedGuilds(String allowedGuilds) {
        getConfig().put(ALLOWED_GUILDS, allowedGuilds);
    }

    public boolean hasAllowedGuilds() {
        String guilds = getConfig().get(ALLOWED_GUILDS);
        return guilds != null && !guilds.trim().isEmpty();
    }

    public Set<String> getAllowedGuildsAsSet() {
        if (hasAllowedGuilds()) {
            String guilds = getConfig().get(ALLOWED_GUILDS);
            return Arrays.stream(guilds.split(",")).map(x -> x.trim()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public String getMappedRoles() {
        return getConfig().get(MAPPED_ROLES);
    }

    public void setMappedRoles(String mappedRoles) {
        getConfig().put(MAPPED_ROLES, mappedRoles);
    }

    public boolean hasMappedRoles() {
        String mappedRoles = getConfig().get(MAPPED_ROLES);
        return mappedRoles != null && !mappedRoles.trim().isEmpty();
    }

    public Map<String, HashMap<String, String>> getMappedRolesAsMap() {
        if (hasMappedRoles()) {
            String mappedRoles = getMappedRoles();
            Map<String, HashMap<String, String>> parsedRoles = new HashMap<>();
            for (String rawRole : mappedRoles.split(",")) {
                rawRole = rawRole.trim();
                String fragments[] = rawRole.split(":");
                if (fragments.length != 3) {
                    continue;
                }
                if (!parsedRoles.containsKey(fragments[0])) {
                    parsedRoles.put(fragments[0], new HashMap<>());
                }
                parsedRoles.get(fragments[0]).put(fragments[1], fragments[2]);
            }
            return parsedRoles;
        }
        return Collections.emptyMap();
    }

    public void setPrompt(String prompt) {
        getConfig().put(PROMPT, prompt);
    }

    /**
     * Whether this provider requests Discord's email scope. Discord only returns an address when "email" is
     * requested, and some deployments broker accounts that have none — or deliberately do not want one.
     *
     * Per provider rather than per Keycloak instance: an installation may broker more than one Discord
     * application, and a JVM-wide switch could not tell them apart or show itself in the admin console.
     */
    /**
     * Extra OAuth scopes appended to the ones this provider derives from its own settings, space or comma
     * separated. Discord returns a scope's data only when it was requested, so anything a custom mapper wants
     * to read — "connections", say — has to be asked for here.
     */
    public String getAdditionalScopes() {
        return getConfig().get(ADDITIONAL_SCOPES);
    }

    public void setAdditionalScopes(String additionalScopes) {
        getConfig().put(ADDITIONAL_SCOPES, additionalScopes);
    }

    public Set<String> getAdditionalScopesAsSet() {
        String scopes = getAdditionalScopes();
        if (scopes == null || scopes.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(scopes.split("[,\\s]+"))
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Whether an email Discord reports as unverified is accepted anyway. Off by default, and that default is
     * the security fix: Discord will hand back an unverified address, and Keycloak's first-broker-login can
     * link a brokered identity to an existing local account by email. Accepting one lets anyone who registers
     * a Discord account against someone else's address take over that account.
     */
    public boolean isAcceptUnverifiedEmail() {
        return Boolean.parseBoolean(getConfig().get(ACCEPT_UNVERIFIED_EMAIL));
    }

    public void setAcceptUnverifiedEmail(boolean accept) {
        getConfig().put(ACCEPT_UNVERIFIED_EMAIL, String.valueOf(accept));
    }

    /**
     * Whether every guild the user belongs to is stored for mappers to read. Requires the guilds scope, which
     * is requested automatically when this is on.
     */
    public boolean isStoreGuilds() {
        return Boolean.parseBoolean(getConfig().get(STORE_GUILDS));
    }

    public void setStoreGuilds(boolean storeGuilds) {
        getConfig().put(STORE_GUILDS, String.valueOf(storeGuilds));
    }

    public boolean isSkipEmailScope() {
        return Boolean.parseBoolean(getConfig().get(SKIP_EMAIL_SCOPE));
    }

    public void setSkipEmailScope(boolean skipEmailScope) {
        getConfig().put(SKIP_EMAIL_SCOPE, String.valueOf(skipEmailScope));
    }
}
