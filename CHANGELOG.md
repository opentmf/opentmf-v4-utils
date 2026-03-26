# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [2.0.0] - 2026-03-26

### Changed
- Compile-time dependencies switched from concrete model classes (`opentmf-*-v4-model`) to
  API interfaces (`opentmf-*-v4-api`). All utility method signatures now use interface types
  (e.g. `ICharacteristic`, `IProductOrderItem`, `IServiceOrder`), allowing any model
  implementation (vanilla, dnext, etc.) to be used with these utilities.
- Method parameters accepting collections now use `Collection<? extends IType>` wildcards
  for broader compatibility.
- Concrete model dependencies retained in `test` scope only.
- Spring Boot BOM upgraded from 3.4.4 to 4.0.4 to align with the model ecosystem.
- API and model dependency versions updated to 4.x.y.10 releases.

## [1.0.5]

## Changed
- Initial open-source version


## [1.0.4]

### Added
- `validateOrder` method to `ServiceOrderUtil`, ensuring service order processing will not
  fail due to missing references and circular dependencies.

## [1.0.3]

### Changed
- Updated to the newest tmf-v4-models versions.

## [1.0.2]

### Changed
- Updated to the newest tmf-v4-models versions.

### Removed
- tmf-common-v4-util dependencies.

## [1.0.1]

### Changed
- Updated to the newest tmf-v4-models versions.

## [1.0.0]

### Added
- Initial version.
