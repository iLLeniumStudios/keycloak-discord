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

    public DiscordIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public DiscordIdentityProviderConfig() {
    }

    public String getAllowedGuilds() {
        return getConfig().get(ALLOWED_GUILDS);
    }

    public boolean setPromptNone() {
        if(getConfig().containsKey(PROMPT_NONE)) {
            return true;
        } else {
            return false;
        }
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
    public boolean isSkipEmailScope() {
        return Boolean.parseBoolean(getConfig().get(SKIP_EMAIL_SCOPE));
    }

    public void setSkipEmailScope(boolean skipEmailScope) {
        getConfig().put(SKIP_EMAIL_SCOPE, String.valueOf(skipEmailScope));
    }
}
