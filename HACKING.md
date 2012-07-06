## Releasing

This is a reminder to the developers:

First, make sure you have the proper keys set up - in your `~/.m2/settings.xml` - for example:

```
<settings>
  <servers>
    <!-- See https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide -->
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>yoursonatypeuser</username>
      <password>TOPSECRET</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>yoursonatypeuser</username>
      <password>TOPSECRET</password>
    </server>
  </servers>
</settings>
```

Replace version numbers in:

* README.md
* History.md

git commit -m "Release X.Y.Z", then release everything:

```
mvn release:clean
mvn --batch-mode -P release-sign-artifacts release:prepare -DdevelopmentVersion=0.3.1-SNAPSHOT
mvn -P release-sign-artifacts release:perform
```
