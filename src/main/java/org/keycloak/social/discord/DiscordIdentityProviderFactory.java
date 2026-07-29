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

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

/**
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class DiscordIdentityProviderFactory extends AbstractIdentityProviderFactory<DiscordIdentityProvider>
        implements SocialIdentityProviderFactory<DiscordIdentityProvider> {

    public static final String PROVIDER_ID = "discord";

    @Override
    public String getName() {
        return "Discord";
    }

    @Override
    public DiscordIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new DiscordIdentityProvider(session, new DiscordIdentityProviderConfig(model));
    }

    @Override
    public DiscordIdentityProviderConfig createConfig() {
        return new DiscordIdentityProviderConfig();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(DiscordIdentityProviderConfig.ALLOWED_GUILDS)
                .type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .label("Guild Id(s) to allow federation")
                .helpText("Only members of these guilds may sign in. One guild id per row. Leave empty to allow any Discord account. Existing comma-separated values are still read.")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.MAPPED_ROLES)
                .type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .label("Discord Roles mapping")
                .helpText("Map a Discord role to a Keycloak group, one mapping per row, as '<guild_id>:<role_id>:<group_name>'. Repeat the guild id in place of the role id to match anyone who is simply a member of that guild. Existing comma-separated values are still read.")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.PROMPT_NONE)
                .type(ProviderConfigProperty.BOOLEAN_TYPE)
                .label("Skip Discord prompt")
                .helpText("Should Discord skip the prompt for users that have already granted access to our application?")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.ADDITIONAL_SCOPES)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Additional scopes")
                .helpText("Extra Discord OAuth scopes to request, space or comma separated (for example \"connections\"). Discord returns a scope's data only when it was asked for, so a custom mapper reading anything beyond the profile needs its scope named here. identify, email, guilds and guilds.members.read are added automatically when the settings above require them.")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.STORE_GUILDS)
                .type(ProviderConfigProperty.BOOLEAN_TYPE)
                .label("Store the user's guilds")
                .helpText("Record every guild the user belongs to as \"discord-guilds\", so an Attribute Importer mapper can copy it onto the user. Requests the guilds scope automatically.")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.ACCEPT_UNVERIFIED_EMAIL)
                .type(ProviderConfigProperty.BOOLEAN_TYPE)
                .label("Accept unverified email addresses")
                .helpText("SECURITY: leave this off. Discord returns an address before the account has confirmed it, and Keycloak's first broker login can link a brokered identity to an existing user by email — so accepting one lets anyone who registers a Discord account against someone else's address take over that account. Enable only where no local account can be matched by email.")
                .add()
                .property()
                .name(DiscordIdentityProviderConfig.SKIP_EMAIL_SCOPE)
                .type(ProviderConfigProperty.BOOLEAN_TYPE)
                .label("Do not request the email scope")
                .helpText("Broker accounts without asking Discord for an email address. Discord only returns one when the email scope is requested, so leaving this on means brokered users arrive with no email — set it only when the realm does not require one.")
                .add()
                .build();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}