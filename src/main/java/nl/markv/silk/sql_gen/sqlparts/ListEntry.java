package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public interface ListEntry {

	void entryText(@Nonnull StringBuilder sql, boolean isLast);

	@Nonnull
	static ListEntry comment(@Nonnull String... messageParts) {
		assert messageParts.length >= 1;
		assert !messageParts[0].startsWith("--"): "comment prefix is automatically added";
		return new StringComment(messageParts);
	}

	@Nonnull
	static ListEntry of(@Nonnull String... messageParts) {
		return listEntry(messageParts);
	}

	@Nonnull
	static ListEntry listEntry(@Nonnull String... messageParts) {
		assert messageParts.length >= 1;
		int last = messageParts.length - 1;
		assert !messageParts[0].startsWith("--"): "use `comment` method for comments";
		assert !messageParts[last].endsWith(";"): "should not end in a semicolon";
		assert !messageParts[last].endsWith(","): "trailing comma is added automatically";
		assert !messageParts[last].endsWith("\n"): "trailing newline is added automatically";
		return new StringListEntry(messageParts);
	}
}
