#!/bin/bash
# Script to download and install missing Saiku dependencies into the local Maven repo.
# These JARs are no longer available in public Maven repos.
#
# Prerequisites: Maven 3.x and Java 8 must be installed and JAVA_HOME set.
#
# Usage:
#   ./scripts/install-missing-deps.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPS_DIR="$SCRIPT_DIR/../lib-ext"
mkdir -p "$DEPS_DIR"

echo "=== Installing Missing Saiku Dependencies ==="

# 1. Mondrian 4.7.0.0-12 (OLAP engine, not in any Maven repo)
MONDRIAN_VERSION="4.7.0.0-12"
MONDRIAN_JAR="mondrian-${MONDRIAN_VERSION}.jar"
if [ ! -f "$DEPS_DIR/$MONDRIAN_JAR" ]; then
    echo "Downloading Mondrian ${MONDRIAN_VERSION} from SourceForge..."
    curl -L -o "$DEPS_DIR/$MONDRIAN_JAR" \
        "https://downloads.sourceforge.net/project/mondrian/mondrian/mondrian-4.7.0/mondrian-4.7.0.0-12.jar"
fi
echo "Installing Mondrian to local Maven repository..."
mvn install:install-file \
    -Dfile="$DEPS_DIR/$MONDRIAN_JAR" \
    -DgroupId=pentaho \
    -DartifactId=mondrian \
    -Dversion="${MONDRIAN_VERSION}" \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -q

# 2. saiku-query (query builder library, original repo dead)
#    Built from source at https://github.com/pstoellberger/saiku-query
#    with patches for API compatibility with Saiku 3.17
SAIKU_QUERY_DIR="/tmp/saiku-query"
if [ ! -d "$SAIKU_QUERY_DIR" ]; then
    echo "Cloning saiku-query from GitHub..."
    git clone https://github.com/pstoellberger/saiku-query.git "$SAIKU_QUERY_DIR"
fi
echo "Building and installing saiku-query..."
pushd "$SAIKU_QUERY_DIR" > /dev/null
mvn clean install -DskipTests -q
# Install with the coordinates the Saiku POM expects
mvn install:install-file \
    -Dfile=target/saiku-query-0.1-SNAPSHOT.jar \
    -DgroupId=com.github.pstoellberger \
    -DartifactId=saiku-query \
    -Dversion=6bd1e0a \
    -Dpackaging=jar \
    -q
popd > /dev/null

# 3. miredot-annotations (API doc annotations, original repo dead)
#    Only the @ReturnType annotation is needed - we build a minimal stub
MIREDOT_DIR="/tmp/miredot-stub"
if [ ! -d "$MIREDOT_DIR" ]; then
    echo "Creating miredot-annotations stub..."
    mkdir -p "$MIREDOT_DIR/src/main/java/com/qmino/miredot/annotations"
    cat > "$MIREDOT_DIR/pom.xml" << 'POMEOF'
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.qmino</groupId>
    <artifactId>miredot-annotations</artifactId>
    <version>1.3.1</version>
    <packaging>jar</packaging>
    <properties>
        <maven.compiler.source>1.7</maven.compiler.source>
        <maven.compiler.target>1.7</maven.compiler.target>
    </properties>
</project>
POMEOF
    cat > "$MIREDOT_DIR/src/main/java/com/qmino/miredot/annotations/ReturnType.java" << 'JAVAEOF'
package com.qmino.miredot.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReturnType {
    String value();
}
JAVAEOF
fi
echo "Building and installing miredot-annotations stub..."
pushd "$MIREDOT_DIR" > /dev/null
mvn clean install -q
popd > /dev/null

echo ""
echo "=== Done installing dependencies ==="
echo "Maven local repo updated with:"
echo "  - pentaho:mondrian:4.7.0.0-12"
echo "  - com.github.pstoellberger:saiku-query:6bd1e0a"
echo "  - com.qmino:miredot-annotations:1.3.1"
