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

This repository is a single Java library module (Module Type: LIBRARY; Runtime: None).

## Documentation

| Document | Purpose |
| --- | --- |
| [Contributing](CONTRIBUTING.md) | Contribution and development notes. |
| [Repository Git Workflow](docs/quality/GIT_WORKFLOW.md) | Applicable repository guidance. |

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
| GitHub | PRIMARY | TavallStudios/tavall-reflection/README.md | 2026-09-27 12:29 PM PDT | Migration PR. |
| Notion | NOT_APPLICABLE | — | 2026-09-27 12:29 PM PDT | README files are not synchronized as Notion twins. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:29 PM PDT | GitHub | UPDATED | TavallStudios/tavall-reflection/README.md | Same path | Migration PR. | Reworked the public README to describe the current project, module boundary, usage, and documentation. |

</details>
