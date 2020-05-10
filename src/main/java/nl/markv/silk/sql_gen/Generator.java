package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

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

		schema.tables().forEach(table -> generateCreateTable(sql, schema, gen, table));
		schema.tables().forEach(table -> {
			gen.addPrimaryKeyToExistingTableSyntax().ifPresent(primaryKeySyn ->
					generatePrimaryKey(sql, primaryKeySyn, table));
			gen.addCheckToExistingTableSyntax().ifPresent(checkSyn ->
					generateChecks(sql, checkSyn, table));
			gen.addUniqueToExistingTableSyntax().ifPresent(uniqueSyn ->
					generateUnique(sql, uniqueSyn, table));
			gen.addReferenceToExistingTableSyntax().ifPresent(referenceSyn ->
					generateReference(sql, referenceSyn, table));
		});

		gen.postlude(sql, dbDbSpecific);
	}

	private static void generateCreateTable(@Nonnull SqlWriter sql, @Nonnull SilkSchema schema, @Nonnull Syntax gen, @Nonnull Table table) {
		sql.newline();
		gen.startTable(sql, table);
		List<Syntax.ColumnInfo> columnInfos = generateColumns(sql, gen, table);
		gen.primaryKeyInCreateTableSyntax().ifPresent(primaryKeySyn ->
				generatePrimaryKey(sql, primaryKeySyn, table));
		gen.checkInCreateTableSyntax().ifPresent(checkSyn ->
				generateChecks(sql, checkSyn, table));
		gen.uniqueInCreateTableSyntax().ifPresent(uniqueSyn ->
				generateUnique(sql, uniqueSyn, table));
		gen.referenceInCreateTableSyntax().ifPresent(referenceSyn ->
				generateReference(sql, referenceSyn, table));
		gen.endTable(sql, table);
		gen.changeColumnForExistingTableSyntax().ifPresent(columnSyn ->
				generateChangeColumn(sql, columnSyn, table, columnInfos));
	}

	private static void generateChangeColumn(@Nonnull SqlWriter sql, @Nonnull Syntax.TableEntrySyntax<Syntax.ColumnInfo> columnSyn, Table table, List<Syntax.ColumnInfo> columnInfos) {
		columnSyn.begin(sql, table);
		for (Syntax.ColumnInfo info : columnInfos) {
			columnSyn.entry(sql, table, info);
		}
		columnSyn.end(sql, table);
	}

	private static void generateReference(@Nonnull SqlWriter sql, @Nonnull Syntax.TableEntrySyntax<ForeignKey> referenceSyn, Table table) {
		referenceSyn.begin(sql, table);
		for (ForeignKey reference : table.references) {
			referenceSyn.entry(sql, table, reference);
		}
		referenceSyn.end(sql, table);
	}

	private static void generateUnique(@Nonnull SqlWriter sql, @Nonnull Syntax.TableEntrySyntax<UniqueConstraint> uniqueSyn, Table table) {
		uniqueSyn.begin(sql, table);
		for (UniqueConstraint unique : table.uniqueConstraints) {
			uniqueSyn.entry(sql, table, unique);
		}
		uniqueSyn.end(sql, table);
	}

	private static void generateChecks(@Nonnull SqlWriter sql, @Nonnull Syntax.TableEntrySyntax<CheckConstraint> checkSyn, Table table) {
		checkSyn.begin(sql, table);
		for (CheckConstraint check : table.checkConstraints) {
			checkSyn.entry(sql, table, check);
		}
		checkSyn.end(sql, table);
	}

	private static void generatePrimaryKey(@Nonnull SqlWriter sql, @Nonnull Syntax.TableEntrySyntax<List<Column>> primaryKeySyn, Table table) {
		primaryKeySyn.begin(sql, table);
		primaryKeySyn.entry(sql, table, table.primaryKey);
		primaryKeySyn.end(sql, table);
	}

	@Nonnull
	private static List<Syntax.ColumnInfo> generateColumns(@Nonnull SqlWriter sql, @Nonnull Syntax gen, @Nonnull Table table) {
		Syntax.TableEntrySyntax<Syntax.ColumnInfo> columnGen = gen.columnInCreateTableSyntax();
		columnGen.begin(sql, table);
		List<Syntax.ColumnInfo> columnInfos = new ArrayList<>();
		for (int colNr = 0; colNr < table.columns.size(); colNr++) {
			Syntax.ColumnInfo info = new Syntax.ColumnInfo();
			info.column = table.columns.get(colNr);
			String autoValue = null;
			info.dataTypeName = gen.dataTypeName(sql, info.column.type);
			if (info.column.autoValue != null) {
				info.autoValueName = gen.autoValueName(sql, info.column.autoValue);
			}
			info.isLast = colNr == table.columns.size() - 1;
			info.primaryKey = MetaInfo.PrimaryKey.NotPart;
			if (table.primaryKeyNames.contains(info.column.name)) {
				info.primaryKey = table.primaryKey.size() == 1 ?
						MetaInfo.PrimaryKey.Single : MetaInfo.PrimaryKey.Composite;
			}
			columnInfos.add(info);
			columnGen.entry(sql, table, info);
		}
		columnGen.end(sql, table);
		return columnInfos;
	}
}
