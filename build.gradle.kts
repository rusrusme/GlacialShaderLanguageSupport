plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0"
}

group = "net.rusrusme.gshader"
version = "0.1.0"

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