# Dependencies and security updates
## Check single dependency

For example to check the dependency `netty-codec-http` in the `testRuntimeClasspath`
```bash
./gradlew dependencyInsight --dependency netty-codec-http --configuration tRC
```