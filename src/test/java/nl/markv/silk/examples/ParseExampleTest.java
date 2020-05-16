package nl.markv.silk.examples;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.markv.silk.example.Examples;
import nl.markv.silk.sql_gen.Generator;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.types.SilkSchema;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ParseExampleTest {

	@Nonnull
	static Stream<Pair<Generator.Dialect, SilkSchema>> dialectExampleProvider() {
		return new Examples().schemas().stream()
				.flatMap(schema -> Arrays.stream(Generator.Dialect.values())
						.map(dialect -> Pair.of(dialect, schema)));
	}

	@ParameterizedTest
	@MethodSource({"dialectExampleProvider"})
	void testLoadExample(@Nonnull Pair<Generator.Dialect, SilkSchema> dialectSchemaPair) {
		StringBuilder sql = new StringBuilder();
		Generator.generate(sql, dialectSchemaPair.getRight(), dialectSchemaPair.getLeft(), new Syntax.SyntaxOptions(true, false));
		String txt = sql.toString();
		System.out.println(txt);
		assertFalse(txt.isEmpty());
	}
}
