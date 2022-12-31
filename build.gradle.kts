import kotlinx.benchmark.gradle.*

plugins {
    kotlin("jvm") version "1.7.22"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.6")
    //implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.7.22")
}

benchmark {
    configurations {
        named("main") {
            iterationTime = 5
            iterationTimeUnit = "sec"
        }
    }
    targets {
        register("main") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }
    wrapper {
        gradleVersion = "7.6"
    }
}
