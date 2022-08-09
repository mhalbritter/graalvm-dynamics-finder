# GraalVM dynamics finder

A tool which can show reflection access with stacktraces in a Java application.
This is useful to answer questions like: "Who did reflection access to type X?".
With the stacktrace provided you know exactly which class and method to blame!

This works with the help of the [native image tracing agent](https://www.graalvm.org/22.2/reference-manual/native-image/metadata/AutomaticMetadataCollection/).

## Usage

Start your application with the native image tracing agent attached:

```
$JAVA_HOME/bin/java -agentlib:native-image-agent=config-output-dir=metadata,experimental-conditional-config-part -jar <your application>
```

and then run the `graalvm-dynamics-finder` on the generated `partial-config-with-origins.json` file:

```
graalvm-dynamics-finder reflection --input metadata/partial-config-with-origins.json --output reflection-report.adoc --target <the classes you are interested in>
```

This will generate a report which shows all reflection access to the classes you provided.
The report looks like [this example report](doc/example-report.adoc).

The example report has been created by running:

```
graalvm-dynamics-finder reflection --input metadata/partial-config-with-origins.json --output example-report.adoc --target reactor.netty.channel.ChannelOperationsHandler,reactor.netty.transport.ServerTransport$Acceptor,reactor.netty.http.server.HttpTrafficHandler
```

## Conditions

The `--target` parameters accepts a list of comma-delimited list of conditions.
A condition determines if a class is either included or excluded from the report.
The list of conditions determine a pipeline, which is executed from beginning to end.
If the pipeline decides that a class is included, it is shown in the report.
By default, all classes start as excluded.
Let's see some examples:

Include all classes:

```
*
```

This will lead to a report showing reflection access to all classes, which could be quite big.

Only include a specific class:

```
reactor.netty.http.server.HttpTrafficHandler
```

Only include some specific classes:

```
reactor.netty.http.server.HttpTrafficHandler,reactor.netty.transport.ServerTransport$Acceptor
```

Include all of `reactor.netty` package, but not `reactor.netty.transport` package:

```
reactor.netty.*,-reactor.netty.transport.*
```

A condition can start with a `-` or a `+`.
`-` excludes the class, `+` includes it.
If the condition doesn't start with `-` or `+`, `+` is implicitly assumed.

You can use `*` in any place as a wildcard.
It matches the empty string (`""`) too.

Explicitly exclude all, then include only `reactor.netty` package, but not `reactor.netty.transport` package and not `reactor.netty.http.server.HttpTrafficHandler`:

```
-*,reactor.netty.*,-reactor.netty.transport.*,-reactor.netty.http.server.HttpTrafficHandler
```

If you are now confused, there is a `debug-conditions` subcommand which lets you play around with the conditions and shows a debug report.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
