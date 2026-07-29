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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.http.simple.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.ErrorPageException;
import org.keycloak.services.messages.Messages;

import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class DiscordIdentityProvider extends AbstractOAuth2IdentityProvider<DiscordIdentityProviderConfig>
        implements SocialIdentityProvider<DiscordIdentityProviderConfig> {

    private static final Logger log = Logger.getLogger(DiscordIdentityProvider.class);

    public static final String AUTH_URL = "https://discord.com/oauth2/authorize";
    public static final String TOKEN_URL = "https://discord.com/api/oauth2/token";
    public static final String PROFILE_URL = "https://discord.com/api/users/@me";
    public static final String GROUP_URL = "https://discord.com/api/users/@me/guilds";
    public static final String GUILD_MEMBER_URL = "https://discord.com/api/users/@me/guilds/%s/member";
    public static final String IDENTIFY_SCOPE = "identify";
    public static final String EMAIL_SCOPE = "email";
    public static final String DEFAULT_SCOPE = IDENTIFY_SCOPE + " " + EMAIL_SCOPE;
    public static final String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s.%s";
    public static final String DEFAULT_AVATAR_URL = "https://cdn.discordapp.com/embed/avatars/%d.png";
    public static final String GUILDS_SCOPE = "guilds";
    public static final String ROLES_SCOPE = "guilds.members.read";

    public DiscordIdentityProvider(KeycloakSession session, DiscordIdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
        if (config.setPromptNone()) {
            config.setPrompt("none");
        }
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "id"), getConfig());

        String username = getJsonProperty(profile, "username");
        String discriminator = getJsonProperty(profile, "discriminator");

        if (!"0".equals(discriminator)) {
            username += "#" + discriminator;
        }

        user.setUsername(username);
        if (!getConfig().isSkipEmailScope()) {
            user.setEmail(getJsonProperty(profile, "email"));
        }
        user.setIdp(this);

        // "picture" is the OIDC standard claim for an avatar, so a mapper written against any other provider
        // finds it under the name it already expects. Discord returns an avatar HASH, not a URL, and none at
        // all for a user who never set one — hence the CDN default, which is derived from the account id.
        String pictureUrl = avatarUrl(getJsonProperty(profile, "id"), getJsonProperty(profile, "avatar"));
        if (pictureUrl != null) {
            user.setUserAttribute("picture", pictureUrl);
        }

        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());

        return user;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        log.debug("doGetFederatedIdentity()");
        JsonNode profile;
        try {
            profile = SimpleHttp.create(session).doGet(PROFILE_URL).header("Authorization", "Bearer " + accessToken).asJson();
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain user profile from discord.", e);
        }

        if (getConfig().hasAllowedGuilds()) {
            if (!isAllowedGuild(accessToken)) {
                throw new ErrorPageException(session, Response.Status.FORBIDDEN, Messages.INVALID_REQUESTER);
            }
        }

        ArrayNode groups = JsonNodeFactory.instance.arrayNode();
        if (getConfig().hasMappedRoles()) {
            Map<String, HashMap<String, String>> mappedRoles = getConfig().getMappedRolesAsMap();
            for (String guild : mappedRoles.keySet()) {
                JsonNode guildMember;
                try {
                    guildMember = SimpleHttp.create(session).doGet(String.format(GUILD_MEMBER_URL, guild)).header("Authorization", "Bearer " + accessToken).asJson();
                    if (guildMember.has("joined_at") && mappedRoles.get(guild).containsKey(guild) ) {
                        groups.add(mappedRoles.get(guild).get(guild));
                    }
                    for (JsonNode role : guildMember.get("roles")) {
                        String roleString = role.textValue();
                        if (mappedRoles.get(guild).containsKey(roleString)) {
                            groups.add(mappedRoles.get(guild).get(roleString));
                        }
                    }
                } catch (Exception e) {
                    log.debug("Could not obtain guild member data from discord.");
                }
            }
        }
        if (profile instanceof ObjectNode) {
            ((ObjectNode) profile).set("discord-groups", groups);
        }

        return extractIdentityFromProfile(null, profile);
    }

    protected boolean isAllowedGuild(String accessToken) {
        try {
            JsonNode guilds = SimpleHttp.create(session).doGet(GROUP_URL).header("Authorization", "Bearer " + accessToken).asJson();
            Set<String> allowedGuilds = getConfig().getAllowedGuildsAsSet();
            for (JsonNode guild : guilds) {
                String guildId = getJsonProperty(guild, "id");
                if (allowedGuilds.contains(guildId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain guilds the current user is a member of from discord.", e);
        }
    }

    /**
     * avatarUrl builds the CDN URL for a Discord avatar hash, or the account's default avatar when it has
     * none. An "a_" prefixed hash is animated and served as a gif; anything else is a png. Returns null when
     * the id is missing, which leaves the attribute unset rather than pointing at a broken image.
     */
    static String avatarUrl(String userId, String avatarHash) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        if (avatarHash == null || avatarHash.isBlank()) {
            // Discord's own fallback: (id >> 22) % 6 for migrated accounts. Parsed defensively — an id that
            // is not a snowflake is not worth failing a login over.
            try {
                return String.format(DEFAULT_AVATAR_URL, (Long.parseUnsignedLong(userId) >> 22) % 6);
            } catch (NumberFormatException e) {
                return String.format(DEFAULT_AVATAR_URL, 0);
            }
        }
        String extension = avatarHash.startsWith("a_") ? "gif" : "png";
        return String.format(AVATAR_URL, userId, avatarHash, extension);
    }

    @Override
    protected String getDefaultScopes() {
        String scopes = getConfig().isSkipEmailScope() ? IDENTIFY_SCOPE : DEFAULT_SCOPE;
        if (getConfig().hasAllowedGuilds()) {
            scopes += " " + GUILDS_SCOPE;
        }
        if (getConfig().hasMappedRoles()) {
            scopes += " " + ROLES_SCOPE;
        }
        return scopes;
    }
}
