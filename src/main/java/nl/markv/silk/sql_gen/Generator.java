package nl.markv.silk.sql_gen;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import nl.markv.silk.parse.SilkDb;
import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.pojos.v0_1_0.Table;
import nl.markv.silk.sql_gen.syntax.SqliteSyntax;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.sql_gen.writer.SqlWriter;

/**
 * Orchestrates the SQL generation, by calling syntax methods to match the Silk schema.
 */
public class Generator {

	public enum Dialect {
		Sqlite(SqliteSyntax::new);

		public final BiFunction<String, String, Syntax> syntaxGenerator;

		/**
		 * @param syntaxGenerator Construct a syntax object from schemaName and silkVersion respectively.
		 */
		Dialect(@Nonnull BiFunction<String, String, Syntax> syntaxGenerator) {
			this.syntaxGenerator = syntaxGenerator;
		}
	}

	public static void generate(
			@Nonnull SqlWriter sql,
			@Nonnull SilkDb schema,
			@Nonnull Dialect sqlDialect
	) {
		Syntax gen = sqlDialect.syntaxGenerator.apply(schema.name(), schema.version());

		gen.prelude(sql);

		for (Table table : schema.tables()) {
			gen.startTable(sql, table.group, table.name, table.description);
			for (LongColumn column : table.columns) {
				gen.columnInCreateTable(
						sql,
						column.name,
						gen.dataTypeName(sql, column.type),
						column.nullable,
						gen.autoValueName(sql, column.autoValue),
						column.defaultValue
				);
			}
			gen.primaryKeyInCreateTable(sql, table.primaryKey);
			gen.endTable(sql, table.group, table.name);
		}

		gen.postlude(sql);
	}
}
