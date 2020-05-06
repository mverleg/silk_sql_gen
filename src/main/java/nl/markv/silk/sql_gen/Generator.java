package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import nl.markv.silk.parse.SilkDb;
import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.pojos.v0_1_0.Table;
import nl.markv.silk.sql_gen.syntax.SqliteSyntax;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.sql_gen.writer.SqlWriter;

import static nl.markv.silk.parse.SilkDb.parseDataType;

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
			sql.newline();
			gen.startTable(sql, table.group, table.name, table.description);
			List<Triple<LongColumn, String, String>> autoColumns = new ArrayList<>();
			for (LongColumn column : table.columns) {
				String autoValue = null;
				String dataType = gen.dataTypeName(sql, parseDataType(column.type));
				if (column.autoValue != null) {
					autoValue = gen.autoValueName(sql, column.autoValue);
					autoColumns.add(Triple.of(column, dataType, autoValue));
				}
				gen.columnInCreateTable(
						sql,
						column.name,
						dataType,
						column.nullable,
						autoValue,
						column.defaultValue
				);
			}
			for (Triple<LongColumn, String, String> autoCol : autoColumns) {
				gen.autoValueAfterCreation(sql, autoCol.getLeft().name, autoCol.getMiddle(), autoCol.getRight());
			}
			gen.primaryKeyInCreateTable(sql, table.primaryKey);
			gen.endTable(sql, table.group, table.name);
		}

		gen.postlude(sql);
	}
}
