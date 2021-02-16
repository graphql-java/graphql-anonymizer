import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.util.Anonymizer;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "graphql-anonymizer", mixinStandardHelpOptions = true, version = "graphql-anonymizer 1.0",
        description = "Aononymize GraphQL schemas and queries.")
public class Main implements Callable<String> {


    @Parameters(index = "0", description = "The GraphQL schema file")
    private File file;

//    @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
//    private String algorithm = "MD5";

    @Override
    public String call() throws Exception {
        String schema = Files.readString(file.toPath());
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schema);
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, MockedWiring.MOCKED_WIRING);
        GraphQLSchema anonSchema = Anonymizer.anonymizeSchema(graphQLSchema);
        String printedSchema = new SchemaPrinter().print(anonSchema);
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
