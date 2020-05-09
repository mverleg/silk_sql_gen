package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import nl.markv.silk.sql_gen.syntax.DB2Syntax;
import nl.markv.silk.sql_gen.syntax.H2Syntax;
import nl.markv.silk.sql_gen.syntax.MetaInfo;
import nl.markv.silk.sql_gen.syntax.PostgresSyntax;
import nl.markv.silk.sql_gen.syntax.SqliteSyntax;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.SilkSchema;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

/**
 * Orchestrates the SQL generation, by calling syntax methods to match the Silk schema.
 */
public class Generator {
//
	public enum Dialect {
		Sqlite(SqliteSyntax::new),
//		Postgres(PostgresSyntax::new),
//		H2(H2Syntax::new),
//		DB2(DB2Syntax::new)
		;

		public final BiFunction<String, String, Syntax> syntaxGenerator;

		/**
		 * @param syntaxGenerator Construct a syntax object from schemaName and silkVersion respectively.
		 */
		Dialect(@Nonnull BiFunction<String, String, Syntax> syntaxGenerator) {
			this.syntaxGenerator = syntaxGenerator;
		}
	}
//
//	public static void generate(
//			@Nonnull SqlWriter sql,
//			@Nonnull SilkSchema schema,
//			@Nonnull Dialect sqlDialect
//	) {
//		Syntax gen = sqlDialect.syntaxGenerator.apply(schema.name(), schema.silkVersion);
//
//		DatabaseSpecific dbDbSpecific = Optional.of(schema.db)
//				.map(db -> db.databaseSpecific).orElse(null);
//		gen.prelude(sql, dbDbSpecific);
//
//		generateCreateTable(sql, schema, gen);
//
//		generateConstraints(sql, schema, gen);
//
//		generateReferences(sql, schema, gen);
//
//		gen.postlude(sql, dbDbSpecific);
//	}
//
//	private static void generateCreateTable(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, Syntax gen) {
//		for (Table table : schema.tables()) {
//			sql.newline();
//			gen.startTable(sql, table.group, table.name, table.description, table.databaseSpecific);
//			List<Triple<Column, String, String>> autoColumns = new ArrayList<>();
//			for (int colNr = 0; colNr < table.columns.size(); colNr++) {
//				Column column = table.columns.get(colNr);
//				String autoValue = null;
//				String dataType = gen.dataTypeName(sql, column.type);
//				if (column.autoValue != null) {
//					autoValue = gen.autoValueName(sql, column.autoValue);
//					autoColumns.add(Triple.of(column, dataType, autoValue));
//				}
//				boolean isLast = colNr == table.columns.size() - 1;
//				MetaInfo.PrimaryKey primaryKey = MetaInfo.PrimaryKey.NotPart;
//				if (table.primaryKey.contains(column)) {
//					primaryKey = table.primaryKey.size() == 1 ?
//							MetaInfo.PrimaryKey.Single : MetaInfo.PrimaryKey.Composite;
//				}
//				gen.columnInCreateTable(
//						sql,
//						column.name,
//						dataType,
//						column.nullable,
//						primaryKey,
//						autoValue,
//						column.defaultValue,
//						isLast,
//						table.databaseSpecific
//				);
//			}
//			for (UniqueConstraint unique : table.uniqueConstraints) {
//				gen.tableUniqueConstraintInline(sql, table.group, table.name, unique.name, unique.columns, table.databaseSpecific);
//			}
//			for (CheckConstraint check : table.checkConstraints) {
//				gen.tableCheckConstraintInline(sql, table.group, table.name, check.name, check.condition, table.databaseSpecific);
//			}
//			for (Triple<Column, String, String> autoCol : autoColumns) {
//				gen.autoValueAfterCreation(sql, autoCol.getLeft().name, autoCol.getMiddle(), autoCol.getRight(), table.databaseSpecific);
//			}
//
//			gen.primaryKeyInCreateTable(sql, table.primaryKey, table.databaseSpecific);
//			gen.endTable(sql, table.group, table.name, table.databaseSpecific);
//		}
//	}
//
//	private static void generateConstraints(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, Syntax gen) {
//		for (Table table : schema.tables()) {
//			gen.startTableUniqueConstraints(sql, table.group, table.name, table.databaseSpecific);
//			for (UniqueConstraint unique : table.uniqueConstraints) {
//				gen.tableUniqueConstraintAfter(sql, table.group, table.name, unique.name, unique.columns, table.databaseSpecific);
//			}
//			gen.endTableUniqueConstraints(sql, table.group, table.name, table.databaseSpecific);
//
//			gen.startTableCheckConstraints(sql, table.group, table.name, table.databaseSpecific);
//			for (CheckConstraint check : table.checkConstraints) {
//				gen.tableCheckConstraintAfter(sql, table.group, table.name, check.name, check.condition, table.databaseSpecific);
//			}
//			gen.endTableCheckConstraints(sql, table.group, table.name, table.databaseSpecific);
//		}
//	}
//
//	private static void generateReferences(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, Syntax gen) {
//		for (Table table : schema.tables()) {
//			gen.startTableReferences(sql, table.group, table.name, table.databaseSpecific);
//			for (ForeignKey ref : table.references) {
//				gen.tableReferenceAfter(sql, table.group, table.name, ref.name, ref.targetTable, ref.columns, table.databaseSpecific);
//			}
//			gen.endTableReferences(sql, table.group, table.name, table.databaseSpecific);
//		}
//	}
}
