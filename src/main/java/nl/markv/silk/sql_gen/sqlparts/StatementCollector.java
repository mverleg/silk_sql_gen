package nl.markv.silk.sql_gen.sqlparts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import static nl.markv.silk.sql_gen.sqlparts.StringEmptyLine.emptyLine;

public class StatementCollector {

	@Nonnull
	private final List<Statement> statements = new ArrayList<>();

	private StatementCollector() {}

	public static StatementCollector empty() {
		return new StatementCollector();
	}

	public void add(@Nonnull Statement statement) {
		statements.add(statement);
	}

	public void add(@Nonnull Collection<Statement> newStatements) {
		statements.addAll(newStatements);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void add(@Nonnull Optional<Statement> statement) {
		statement.ifPresent(s -> add(s));
	}

	public void statementsText(@Nonnull StringBuilder sql) {
		for (Statement statement : statements) {
			statement.statementText(sql);
		}
	}

	/**
	 * Add a single empty line, meaning that if this method is called twice without other
	 * statements being added, then second and subsequent times are ignored.
	 */
	public void singleEmptyLine() {
		if (statements.isEmpty()) {
			add(emptyLine());
			return;
		}
		boolean lastWasEmptyLine = statements.get(statements.size() - 1) instanceof StringEmptyLine;
		if (!lastWasEmptyLine) {
			add(emptyLine());
		}
	}
}
