# tavall-reflection

A Java 25 library of low-level reflection and class-loading helpers.

ReflectUtil wraps selected Java reflection operations for classes, fields, methods, method-handle lookups, and file-based library loading.

## Why tavall-reflection

- Centralizes common reflection lookups used by Tavall systems.
- Provides overloads for field and method lookup with explicit accessibility handling.
- Keeps reflection helpers in one reusable library module.

## Features

- Class lookup by name
- Field and method lookup helpers
- Parent-aware method lookup
- MethodHandles.Lookup access helper
- File-library loading helpers

## Quick Start

Add the published artifact to a Gradle project:

```kotlin
dependencies {
    implementation("org.tavall:tavall-reflection:<version>")
}
```

Use the exact published version and repository access configured for your project. See the links below for API and contribution details.

## Project Structure

`tavall-reflection/` (root Gradle project)
- **Root Java source/build module** ← This Module — `src/main/java/`, `build.gradle.kts`

Module Type: `LIBRARY`; Runtime Owner: `None`.

## Documentation

| Document | Purpose |
| --- | --- |
| [Module Progression](docs/progression/TAVALL_REFLECTION_PROGRESSION.md) | Audited module implementation, integration, validation, and history. |
| [Contributing](CONTRIBUTING.md) | Contribution and development notes. |
| [Repository Git Workflow](docs/quality/GIT_WORKFLOW.md) | Applicable repository guidance. |

## Module Development

- **Module Type:** `LIBRARY`
- **Runtime Owner:** `None`
- **Current PR Stack:** README and module Progression [#10](https://github.com/TavallStudios/tavall-reflection/pull/10); platform integration [#6](https://github.com/TavallStudios/tavall-reflection/pull/6) (draft to `main`); CI localization [#7](https://github.com/TavallStudios/tavall-reflection/pull/7) (draft to `staging/platform`).
- **Module-local CI Definition:** Missing from current `main`: `.tavallci/ci.yaml` (required for a Tavall source/build module). This documentation PR records the gap and does not change CI configuration.

## Requirements / Compatibility

Java 25. Reflection may bypass ordinary encapsulation when accessibility handling is enabled; use only where the calling boundary permits it.

## Building From Source

```bash
./gradlew check
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

No tracked license file is present in the current repository tree.

## Documentation Update State

<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | PRIMARY | TavallStudios/tavall-reflection/README.md | 2026-09-27 5:33 PM PDT| PR [#17](https://github.com/TavallStudios/tavall-reflection/pull/10) updated to correct table formatting. |
| Notion | NOT_APPLICABLE | — | 2026-09-27 12:29 PM PDT | README files are not synchronized as Notion twins. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:29 PM PDT | GitHub | UPDATED | TavallStudios/tavall-reflection/README.md | Same path | https://github.com/TavallStudios/tavall-reflection/pull/10. | Reworked the public README to describe the current project, module boundary, usage, and documentation. |
| 2026-09-27 5:32 PM PDT | GitHub | UPDATED | TavallStudios/tavall-reflection/README.md | Same path | PR [#10](https://github.com/TavallStudios/tavall-reflection/pull/10) | Added the required root-module Progression route, visible module marker, classification, PR stack, and CI-definition state. |
| 2026-09-27 5:33 PM PDT | GitHub | UPDATED | TavallStudios/tavall-reflection/README.md | Same path | PR [#10](https://github.com/TavallStudios/tavall-reflection/pull/10) | Corrected the Documentation table row order; preserved module routing and development details. |

</details>
