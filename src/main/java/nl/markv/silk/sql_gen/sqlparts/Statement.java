package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public interface Statement {

	void statementText(@Nonnull StringBuilder sql);

	@Nonnull
	static Statement comment(@Nonnull String... messageParts) {
		assert messageParts.length >= 1;
		assert !messageParts[0].startsWith("--"): "comment prefix is automatically added";
		return new StringComment(messageParts);
	}

	@Nonnull
	static Statement of(@Nonnull String... messageParts) {
		assert messageParts.length >= 1;
		int last = messageParts.length - 1;
		assert !messageParts[0].startsWith("--"): "use `comment` method for comments";
		assert !messageParts[last].endsWith(","): "should not end in a comma";
		assert !messageParts[last].endsWith(";"): "trailing semicolon is added automatically";
		assert !messageParts[last].endsWith("\n"): "trailing newline is added automatically";
		return new StringStatement(messageParts);
	}
}
