import graphql.schema.GraphQLSchema;
import graphql.schema.idl.DirectiveInfo;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.util.Anonymizer;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "graphql-anonymizer", mixinStandardHelpOptions = true, version = "graphql-anonymizer 1.0",
        description = "Anonymize GraphQL schemas and queries")
public class Main implements Callable<String> {


    @Option(names = {"-s", "--schema"}, description = "The GraphQL schema file", paramLabel = "schema-file")
    private File schemaFile;

    @Option(names = {"-q", "--query"}, description = "A GraphQL query file", paramLabel = "query-file")
    private File queryFile;

    @Option(names = {"-v", "--verbose"}, description = "print out more details", defaultValue = "false")
    private boolean verbose;

    @Override
    public String call() throws Exception {
        String sdl;
        if (schemaFile != null) {
            logVerbose("Loading schema from file %s%n", schemaFile);
            sdl = Files.readString(schemaFile.toPath());
        } else {
            logVerbose("Loading schema from stdin%n");
            List<String> lines = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
            sdl = String.join("\n", lines);
        }
        logVerbose("Loaded schema: %s%n", sdl);
        String query = null;
        if (queryFile != null) {
            logVerbose("Loading query from file %s%n", queryFile);
            query = Files.readString(queryFile.toPath());
            logVerbose("Loaded query %s%n", query);
        }
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(sdl);
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, MockedWiring.MOCKED_WIRING);

        Anonymizer.AnonymizeResult anonymizeResult = Anonymizer.anonymizeSchemaAndQueries(graphQLSchema, query != null ? Collections.singletonList(query) : Collections.emptyList());


        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions();
        options = options.includeDirectives(graphQLDirective -> !DirectiveInfo.isGraphqlSpecifiedDirective(graphQLDirective));
        String printedSchema = new SchemaPrinter(options).print(anonymizeResult.getSchema());
        System.out.println(printedSchema);
        System.out.println();
        if (anonymizeResult.getQueries().size() > 0) {
            System.out.println(anonymizeResult.getQueries().get(0));
        }
        return printedSchema;
    }

    private void logVerbose(String string, Object... args) {
        if (verbose) {
            System.out.printf(string, args);
        }
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
