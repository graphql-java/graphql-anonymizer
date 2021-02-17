import graphql.Directives;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.DirectiveInfo;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.util.Anonymizer;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "graphql-anonymizer", mixinStandardHelpOptions = true, version = "graphql-anonymizer 1.0",
        description = "Anonymize GraphQL schemas and queries")
public class Main implements Callable<String> {


    @Option(names = {"-s", "--schema"}, description = "The GraphQL schema file", paramLabel = "schema-file")
    private File schemaFile;

    @Option(names = {"-v", "--verbose"}, description = "print out more details", defaultValue = "false")
    private boolean verbose;

    @Override
    public String call() throws Exception {
        String sdl;
        if (schemaFile != null) {
            sdl = Files.readString(schemaFile.toPath());
        } else {
            List<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
            sdl = String.join("\n", lines);
        }
        if (verbose) {
            System.out.printf("Loaded schema: %s%n", sdl);
        }
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(sdl);
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, MockedWiring.MOCKED_WIRING);
        GraphQLSchema anonSchema = Anonymizer.anonymizeSchema(graphQLSchema);
        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions();
        options = options.includeDirectives(graphQLDirective -> !DirectiveInfo.isGraphqlSpecifiedDirective(graphQLDirective));
        String printedSchema = new SchemaPrinter(options).print(anonSchema);
        System.out.println(printedSchema);
        return printedSchema;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
