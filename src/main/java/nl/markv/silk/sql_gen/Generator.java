package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import nl.markv.silk.sql_gen.sqlparts.Statement;
import nl.markv.silk.sql_gen.sqlparts.StatementCollector;
import nl.markv.silk.sql_gen.syntax.MetaInfo;
import nl.markv.silk.sql_gen.syntax.SqliteSyntax;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.SilkSchema;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

import static nl.markv.silk.sql_gen.sqlparts.Statement.comment;
import static nl.markv.silk.sql_gen.sqlparts.Statement.statement;
import static nl.markv.silk.sql_gen.sqlparts.StringEmptyLine.emptyLine;

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
			@Nonnull StringBuilder sql,
			@Nonnull SilkSchema schema,
			@Nonnull Dialect sqlDialect
	) {
		Syntax gen = sqlDialect.syntaxGenerator.apply(schema.name(), schema.silkVersion);

		StatementCollector statements = StatementCollector.empty();

		DatabaseSpecific dbDbSpecific = Optional.of(schema.db)
				.map(db -> db.databaseSpecific).orElse(null);
		statements.add(emptyLine());
		statements.add(gen.prelude(dbDbSpecific));

		schema.tables().forEach(table -> generateCreateTable(statements, schema, gen, table));
		schema.tables().forEach(table -> {
			gen.addPrimaryKeyToExistingTableSyntax().ifPresent(primaryKeySyn ->
					generatePrimaryKey(primaryKeySyn, table));
			gen.addCheckToExistingTableSyntax().ifPresent(checkSyn ->
					generateChecks(checkSyn, table));
			gen.addUniqueToExistingTableSyntax().ifPresent(uniqueSyn ->
					generateUnique(uniqueSyn, table));
			gen.addReferenceToExistingTableSyntax().ifPresent(referenceSyn ->
					generateReference(referenceSyn, table));
		});

		statements.add(gen.postlude(dbDbSpecific));
		statements.add(emptyLine());

		statements.statementsText(sql);
	}

	private static void generateCreateTable(@Nonnull StatementCollector statements, @Nonnull SilkSchema schema, @Nonnull Syntax gen, @Nonnull Table table) {
		statements.add(emptyLine());

		statements.add(generateTableDescriptionComment(table));
		StringBuilder createTable = new StringBuilder();
		createTable.append(gen.startTable(table));
		List<Syntax.ColumnInfo> columnInfos = generateColumns(gen, table);
		gen.primaryKeyInCreateTableSyntax().ifPresent(primaryKeySyn ->
				generatePrimaryKey(primaryKeySyn, table));
		gen.checkInCreateTableSyntax().ifPresent(checkSyn ->
				generateChecks(checkSyn, table));
		gen.uniqueInCreateTableSyntax().ifPresent(uniqueSyn ->
				generateUnique(uniqueSyn, table));
		gen.referenceInCreateTableSyntax().ifPresent(referenceSyn ->
				generateReference(referenceSyn, table));
		createTable.append(gen.endTable(table));
		statements.add(statement(createTable));
		statements.add(gen.changeColumnForExistingTableSyntax()
				.map(columnSyn -> generateChangeColumn(columnSyn, table, columnInfos)));
	}

	@Nonnull
	private static Optional<Statement> generateTableDescriptionComment(@Nonnull Table table) {
		if (StringUtils.isNotEmpty(table.description)) {
			//TODO @mark: handle multiline descriptions
			comment(table.description);
		}
	}

	@Nonnull
	private static List<Statement> generateChangeColumn(@Nonnull int columnSyn, Table table, List<Syntax.ColumnInfo> columnInfos) {
		columnSyn.begin(table);
		for (Syntax.ColumnInfo info : columnInfos) {
			columnSyn.entry(table, info);
		}
		columnSyn.end(table);
	}

	private static List<Statement> generateReference(@Nonnull Syntax.TableEntrySyntax<ForeignKey, Statement> referenceSyn, Table table) {
		ArrayList<Statement> statements = new ArrayList<>();
		statements.addAll(referenceSyn.begin(table));
		for (ForeignKey reference : table.references) {
			statements.addAll(referenceSyn.entry(table, reference));
		}
		statements.addAll(referenceSyn.end(table));
		return statements;
	}

	private static void generateUnique(@Nonnull Syntax.TableEntrySyntax<UniqueConstraint, Statement> uniqueSyn, Table table) {
		uniqueSyn.begin(sql, table);
		for (UniqueConstraint unique : table.uniqueConstraints) {
			uniqueSyn.entry(sql, table, unique);
		}
		uniqueSyn.end(sql, table);
	}

	private static void generateChecks(@Nonnull Syntax.TableEntrySyntax<CheckConstraint, Statement> checkSyn, Table table) {
		checkSyn.begin(sql, table);
		for (CheckConstraint check : table.checkConstraints) {
			checkSyn.entry(sql, table, check);
		}
		checkSyn.end(sql, table);
	}

	private static void generatePrimaryKey(@Nonnull Syntax.TableEntrySyntax<List<Column>, Statement> primaryKeySyn, Table table) {
		primaryKeySyn.begin(sql, table);
		primaryKeySyn.entry(sql, table, table.primaryKey);
		primaryKeySyn.end(sql, table);
	}

	@Nonnull
	private static List<Syntax.ColumnInfo> generateColumns(@Nonnull Syntax gen, @Nonnull Table table) {
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
