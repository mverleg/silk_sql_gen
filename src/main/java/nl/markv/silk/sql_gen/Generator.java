package nl.markv.silk.sql_gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
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

import static nl.markv.silk.sql_gen.sqlparts.ListEntry.entriesText;
import static nl.markv.silk.sql_gen.sqlparts.Statement.comment;
import static nl.markv.silk.sql_gen.sqlparts.Statement.statement;
import static nl.markv.silk.sql_gen.sqlparts.StringEmptyLine.emptyLine;

/**
 * Orchestrates the SQL generation, by calling syntax methods to match the Silk schema.
 */
@SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
public class Generator {
//
	public enum Dialect {
		Sqlite(SqliteSyntax::new),
//		Postgres(PostgresSyntax::new),
//		H2(H2Syntax::new),
//		DB2(DB2Syntax::new)
		;

		public final Syntax.SyntaxConstructor syntaxGenerator;

		/**
		 * @param syntaxGenerator Construct a syntax object from schemaName and silkVersion respectively.
		 */
		Dialect(@Nonnull Syntax.SyntaxConstructor syntaxGenerator) {
			this.syntaxGenerator = syntaxGenerator;
		}
	}

	public static void generate(
			@Nonnull StringBuilder sql,
			@Nonnull SilkSchema schema,
			@Nonnull Dialect sqlDialect,
			@Nonnull Syntax.SyntaxOptions options
	) {
		Syntax gen = sqlDialect.syntaxGenerator.create(schema.name(), schema.silkVersion, options);

		StatementCollector statements = StatementCollector.empty();

		DatabaseSpecific dbDbSpecific = Optional.of(schema.db)
				.map(db -> db.databaseSpecific).orElse(null);
		statements.add(emptyLine());
		statements.add(gen.prelude(dbDbSpecific));

		schema.tables().forEach(table -> {
			List<Syntax.ColumnInfo> columnsInfo = generateCreateTable(statements, schema, gen, table);
			statements.add(gen.changeColumnForExistingTableSyntax()
					.map(columnSyn -> generateChangeColumn(columnSyn, table, columnsInfo))
					.orElse(Collections.emptyList()));
		});
		schema.tables().forEach(table -> {
			statements.addAfterLine(gen.addPrimaryKeyToExistingTableSyntax()
					.map(primaryKeySyn -> generatePrimaryKey(primaryKeySyn, table))
					.orElse(Collections.emptyList()));
			statements.addAfterLine(gen.addCheckToExistingTableSyntax()
					.map(checkSyn -> generateChecks(checkSyn, table))
					.orElse(Collections.emptyList()));
			statements.addAfterLine(gen.addUniqueToExistingTableSyntax()
					.map(uniqueSyn -> generateUnique(uniqueSyn, table))
					.orElse(Collections.emptyList()));
			statements.addAfterLine(gen.addReferenceToExistingTableSyntax()
					.map(referenceSyn -> generateReference(referenceSyn, table))
					.orElse(Collections.emptyList()));
		});

		statements.add(emptyLine());
		statements.add(gen.postlude(dbDbSpecific));
		statements.add(emptyLine());

		statements.statementsText(sql);
	}

	@Nonnull
	private static List<Syntax.ColumnInfo> generateCreateTable(@Nonnull StatementCollector statements, @Nonnull SilkSchema schema, @Nonnull Syntax gen, @Nonnull Table table) {
		statements.add(emptyLine());

		statements.add(generateTableDescriptionComment(table));
		Pair<List<ListEntry>, List<Syntax.ColumnInfo>> res = generateColumns(gen, table);
		List<ListEntry> entries = res.getLeft();
		entries.addAll(gen.primaryKeyInCreateTableSyntax()
				.map(primaryKeySyn -> generatePrimaryKey(primaryKeySyn, table))
				.orElse(Collections.emptyList()));
		entries.addAll(gen.checkInCreateTableSyntax()
				.map(checkSyn -> generateChecks(checkSyn, table))
				.orElse(Collections.emptyList()));
		entries.addAll(gen.uniqueInCreateTableSyntax()
				.map(uniqueSyn -> generateUnique(uniqueSyn, table))
				.orElse(Collections.emptyList()));
		entries.addAll(gen.referenceInCreateTableSyntax()
				.map(referenceSyn -> generateReference(referenceSyn, table))
				.orElse(Collections.emptyList()));
		statements.add(statement(
				gen.startTable(table) + "\n",
				entriesText(entries),
				gen.endTable(table)
		));
		return res.getRight();
	}

	@Nonnull
	private static Optional<Statement> generateTableDescriptionComment(@Nonnull Table table) {
		if (StringUtils.isNotEmpty(table.description)) {
			//TODO @mark: handle multiline descriptions
			return Optional.of(comment(table.description));
		}
		return Optional.empty();
	}

	@Nonnull
	private static List<Statement> generateChangeColumn(@Nonnull Syntax.TableEntrySyntax<Syntax.ColumnInfo, Statement> columnSyn, @Nonnull Table table, @Nonnull List<Syntax.ColumnInfo> columnInfos) {
		ArrayList<Statement> list = new ArrayList<>();
		list.addAll(columnSyn.begin(table));
		for (Syntax.ColumnInfo info : columnInfos) {
			list.addAll(columnSyn.entry(table, info));
		}
		list.addAll(columnSyn.end(table));
		return list;
	}

	@Nonnull
	private static <U> List<U> generateReference(@Nonnull Syntax.TableEntrySyntax<ForeignKey, U> referenceSyn, @Nonnull Table table) {
		ArrayList<U> list = new ArrayList<>();
		list.addAll(referenceSyn.begin(table));
		for (ForeignKey reference : table.references) {
			list.addAll(referenceSyn.entry(table, reference));
		}
		list.addAll(referenceSyn.end(table));
		return list;
	}

	@Nonnull
	private static <U> List<U> generateUnique(@Nonnull Syntax.TableEntrySyntax<UniqueConstraint, U> uniqueSyn, @Nonnull Table table) {
		ArrayList<U> list = new ArrayList<>();
		list.addAll(uniqueSyn.begin(table));
		for (UniqueConstraint unique : table.uniqueConstraints) {
			list.addAll(uniqueSyn.entry(table, unique));
		}
		list.addAll(uniqueSyn.end(table));
		return list;
	}

	@Nonnull
	private static <U> List<U> generateChecks(@Nonnull Syntax.TableEntrySyntax<CheckConstraint, U> checkSyn, @Nonnull Table table) {
		ArrayList<U> list = new ArrayList<>();
		list.addAll(checkSyn.begin(table));
		for (CheckConstraint check : table.checkConstraints) {
			list.addAll(checkSyn.entry(table, check));
		}
		list.addAll(checkSyn.end(table));
		return list;
	}

	@Nonnull
	private static <U> List<U> generatePrimaryKey(@Nonnull Syntax.TableEntrySyntax<List<Column>, U> primaryKeySyn, @Nonnull Table table) {
		ArrayList<U> list = new ArrayList<>();
		list.addAll(primaryKeySyn.begin(table));
		list.addAll(primaryKeySyn.entry(table, table.primaryKey));
		list.addAll(primaryKeySyn.end(table));
		return list;
	}

	@Nonnull
	private static Pair<List<ListEntry>, List<Syntax.ColumnInfo>> generateColumns(@Nonnull Syntax gen, @Nonnull Table table) {
		List<ListEntry> entries = new ArrayList<>();
		Syntax.TableEntrySyntax<Syntax.ColumnInfo, ListEntry> columnGen = gen.columnInCreateTableSyntax();
		entries.addAll(columnGen.begin(table));
		List<Syntax.ColumnInfo> columnInfos = new ArrayList<>();
		for (int colNr = 0; colNr < table.columns.size(); colNr++) {
			Column column = table.columns.get(colNr);
			MetaInfo.PrimaryKey primaryKey = MetaInfo.PrimaryKey.NotPart;
			if (table.primaryKeyNames.contains(column.name)) {
				primaryKey = table.primaryKey.size() == 1 ?
						MetaInfo.PrimaryKey.Single : MetaInfo.PrimaryKey.Composite;
			}
			Syntax.ColumnInfo info = new Syntax.ColumnInfo(
					column,
					gen.dataTypeName(column.type),
					column.autoValue == null ? null : gen.autoValueName(column.autoValue),
					primaryKey
			);
			columnInfos.add(info);
			entries.addAll(columnGen.entry(table, info));
		}
		entries.addAll(columnGen.end(table));
		return Pair.of(entries, columnInfos);
	}
}
