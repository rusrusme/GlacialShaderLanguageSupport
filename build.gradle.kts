plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0"
}

val pluginVersion = providers.gradleProperty("pluginVersion")
val pluginGroup = providers.gradleProperty("pluginGroup")

group = pluginGroup.get()
version = pluginVersion.get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        clion("2026.2.3")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen", "src/main/java")
        }
    }
}

tasks {
    generateLexer {
        sourceFile.set(file("src/main/java/net/rusrusme/gshader/grammar/GLexer.flex"))
        targetRootOutputDir.set(file("src/main/gen/net/rusrusme/gshader/lexer"))
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/java/net/rusrusme/gshader/grammar/GShader.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("net/rusrusme/gshader/parser/GParser.java")
        pathToPsiRoot.set("net/rusrusme/gshader/psi")
        purgeOldFiles.set(true)
    }

    compileJava {
        dependsOn(generateLexer, generateParser)
    }
}

intellijPlatform {
    publishing {
        token = providers.gradleProperty("jetbrainsMarketplaceToken")
    }

    pluginConfiguration {
        version = pluginVersion

        description = provider {
            """
            <p>Provides syntax highlighting, lexing, and language support for Glacial Shader (<code>.gshader</code>) files in CLion.</p>
            <h3>Features</h3>
            <ul>
                <li>Syntax highlighting for multi-stage shaders (<code>#vert</code>, <code>#frag</code>, <code>#comp</code>, <code>#geom</code>)</li>
                <li>Lexing and AST parsing via Grammar-Kit</li>
                <li>Code formatting, brace matching, and commenting</li>
                <li>Code completion for GLSL types, layout qualifiers, and keywords</li>
            </ul>
            """.trimIndent()
        }

        changeNotes = pluginVersion.map { ver ->
            """
            <h3>$ver</h3>
            <ul>
                <li>Pre-Release</li>
                <li>Syntax highlighting and lexing for <code>.gshader</code> files.</li>
                <li>Code formatting, brace matching, and commenting support.</li>
                <li>Keyword and type code completion.</li>
            </ul>
            """.trimIndent()
        }
    }
}