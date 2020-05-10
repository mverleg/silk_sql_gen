package nl.markv.silk.sql_gen.sqlparts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

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
		statement.ifPresent(s -> statements.add(s));
	}

	public void statementsText(@Nonnull StringBuilder sql) {
		for (Statement statement : statements) {
			statement.statementText(sql);
		}
	}
}
