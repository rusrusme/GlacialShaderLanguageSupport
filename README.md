# Glacial Shader Language for CLion

Language support plugin for the Glacial Shader Language (`.gshader`) used in GlacialEngine.

## Features
- **Syntax Highlighting & Lexing:** Complete coverage for stages (`#vert`, `#frag`, `#comp`, `#geom`), preprocessor directives, and standard GLSL types.
- **Code Formatting:** Configurable brace matching, indentation, and structure formatting.
- **Completion:** Auto-completion for GShader keywords, types, and qualifiers.
- **File Templates:** Quick creation of new `.gshader` files via the `New -> Glacial Shader File` menu.

## Installation
- Install directly from the [JetBrains Marketplace](https://plugins.jetbrains.com).
- Or download the latest `.zip` release and install via `Settings -> Plugins -> ⚙️ -> Install Plugin from Disk...`.

## Building
```bash
./gradlew buildPlugin
```

For language grammar and architectural details, see the [Language Specification](SPECIFICATIONS.md).

---

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.