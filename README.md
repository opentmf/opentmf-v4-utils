# opentmf-v4-utils

Common utility methods for any TMF v4 implementation whose model classes implement
[opentmf-v4-api](https://github.com/opentmf/opentmf-v4-api) interfaces.

Unlike the model and API projects, this is **not generated** — it is hand-written and extended
on demand as new utility needs arise. Contributions are welcome.

Known `opentmf-v4-api` implementations:

| Implementation | Scope |
|---|---|
| **opentmf-v4-models** | Full artifact set — all TMF v4 modules |
| **dnext-v4-models** | Partial artifact set |

## Modules

### opentmf-common-v4-util

#### CharacteristicUtil

Lookup and type-convert characteristics by name from any `Collection<? extends ICharacteristic>`.

| Method | Description |
|---|---|
| `findCharacteristicByName` | Find a characteristic by name, returning `Optional` |
| `getMandatoryCharacteristic` | Get a characteristic by name or throw |
| `getOptionalCharacteristic*Value` | Get optional value as String, Boolean, Integer, Long, or OffsetDateTime |
| `getMandatoryCharacteristic*Value` | Get mandatory value as String, Boolean, Integer, Long, or OffsetDateTime |
| `toNameObjectMap` / `toNameStringMap` | Convert a characteristic collection to a `Map` |
| `detectDuplicates` | Detect duplicate characteristic names |

#### CharacteristicSpecificationUtil

| Method | Description |
|---|---|
| `isMandatory` | Check if a specification has min = max cardinality = 1 |
| `allowedValues` | Extract allowed values from value specifications |

#### NoteUtil

| Method | Description |
|---|---|
| `noteListContainsText` | Check if any note in the collection contains a given text |

#### RelatedPartyUtil

Lookup related parties by role and/or referred type.

| Method | Description |
|---|---|
| `findRelatedPartyByRole` | Find first party by role or throw |
| `findOptionalRelatedPartyByRole` | Find first party by role, returning `Optional` |
| `findUniqueRelatedPartyByRole` | Find exactly one party by role or throw |
| `findCustomerParty` | Shorthand: referred type "Customer", role "customer" |
| `findOperatorParty` | Shorthand: referred type "Organization", role "operator" |
| `findSupplierParty` | Shorthand: referred type "Organization", role "supplier" |

### opentmf-622-v4-util

#### ProductOrderUtil

| Method | Description |
|---|---|
| `findProductOrderItemById` | Find an order item by id or throw |
| `findProductCharacteristicsBySpecificationId` | Get characteristics of the product matching a specification id |
| `isBundle` | Check if an order item has a "bundles" relationship |
| `validateOrder` | Validate the order graph: start/end nodes exist, all references resolve, no circular dependencies |

### opentmf-641-v4-util

#### ServiceOrderUtil

| Method | Description |
|---|---|
| `findServiceOrderItemById` | Find an order item by id or throw |
| `validateOrder` | Validate the order graph: start/end nodes exist, all references resolve, no circular dependencies |

## Changelog

See [CHANGELOG.md](CHANGELOG.md).
