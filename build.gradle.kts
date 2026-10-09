plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0"
}

group = "net.rusrusme.bsh"
version = "1.0-SNAPSHOT"

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
        sourceFile.set(file("src/main/java/net/rusrusme/bsh/grammar/BshLexer.flex"))
        targetRootOutputDir.set(file("src/main/gen/net/rusrusme/bsh/lexer"))
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/java/net/rusrusme/bsh/grammar/Bsh.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("net/rusrusme/bsh/parser/BshParser.java")
        pathToPsiRoot.set("net/rusrusme/bsh/psi")
        purgeOldFiles.set(true)
    }

    compileJava {
        dependsOn(generateLexer, generateParser)
    }
}