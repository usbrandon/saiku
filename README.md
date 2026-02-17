<a href="#readme"></a>

<h1 align="center">Saiku Analytics</h1>
<h2 align="center">Open Source OLAP Browser</h2>

<p align="justify">
  Saiku allows business users to explore complex data sources,
  using a familiar drag and drop interface and easy to understand
  business terminology, all within a browser. Select the data you
  are interested in, look at it from different perspectives,
  drill into the detail. Once you have your answer, save your results,
  share them, export them to Excel or PDF, all straight from the browser.
</p>

***

## Building from Source

Saiku 3.17 requires **Java 8** and **Maven 3.6+**. Several dependencies are no longer
available from their original Maven repositories and must be installed locally before building.

### Prerequisites

| Requirement | Notes |
|------------|-------|
| **Java 8 (JDK)** | OpenJDK 8 or Zulu JDK 8. On Apple Silicon Macs, use [Azul Zulu JDK 8](https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=arm-64-bit&package=jdk) since OpenJDK 8 only supports x86_64. |
| **Maven 3.6+** | `brew install maven` or download from [maven.apache.org](https://maven.apache.org/download.cgi). Maven 3.9+ works but requires the settings.xml fix described below. |
| **Git** | To clone saiku-query dependency from GitHub. |

### Step 1: Configure Maven Settings

Copy the provided settings file to your Maven config directory:

```sh
cp docker-maven-settings.xml ~/.m2/settings.xml
```

This file configures HTTPS repository mirrors and overrides Maven 3.9's default HTTP
repository blocker (many old transitive dependencies reference HTTP-only repos in their POMs).

### Step 2: Install Missing Dependencies

Several dependencies are no longer hosted on public Maven repositories. The install script
downloads and builds them into your local Maven repo (`~/.m2/repository`):

```sh
chmod +x scripts/install-missing-deps.sh
./scripts/install-missing-deps.sh
```

This installs:
- **Mondrian 4.7.0.0-12** (OLAP engine) - downloaded from SourceForge
- **saiku-query** (query builder) - cloned from GitHub and built from source
- **miredot-annotations 1.3.1** (API doc annotations) - stub with just `@ReturnType`

### Step 3: Build

```sh
export JAVA_HOME=/path/to/java8
mvn clean install -DskipTests -Ddependency-check.skip=true
```

The build produces:
- `saiku-server/target/saiku-server-foodmart-3.17.zip` - standalone server distribution
- `saiku-webapp/target/saiku-webapp-3.17.war` - deployable WAR file

Build takes approximately 20 seconds on a modern machine.

### Docker Build (Alternative)

If you have Docker installed, you can build without installing Java or Maven locally:

```sh
./build.sh full
```

This uses `Dockerfile.build` with `maven:3.6.3-amazoncorretto-8` as the base image.

## Running Saiku

### From Build Output

```sh
export JAVA_HOME=/path/to/java8
cd saiku-server/target/dist/saiku-server
sh start-saiku.sh
```

### From Distribution Zip

```sh
unzip saiku-server/target/saiku-server-foodmart-3.17.zip
cd saiku-server
export JAVA_HOME=/path/to/java8
sh start-saiku.sh
```

Then open **http://localhost:8080/** in your browser. Default credentials: **admin / admin**.

To stop the server:
```sh
sh stop-saiku.sh
```

To change the port, edit `tomcat/conf/server.xml`.

The distribution includes a sample Foodmart OLAP database. To add your own data sources,
see the [wiki](https://github.com/OSBI/saiku/wiki/Adding-a-new-data-source).

## What Changed from Upstream OSBI/saiku

This fork includes build fixes to make Saiku 3.17 compilable with modern tooling,
since many of the original dependency repositories are no longer available.

### Dependency Replacements

| Original Dependency | Problem | Replacement |
|---|---|---|
| `bi.meteorite:licenseserver-core` | Commercial, unavailable | `saiku-core/saiku-license-stub/` (no-op stubs, community edition always valid) |
| `org.saiku:saiku-query:0.4-SNAPSHOT` | Analytical Labs repo dead | `com.github.pstoellberger:saiku-query:6bd1e0a` (built from [GitHub source](https://github.com/pstoellberger/saiku-query)) |
| `iText:iText:4.2.0` | Old groupId, not on Maven Central | `com.lowagie:itext:2.1.7` (same `com.lowagie.text` package) |
| `com.qmino:miredot-annotations:1.3.1` | qmino repo dead | Stub JAR with just `@ReturnType` annotation |
| `pentaho:mondrian:4.7.0.0-12` | Not in any Maven repo | Manually installed from [SourceForge](https://sourceforge.net/projects/mondrian/) |

### POM Fixes

- **Root `pom.xml`**: Replaced all HTTP repository URLs with HTTPS equivalents. Removed dead
  repos (Analytical Labs, Pentaho Nexus, miredot/qmino). Added JitPack repository.
  Changed `calcite.version` from `0.9.2-incubating-SNAPSHOT` to `1.0.0-incubating`.
- **`saiku-ui/pom.xml`**: Changed `minify-maven-plugin` from custom `1.7.4-modify` to
  standard `1.7.4` with explicit `plexus-utils` dependency (required for Maven 3.9+).
  Removed dead repos.
- **`saiku-core/saiku-web/pom.xml`**: Changed iText dependency coordinates.

### Code Fixes

- **`LicenseUtils.java`**: Returns a default `SaikuLicense2` (community edition) when no
  license file exists, instead of throwing an exception.
- **`Fat.java`**: Removed unused `MondrianOlap4jLevel` import (class is package-private
  in Mondrian 4.7).
- **`MarkLogicRepositoryManager.java`**: Removed (MarkLogic dependencies unavailable).
- **`RepositoryDatasourceManager.java`**: Removed MarkLogic references.

### License Stub Module

The `saiku-core/saiku-license-stub/` module provides no-op replacements for the commercial
`bi.meteorite:licenseserver-core` library:

- `EncryptionManager` - all crypto operations return true/pass-through
- `SaikuLicense` / `SaikuLicense2` - community license, never expires, unlimited users
- `ILicense` - license interface with `Serializable`
- `LicenseException` / `LicenseVersionExpiredException` - exception classes

## Architecture

Saiku is a multi-module Maven project:

```
saiku/
├── saiku-core/
│   ├── saiku-license-stub/   # License server stubs (community edition)
│   ├── saiku-olap-util/      # OLAP utility classes
│   ├── saiku-service/        # Core business logic, datasource management
│   └── saiku-web/            # REST API endpoints, PDF/Excel export
├── saiku-ui/                 # JavaScript frontend (Backbone.js)
├── saiku-webapp/             # WAR packaging with Spring config
└── saiku-server/             # Standalone server with embedded Tomcat
```

Key technologies:
- **Mondrian 4.7** (EPL-1.0) - OLAP engine
- **olap4j 1.2** - Java OLAP API
- **Spring Framework 4.x** - dependency injection, security
- **Jersey 1.19** - JAX-RS REST framework
- **Apache Tomcat 9.0.8** - embedded servlet container

## Browser Support

| Edge | Chrome | Firefox | Opera | Safari |
|------|--------|---------|-------|--------|
| Latest | Latest | Latest | Latest | Latest |

## License

Saiku and the Saiku UI are free software, available under the terms of the
**Apache License Version 2.0**.

Mondrian (the OLAP engine dependency) is licensed under the
**Eclipse Public License v1.0**.

## Upstream

This is a fork of [OSBI/saiku](https://github.com/OSBI/saiku). The original project
was maintained by [Meteorite BI](http://www.meteorite.bi/).

**[back to top](#readme)**
