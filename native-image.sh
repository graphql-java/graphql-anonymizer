#!/usr/bin/env bash
set -ex
#./gradlew clean shadowJar
native-image \
#-cp ./build/libs/graphql-anonymizer-1.0-SNAPSHOT-all.jar \
#-H:Name=graphql-anonymizer \
#-H:Class=Main \
#--verbose \
#-H:+ReportUnsupportedElementsAtRuntime \
#--no-fallback \
#--allow-incomplete-classpath \
#-H:+ReportExceptionStackTraces \
#--no-server

native-image -H:+ReportExceptionStackTraces -H:+ReportUnsupportedElementsAtRuntime --verbose --no-server --no-fallback --allow-incomplete-classpath -jar ./build/libs/graphql-anonymizer-1.0.0-all.jar
