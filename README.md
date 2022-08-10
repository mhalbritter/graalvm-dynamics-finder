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

## Use cases

### Show reflection access to given classes

Show all reflection calls to `reactor.netty.channel.ChannelOperationsHandler`:

```
graalvm-dynamics-finder reflection --input ... --output ... --target reactor.netty.channel.ChannelOperationsHandler
```

### Show reflection calls for a given caller class (directly or indirectly)

Show all reflection calls where `reactor.netty.channel.ChannelOperationsHandler` is involved:

```
graalvm-dynamics-finder reflection --input ... --output ... --caller reactor.netty.channel.ChannelOperationsHandler
```

Show all reflection calls from `io.lettuce` package, but exclude those where `io.netty` package is involved:

```
graalvm-dynamics-finder reflection --input ... --output ... --caller io.lettuce.* --exclude-caller io.netty.*
```

### Show reflection calls for a given caller class (direct only)

Show all reflection calls which `org.springframework.util.ClassUtils` did:

```
graalvm-dynamics-finder reflection --input ... --output ... --direct-caller org.springframework.util.ClassUtils
```

Show all reflection which the package `org.springframework` did, but ignore `org.springframework.util.ClassUtils`:

```
graalvm-dynamics-finder reflection --input ... --output ... --direct-caller org.springframework.*,-org.springframework.util.ClassUtils
```

## Conditions

Some parameters are conditions (for example `--target`, `--caller` or `--direct-caller`).
A condition determines if a class is either included or excluded from the report.
The list of conditions determine a pipeline, which is executed from beginning to end.
If the pipeline decides that a class is included, it is shown in the report.
By default, all classes start as excluded.
Let's see some examples:

Include all classes:

```
*
```

This will include all classes, which could generate a large record.

Only include a specific class:

```
reactor.netty.http.server.HttpTrafficHandler
```

Only include some specific classes:

```
reactor.netty.http.server.HttpTrafficHandler,reactor.netty.transport.ServerTransport$Acceptor
```

Include all of `reactor.netty` package:

```
reactor.netty.*
```

Include all of the `reactor.netty` package, but not the`reactor.netty.transport` package:

```
reactor.netty.*,-reactor.netty.transport.*
```

A condition can start with a `-` or a `+`.
`-` excludes the class, `+` includes it.
If the condition doesn't start with `-` or `+`, `+` is implicitly assumed.

You can use `*` in any place as a wildcard.
It matches the empty string (`""`) too.

Explicitly exclude all classes (this is not required, as a pipeline starts with excluding all classes anyway), then include only the `reactor.netty` package, but not the `reactor.netty.transport` package and not `reactor.netty.http.server.HttpTrafficHandler`:

```
-*,reactor.netty.*,-reactor.netty.transport.*,-reactor.netty.http.server.HttpTrafficHandler
```

If you are now confused, there is a `debug-conditions` subcommand which lets you play around with the conditions and shows a debug report.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
