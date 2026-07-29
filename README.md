# keycloak-discord

Keycloak Social Login extension for Discord.

This is a fork of [wadahiro/keycloak-discord](https://github.com/wadahiro/keycloak-discord) by way of
[FTBTeam](https://github.com/FTBTeam/keycloak-discord), tracking Keycloak 26.5 and carrying fixes that are
still open upstream. See [What this fork adds](#what-this-fork-adds).

## Install

Download `keycloak-discord-<version>.jar` from the [Releases page](https://github.com/iLLeniumStudios/keycloak-discord/releases).
Then deploy it into `$KEYCLOAK_HOME/providers` directory.

## Setup

### Discord

Access to [Discord Developer Portal](https://discord.com/developers/applications) and create your application.
You can get Client ID and Client Secret from the created application.

### Keycloak

Note: You don't need to setup the theme in `master` realm from v0.3.0.

1. Add `discord` Identity Provider in the realm which you want to configure.
2. In the `discord` identity provider page, set `Client Id` and `Client Secret`.
3. (Optional) Set Guild Id(s) to allow federation if you want. One guild id per row — only members of those
   guilds may sign in. Leave it empty to allow any Discord account.

### Identity provider settings

| Setting | Meaning |
| --- | --- |
| **Guild Id(s) to allow federation** | Only members of these guilds may sign in. Empty allows any Discord account. |
| **Discord Roles mapping** | Maps a Discord role to a Keycloak group name. See [Syncing roles](#syncing-roles). |
| **Skip Discord prompt** | Ask Discord not to re-prompt users who already authorised the application. |
| **Additional scopes** | Extra Discord OAuth scopes to request, e.g. `connections`. Discord returns a scope's data only when it was asked for, so a custom mapper reading anything beyond the profile needs its scope named here. |
| **Store the user's guilds** | Records every guild the user belongs to as the `discord-guilds` claim, for an Attribute Importer mapper. Requests the `guilds` scope automatically. |
| **Accept unverified email addresses** | **Leave this off.** See [Unverified emails](#unverified-emails). |
| **Do not request the email scope** | Broker accounts without asking Discord for an email at all. Users then arrive with no email, so the realm must not require one. |

`identify`, `email`, `guilds` and `guilds.members.read` are requested automatically whenever the settings
above imply them; you only need **Additional scopes** for anything beyond that.


### Syncing roles

To sync roles from Discord -> Keycloak, do the following:

Syncing happens in two stages, and understanding the split is what makes it configurable:

1. **The identity provider resolves Discord roles into names.** On each login it asks Discord which of the
   mapped roles the user holds, and puts the matching names into a claim called `discord-groups`.
2. **A mapper turns those names into Keycloak objects** — either groups or roles. This is the stage you choose.

### Stage 1 — the role mapping

Under the `discord` Identity Provider, fill out `Discord Roles mapping`, **one mapping per row**:

- The format is `Discord-Guild-ID:Discord-Role-ID:Name`, e.g. `613425648685547541:613426529623605268:discord-devs-moderators`
- To match anyone who is simply a member of the guild, use the guild id in place of the role id (that is the
  `@everyone` role): `613425648685547541:613425648685547541:everyone`

Older versions stored these as one comma-separated string. Those values are still read, so upgrading does not
require re-entering them.

### Stage 2a — into Keycloak **groups**

- Add a Mapper under the `discord` Identity Provider
- Mapper Type: `Claim to Group Mapper`
- Claim: `discord-groups`
- Tick *Create Groups if not exists*

### Stage 2b — into Keycloak **roles**

Use this instead when you want realm roles rather than group membership.

- Add a Mapper under the `discord` Identity Provider
- Mapper Type: `Claim to Role`
- Claim: `discord-groups`
- *Managed role prefix*: optional, and it decides whether roles are ever **revoked**:
  - **empty** — the mapper only ever grants. Safe, but a user keeps a role after losing it in Discord.
  - **set** (e.g. `discord-`) — roles with that prefix are granted when Discord says so and revoked when it
    stops. Roles without the prefix are never touched, so an administrator's manual grant survives.
- *Create missing roles*: off by default, so a typo in a mapping does not silently populate the realm.

The two mappers can run together: the same claim can feed groups and roles at once.

If syncing does not seem to take effect, check the **Sync mode override** on the mapper — `Force` re-imports
on every login, rather than only when the user is first created.


## What this fork adds

On top of [FTBTeam](https://github.com/FTBTeam/keycloak-discord)'s Keycloak 26.5 base and Discord role sync:

| Change | Upstream issue |
| --- | --- |
| Unverified Discord emails are no longer trusted | [#46](https://github.com/wadahiro/keycloak-discord/issues/46) |
| `Skip Discord prompt` can be turned back off | [#59](https://github.com/wadahiro/keycloak-discord/issues/59) |
| `Additional scopes` setting | [#43](https://github.com/wadahiro/keycloak-discord/issues/43) |
| The user's guilds stored as `discord-guilds` | [#39](https://github.com/wadahiro/keycloak-discord/issues/39) |
| `Claim to Role` mapper — Discord roles into Keycloak **roles** | [#7](https://github.com/wadahiro/keycloak-discord/issues/7) |
| Avatars exposed as the OIDC-standard `picture` attribute | [PR #64](https://github.com/wadahiro/keycloak-discord/pull/64) |
| The email scope can be skipped entirely, per provider | [PR #62](https://github.com/wadahiro/keycloak-discord/pull/62) |
| One input box per guild and per role mapping | — |

[#51](https://github.com/wadahiro/keycloak-discord/issues/51) (`NoSuchMethodError` on registration) needs no
fix here: it came from a jar built against an older Keycloak, and this fork's 26.5 base already uses the
constructor that trace was missing.

### Unverified emails

Discord returns an email address **before the account has confirmed it**. Keycloak's first-broker-login can
link a brokered identity to an existing local user *by email*, so trusting an unverified one lets anybody who
registers a Discord account against someone else's address sign in as them.

Unverified addresses are therefore dropped, with a warning in the server log. `Accept unverified email
addresses` restores the previous behaviour for realms where no local account can be matched by email — it is
off by default deliberately.

### On listing guilds and roles in the admin console

A dropdown of the guilds and roles you can actually pick from is **not possible**, and it is worth writing down
so the idea does not keep resurfacing. The options for a Keycloak config property come from
`getConfigProperties()`, a factory method with no session and no access to a particular provider's settings —
so it cannot call Discord. Populating one live would need a custom admin console page, and Keycloak 26 offers
no supported extension point for adding one to an identity provider's settings screen.

What is possible, and what this fork does, is give each entry its own input box with add/remove.

## Source Build

Clone this repository and run `mvn package`.
You can see `keycloak-discord-<version>.jar` under `target` directory.


## Licence

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)


## Author

- [Hiroyuki Wada](https://github.com/wadahiro)

