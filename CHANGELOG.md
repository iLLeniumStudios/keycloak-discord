# [0.7.0](https://github.com/iLLeniumStudios/keycloak-discord/compare/v0.6.1...v0.7.0) (2026-07-29)


### Bug Fixes

* maven deploys ([7f86498](https://github.com/iLLeniumStudios/keycloak-discord/commit/7f8649850563c0948f84e04b5f50b100f66e17a7))
* reject unverified Discord emails, and repair the prompt toggle ([d5af08a](https://github.com/iLLeniumStudios/keycloak-discord/commit/d5af08aa1c8e8c4d24f30138740717b8355f69d1)), closes [#46](https://github.com/iLLeniumStudios/keycloak-discord/issues/46) [#59](https://github.com/iLLeniumStudios/keycloak-discord/issues/59) [#43](https://github.com/iLLeniumStudios/keycloak-discord/issues/43) [#39](https://github.com/iLLeniumStudios/keycloak-discord/issues/39) [#46](https://github.com/iLLeniumStudios/keycloak-discord/issues/46) [#59](https://github.com/iLLeniumStudios/keycloak-discord/issues/59) [#43](https://github.com/iLLeniumStudios/keycloak-discord/issues/43) [#39](https://github.com/iLLeniumStudios/keycloak-discord/issues/39) [#51](https://github.com/iLLeniumStudios/keycloak-discord/issues/51)
* releases ([dae36e9](https://github.com/iLLeniumStudios/keycloak-discord/commit/dae36e956f5e275f9ce76e52c8a83ac01d78b006))


### Features

* a Claim to Role mapper, and a README that explains the two stages ([fa28cbf](https://github.com/iLLeniumStudios/keycloak-discord/commit/fa28cbface24f8ae222ebb8afcebc013f18e2518)), closes [#7](https://github.com/iLLeniumStudios/keycloak-discord/issues/7)
* discord role sync ([9f6c287](https://github.com/iLLeniumStudios/keycloak-discord/commit/9f6c28757819d97f45f758177b934275c7c613bd))
* discord role sync ([e98c2e5](https://github.com/iLLeniumStudios/keycloak-discord/commit/e98c2e52aaf72afe1e8d5751f94dea824c8b85cb))
* one input box per guild and per role mapping, and the first tests ([5936aaf](https://github.com/iLLeniumStudios/keycloak-discord/commit/5936aaf31df087aafa64812d9505d23db1644145)), closes [#59](https://github.com/iLLeniumStudios/keycloak-discord/issues/59)
* optional email scope, and an OIDC-compliant picture attribute ([a8bd873](https://github.com/iLLeniumStudios/keycloak-discord/commit/a8bd873feffcda005402cd562542db7ed2e31ba3)), closes [#62](https://github.com/iLLeniumStudios/keycloak-discord/issues/62) [#64](https://github.com/iLLeniumStudios/keycloak-discord/issues/64)
* **release:** changes ([4b58367](https://github.com/iLLeniumStudios/keycloak-discord/commit/4b58367b5db8ff2aa0f2474cbef7848b0345e3a8))
* **release:** updated java ([5fbfac3](https://github.com/iLLeniumStudios/keycloak-discord/commit/5fbfac3fb7d9dd82ada89d0f65bf90b852234ad9))
* update to keycloak 26.0.5 ([bb66494](https://github.com/iLLeniumStudios/keycloak-discord/commit/bb66494085a170d046d2c377e53548146cf90fd9))
* update to keycloak 26.2.5 ([edf6dbb](https://github.com/iLLeniumStudios/keycloak-discord/commit/edf6dbb35464353b6bcbfdf5c652e864bb557cce))
* update to keycloak 26.2.5 ([9f0673a](https://github.com/iLLeniumStudios/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))
* update to keycloak 26.4.5 ([596006e](https://github.com/iLLeniumStudios/keycloak-discord/commit/596006e5284147a18ebb684701b61d06e1396596))

# [1.2.0](https://github.com/FTBTeam/keycloak-discord/compare/v1.1.0...v1.2.0) (2026-01-26)


### Features

* **release:** changes ([4b58367](https://github.com/FTBTeam/keycloak-discord/commit/4b58367b5db8ff2aa0f2474cbef7848b0345e3a8))

# [1.1.0](https://github.com/FTBTeam/keycloak-discord/compare/v1.0.1...v1.1.0) (2025-12-01)


### Features

* **release:** updated java ([5fbfac3](https://github.com/FTBTeam/keycloak-discord/commit/5fbfac3fb7d9dd82ada89d0f65bf90b852234ad9))

## [1.0.1](https://github.com/FTBTeam/keycloak-discord/compare/v1.0.0...v1.0.1) (2025-12-01)


### Bug Fixes

* releases ([dae36e9](https://github.com/FTBTeam/keycloak-discord/commit/dae36e956f5e275f9ce76e52c8a83ac01d78b006))

# 1.0.0 (2025-12-01)


### Bug Fixes

* eliminate the possibility of NullPointerException ([#56](https://github.com/FTBTeam/keycloak-discord/issues/56)) ([e2b5991](https://github.com/FTBTeam/keycloak-discord/commit/e2b5991c7ef33ee3ac6483ca00e3e854bb935371))
* ignore discriminator if the value is "0" ([4c68b69](https://github.com/FTBTeam/keycloak-discord/commit/4c68b69b0bf0d6421589e03e4baf8bbbe5138caa))
* maven deploys ([7f86498](https://github.com/FTBTeam/keycloak-discord/commit/7f8649850563c0948f84e04b5f50b100f66e17a7))
* update authorization URL ([#28](https://github.com/FTBTeam/keycloak-discord/issues/28)) ([a31c116](https://github.com/FTBTeam/keycloak-discord/commit/a31c116092183318fdbe09fa059a1f63bb0d794c))
* update discord api endpoint urls ([0aa7f14](https://github.com/FTBTeam/keycloak-discord/commit/0aa7f14edf48a5f9e8a75a68f530cfb1945935e9))
* use DiscordIdentityProvider class in instantiating the Logger ([93003c2](https://github.com/FTBTeam/keycloak-discord/commit/93003c28bc377577b5d4b218e3d89d984a3f1ae6))


### Features

* discord role sync ([9f6c287](https://github.com/FTBTeam/keycloak-discord/commit/9f6c28757819d97f45f758177b934275c7c613bd))
* discord role sync ([e98c2e5](https://github.com/FTBTeam/keycloak-discord/commit/e98c2e52aaf72afe1e8d5751f94dea824c8b85cb))
* implement IdentityProviderFactory#createConfig() which is ([9f359f5](https://github.com/FTBTeam/keycloak-discord/commit/9f359f5f61848131f1b19994a2c75d6955bb9b27))
* update to keycloak 17.0.0 (based on Quarkus) ([f24ed18](https://github.com/FTBTeam/keycloak-discord/commit/f24ed1801141da1fbb58d0a58fafdb5aeb0bfa6e))
* update to keycloak 22.0.1 (support new admin console) ([#37](https://github.com/FTBTeam/keycloak-discord/issues/37)) ([2c8da2d](https://github.com/FTBTeam/keycloak-discord/commit/2c8da2da4b48ea081644deb7fc11931aba55ba8f))
* update to keycloak 26.0.5 ([bb66494](https://github.com/FTBTeam/keycloak-discord/commit/bb66494085a170d046d2c377e53548146cf90fd9))
* update to keycloak 26.0.5 ([05a3df4](https://github.com/FTBTeam/keycloak-discord/commit/05a3df43f21289762f72e3ac6780fd6b543d8c07))
* update to keycloak 26.2.5 ([edf6dbb](https://github.com/FTBTeam/keycloak-discord/commit/edf6dbb35464353b6bcbfdf5c652e864bb557cce))
* update to keycloak 26.2.5 ([9f0673a](https://github.com/FTBTeam/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))
* update to keycloak 26.4.5 ([596006e](https://github.com/FTBTeam/keycloak-discord/commit/596006e5284147a18ebb684701b61d06e1396596))
* updated provider for Keycloak 25.x ([#49](https://github.com/FTBTeam/keycloak-discord/issues/49)) ([c10480b](https://github.com/FTBTeam/keycloak-discord/commit/c10480b79864a85817d20d48f949475020322090))
* use "Theme Resources" for the custom template ([7cb885c](https://github.com/FTBTeam/keycloak-discord/commit/7cb885cea7ae317ae071a2ac72956faa67d4fdf9))

# [0.9.0](https://github.com/maaroen/keycloak-discord/compare/v0.8.0...v0.9.0) (2025-11-20)


### Features

* update to keycloak 26.4.5 ([596006e](https://github.com/maaroen/keycloak-discord/commit/596006e5284147a18ebb684701b61d06e1396596))

# [0.8.0](https://github.com/maaroen/keycloak-discord/compare/v0.7.0...v0.8.0) (2025-07-02)


### Features

* update to keycloak 26.0.5 ([bb66494](https://github.com/maaroen/keycloak-discord/commit/bb66494085a170d046d2c377e53548146cf90fd9))
* update to keycloak 26.2.5 ([edf6dbb](https://github.com/maaroen/keycloak-discord/commit/edf6dbb35464353b6bcbfdf5c652e864bb557cce))
* update to keycloak 26.2.5 ([9f0673a](https://github.com/maaroen/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))

# [0.8.0](https://github.com/maaroen/keycloak-discord/compare/v0.7.0...v0.8.0) (2025-07-02)


### Features

* update to keycloak 26.0.5 ([bb66494](https://github.com/maaroen/keycloak-discord/commit/bb66494085a170d046d2c377e53548146cf90fd9))
* update to keycloak 26.2.5 ([edf6dbb](https://github.com/maaroen/keycloak-discord/commit/edf6dbb35464353b6bcbfdf5c652e864bb557cce))
* update to keycloak 26.2.5 ([9f0673a](https://github.com/maaroen/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))

# [0.9.0](https://github.com/maaroen/keycloak-discord/compare/v0.8.0...v0.9.0) (2025-07-02)


### Features

* update to keycloak 26.2.5 ([edf6dbb](https://github.com/maaroen/keycloak-discord/commit/edf6dbb35464353b6bcbfdf5c652e864bb557cce))

# [0.8.0](https://github.com/maaroen/keycloak-discord/compare/v0.7.0...v0.8.0) (2025-07-02)


### Features

* update to keycloak 26.2.5 ([9f0673a](https://github.com/maaroen/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))

# [0.8.0](https://github.com/maaroen/keycloak-discord/compare/v0.7.0...v0.8.0) (2025-07-02)


### Features

* update to keycloak 26.2.5 ([9f0673a](https://github.com/maaroen/keycloak-discord/commit/9f0673a5de15ad7e2a813a99aba9ed21cec0c596))

# [0.7.0](https://github.com/maaroen/keycloak-discord/compare/v0.6.1...v0.7.0) (2025-03-08)

### Features
* Updated to keycloak 26.1.3
* Added Discord role syncing support (work done by [NotActuallyTerry](https://github.com/NotActuallyTerry/keycloak-discord))
* Added a fix to also sync roles (delete all) if no roles were returned for the discord guild (Inspired by [pierrearma](https://github.com/NotActuallyTerry/keycloak-discord/pull/2))


## [0.6.1](https://github.com/wadahiro/keycloak-discord/compare/v0.6.0...v0.6.1) (2024-11-02)


### Bug Fixes

* eliminate the possibility of NullPointerException ([#56](https://github.com/wadahiro/keycloak-discord/issues/56)) ([e2b5991](https://github.com/wadahiro/keycloak-discord/commit/e2b5991c7ef33ee3ac6483ca00e3e854bb935371))

# [0.6.0](https://github.com/wadahiro/keycloak-discord/compare/v0.5.0...v0.6.0) (2024-11-02)


### Bug Fixes

* ignore discriminator if the value is "0" ([4c68b69](https://github.com/wadahiro/keycloak-discord/commit/4c68b69b0bf0d6421589e03e4baf8bbbe5138caa))


### Features

* update to keycloak 26.0.5 ([05a3df4](https://github.com/wadahiro/keycloak-discord/commit/05a3df43f21289762f72e3ac6780fd6b543d8c07))
* updated provider for Keycloak 25.x ([#49](https://github.com/wadahiro/keycloak-discord/issues/49)) ([c10480b](https://github.com/wadahiro/keycloak-discord/commit/c10480b79864a85817d20d48f949475020322090))
