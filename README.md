# GraalVM dynamics finder

A tool which can show reflection access with stacktraces in a Java application.

This works with the help of the [native image tracing agent](https://www.graalvm.org/22.2/reference-manual/native-image/metadata/AutomaticMetadataCollection/).

## Usage

Start your application with the native image tracing agent attached:

```
$JAVA_HOME/bin/java -agentlib:native-image-agent=config-output-dir=metadata,experimental-conditional-config-part -jar <your application>
```

and then run the `graalvm-dynamics-finder` on the generated `partial-config-with-origins.json` file:

```
graalvm-dynamics-finder --input metadata/partial-config-with-origins.json --output reflection-report.adoc --reflection-targets <the classes you are interested in>
```

This will generate a report which shows all reflection access to the classes you provided.
The report looks like [this example report](doc/example-report.adoc).

The example report has been created by running:

```
graalvm-dynamics-finder --input metadata/partial-config-with-origins.json --output example-report.adoc --reflection-targets reactor.netty.channel.ChannelOperationsHandler,reactor.netty.transport.ServerTransport$Acceptor,reactor.netty.http.server.HttpTrafficHandler
```

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
