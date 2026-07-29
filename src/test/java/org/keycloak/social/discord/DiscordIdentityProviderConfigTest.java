package org.keycloak.social.discord;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the settings that changed shape when the admin console gained one input box per entry. The legacy
 * cases are the point: these values are already stored in live realms, and a parser that stopped reading them
 * would empty a guild allow-list silently — which fails OPEN, letting anyone sign in.
 */
class DiscordIdentityProviderConfigTest {

    @Test
    void readsMultivaluedEntries() {
        assertEquals(List.of("111", "222"), DiscordIdentityProviderConfig.splitEntries("111##222"));
        assertEquals(List.of("111", "222"), DiscordIdentityProviderConfig.splitEntries("111 ## 222"));
    }

    @Test
    void stillReadsLegacyCommaSeparatedEntries() {
        assertEquals(List.of("111", "222"), DiscordIdentityProviderConfig.splitEntries("111,222"));
        assertEquals(List.of("111", "222"), DiscordIdentityProviderConfig.splitEntries(" 111 , 222 "));
    }

    @Test
    void emptyAndBlankYieldNothing() {
        assertTrue(DiscordIdentityProviderConfig.splitEntries(null).isEmpty());
        assertTrue(DiscordIdentityProviderConfig.splitEntries("   ").isEmpty());
        assertTrue(DiscordIdentityProviderConfig.splitEntries("a,,b##").contains("a"));
        assertEquals(2, DiscordIdentityProviderConfig.splitEntries("a,,b##").size());
    }

    @Test
    void roleMappingsParseInBothFormats() {
        DiscordIdentityProviderConfig legacy = new DiscordIdentityProviderConfig();
        legacy.setMappedRoles("g1:r1:groupA,g1:r2:groupB");
        Map<String, java.util.HashMap<String, String>> parsedLegacy = legacy.getMappedRolesAsMap();
        assertEquals("groupA", parsedLegacy.get("g1").get("r1"));
        assertEquals("groupB", parsedLegacy.get("g1").get("r2"));

        DiscordIdentityProviderConfig multi = new DiscordIdentityProviderConfig();
        multi.setMappedRoles("g1:r1:groupA##g2:r3:groupC");
        Map<String, java.util.HashMap<String, String>> parsedMulti = multi.getMappedRolesAsMap();
        assertEquals("groupA", parsedMulti.get("g1").get("r1"));
        assertEquals("groupC", parsedMulti.get("g2").get("r3"));
    }

    @Test
    void malformedMappingsAreSkippedRatherThanFailing() {
        DiscordIdentityProviderConfig config = new DiscordIdentityProviderConfig();
        config.setMappedRoles("nonsense##g1:r1:groupA##also:bad");
        Map<String, java.util.HashMap<String, String>> parsed = config.getMappedRolesAsMap();
        assertEquals(1, parsed.size());
        assertEquals("groupA", parsed.get("g1").get("r1"));
    }

    @Test
    void promptNoneReadsTheValueNotThePresenceOfTheKey() {
        DiscordIdentityProviderConfig config = new DiscordIdentityProviderConfig();
        config.getConfig().put(DiscordIdentityProviderConfig.PROMPT_NONE, "false");
        assertEquals(false, config.isPromptNone(),
                "the admin console writes \"false\" rather than removing the key, so a presence check " +
                        "would leave the prompt suppressed forever once it had been enabled once");
        config.getConfig().put(DiscordIdentityProviderConfig.PROMPT_NONE, "true");
        assertEquals(true, config.isPromptNone());
    }

    @Test
    void avatarUrlHandlesAnimatedStaticAndMissingAvatars() {
        assertEquals("https://cdn.discordapp.com/avatars/123/a_hash.gif",
                DiscordIdentityProvider.avatarUrl("123", "a_hash"));
        assertEquals("https://cdn.discordapp.com/avatars/123/hash.png",
                DiscordIdentityProvider.avatarUrl("123", "hash"));
        assertTrue(DiscordIdentityProvider.avatarUrl("123", null).startsWith("https://cdn.discordapp.com/embed/avatars/"));
        assertEquals(null, DiscordIdentityProvider.avatarUrl(null, "hash"));
        // A non-snowflake id must not fail a login.
        assertTrue(DiscordIdentityProvider.avatarUrl("not-a-number", "").endsWith("/0.png"));
    }
}
