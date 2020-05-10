package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import nl.markv.silk.sql_gen.syntax.MetaInfo;
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

	public static void generate(
			@Nonnull SqlWriter sql,
			@Nonnull SilkSchema schema,
			@Nonnull Dialect sqlDialect
	) {
		Syntax gen = sqlDialect.syntaxGenerator.apply(schema.name(), schema.silkVersion);

		DatabaseSpecific dbDbSpecific = Optional.of(schema.db)
				.map(db -> db.databaseSpecific).orElse(null);
		gen.prelude(sql, dbDbSpecific);

		generateCreateTable(sql, schema, gen);

		generateConstraints(sql, schema, gen);

		generateReferences(sql, schema, gen);

		gen.postlude(sql, dbDbSpecific);
	}

	private static void generateCreateTable(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, @Nonnull Syntax gen) {
		for (Table table : schema.tables()) {
			sql.newline();
			gen.startTable(sql, table);
			generateColumns(sql, gen, table);
			gen.primaryKeyInCreateTableSyntax().ifPresent(primaryKeySyn -> {
				primaryKeySyn.begin(sql, table);
				primaryKeySyn.entry(sql, table, table.primaryKey);
				primaryKeySyn.end(sql, table);
			});
			gen.checkInCreateTableSyntax().ifPresent(checkSyn -> {
				checkSyn.begin(sql, table);
				for (CheckConstraint check : table.checkConstraints) {
					checkSyn.entry(sql, table, check);
				}
				checkSyn.end(sql, table);
			});
			gen.uniqueInCreateTableSyntax().ifPresent(uniqueSyn -> {
				uniqueSyn.begin(sql, table);
				for (UniqueConstraint unique : table.uniqueConstraints) {
					uniqueSyn.entry(sql, table, unique);
				}
				uniqueSyn.end(sql, table);
			});
			gen.referenceInCreateTableSyntax().ifPresent(referenceSyn -> {
				referenceSyn.begin(sql, table);
				for (ForeignKey reference : table.references) {
					referenceSyn.entry(sql, table, reference);
				}
				referenceSyn.end(sql, table);
			});
			gen.endTable(sql, table);
			gen.changeColumnForExistingTableSyntax();
		}
	}

	private static void generateColumns(@Nonnull SqlWriter sql, @Nonnull Syntax gen, @Nonnull Table table) {
		Syntax.TableEntrySyntax<Syntax.ColumnInfo> columnGen = gen.columnInCreateTableSyntax();
		columnGen.begin(sql, table);
		for (int colNr = 0; colNr < table.columns.size(); colNr++) {
			Syntax.ColumnInfo info = new Syntax.ColumnInfo();
			info.column = table.columns.get(colNr);
			String autoValue = null;
			info.dataTypeName = gen.dataTypeName(sql, info.column.type);
			if (info.column.autoValue != null) {
				autoValue = gen.autoValueName(sql, info.column.autoValue);
				//autoColumns.add(Triple.of(info.column, dataType, autoValue));
			}
			info.isLast = colNr == table.columns.size() - 1;
			info.primaryKey = MetaInfo.PrimaryKey.NotPart;
			if (table.primaryKeyNames.contains(info.column.name)) {
				info.primaryKey = table.primaryKey.size() == 1 ?
						MetaInfo.PrimaryKey.Single : MetaInfo.PrimaryKey.Composite;
			}
			columnGen.entry(sql, table, info);
		}
		columnGen.end(sql, table);
	}

	private static void generateConstraints(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, Syntax gen) {
		for (Table table : schema.tables()) {
			gen.startTableUniqueConstraints(sql, table.group, table.name, table.databaseSpecific);
			for (UniqueConstraint unique : table.uniqueConstraints) {
				gen.tableUniqueConstraintAfter(sql, table.group, table.name, unique.name, unique.columns, table.databaseSpecific);
			}
			gen.endTableUniqueConstraints(sql, table.group, table.name, table.databaseSpecific);

			gen.startTableCheckConstraints(sql, table.group, table.name, table.databaseSpecific);
			for (CheckConstraint check : table.checkConstraints) {
				gen.tableCheckConstraintAfter(sql, table.group, table.name, check.name, check.condition, table.databaseSpecific);
			}
			gen.endTableCheckConstraints(sql, table.group, table.name, table.databaseSpecific);
		}
	}

	private static void generateReferences(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, Syntax gen) {
		for (Table table : schema.tables()) {
			gen.startTableReferences(sql, table.group, table.name, table.databaseSpecific);
			for (ForeignKey ref : table.references) {
				gen.tableReferenceAfter(sql, table.group, table.name, ref.name, ref.targetTable, ref.columns, table.databaseSpecific);
			}
			gen.endTableReferences(sql, table.group, table.name, table.databaseSpecific);
		}
	}
}
