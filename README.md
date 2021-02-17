# Anonymize GraphQL schemas and queries

GraphQL anonymizer is CLI tool to anonymize schemas and queries by replacing all names with generic values
like `field22`.

For example this schema and query:

```graphql
type Query{
   mySecret(arg: String): MySecret 
}
type MySecret {
    id: ID
    dontExposeThat: String
}

query MySecretOp{mySecret(arg: "myValue") { id dontExposeThat } }
```

will be anonymized into:

```graphql
schema {
  query: Object1
}

type Object1 {
  field1(argument1: String): Object2
}

type Object2 {
  field2: ID
  field3: String
}

query {field1(argument1:"stringValue1") {field2 field3}}

```

## Installation and usage

The easiest way to install it is via `npm`:

```sh
npm i -g graphql-anonymizer
```

To anonymize a schema and query from a file:

```sh
graphql-anonymizer -s schema-file -q query-file 
```

The `-q` option is optional, you can also just anonymize a schema:

```sh
graphql-anonymizer -s schema-file 
```

Alternatively the schema file can be read from stdin:

```sh
graphql-anonymizer < schema-file
```

The full list of options is available via `--help`:

```sh
graphql-anonymizer --help
```

## Bugs of feedback

Please open an Issue to raise a bug or other feedback.

## Implementation details

GraphQL Anonymizer is a thin wrapper around a
[class inside GraphQL Java](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/util/Anonymizer.java)
It is compiled via [GraalVM](graalvm.org) to a native binary and distributed via NPM.

The cross-platform compiling, releasing and testing is done fully automatically via GitHub Actions. Have a look
at [release.yml](./.github/workflows/release.yml) and [npm](/npm) if you want to know all the details.
