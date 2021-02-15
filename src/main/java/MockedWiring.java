import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.TypeResolver;
import graphql.schema.idl.FieldWiringEnvironment;
import graphql.schema.idl.InterfaceWiringEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.ScalarInfo;
import graphql.schema.idl.ScalarWiringEnvironment;
import graphql.schema.idl.UnionWiringEnvironment;
import graphql.schema.idl.WiringFactory;

public class MockedWiring implements WiringFactory {


    private static WiringFactory mockedWiringFactory = new WiringFactory() {
        @Override
        public boolean providesTypeResolver(InterfaceWiringEnvironment environment) {
            return true;
        }

        @Override
        public TypeResolver getTypeResolver(InterfaceWiringEnvironment environment) {
            return env -> {
                throw new UnsupportedOperationException("Not implemented");
            };
        }

        @Override
        public boolean providesTypeResolver(UnionWiringEnvironment environment) {
            return true;
        }

        @Override
        public TypeResolver getTypeResolver(UnionWiringEnvironment environment) {
            return env -> {
                throw new UnsupportedOperationException("Not implemented");
            };
        }

        @Override
        public boolean providesDataFetcher(FieldWiringEnvironment environment) {
            return true;
        }

        @Override
        public DataFetcher getDataFetcher(FieldWiringEnvironment environment) {
            return new PropertyDataFetcher(environment.getFieldDefinition().getName());
        }

        @Override
        public boolean providesScalar(ScalarWiringEnvironment environment) {
            if (ScalarInfo.isGraphqlSpecifiedScalar(environment.getScalarTypeDefinition().getName())) {
                return false;
            }
            return true;
        }

        public GraphQLScalarType getScalar(ScalarWiringEnvironment environment) {
            return GraphQLScalarType.newScalar().name(environment.getScalarTypeDefinition().getName()).coercing(new Coercing() {
                @Override
                public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    throw new UnsupportedOperationException("Not implemented");
                }

                @Override
                public Object parseValue(Object input) throws CoercingParseValueException {
                    throw new UnsupportedOperationException("Not implemented");
                }

                @Override
                public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                    throw new UnsupportedOperationException("Not implemented");
                }
            }).build();
        }
    };

    public static final RuntimeWiring MOCKED_WIRING = RuntimeWiring
            .newRuntimeWiring()
            .wiringFactory(mockedWiringFactory)
            .build();
}
