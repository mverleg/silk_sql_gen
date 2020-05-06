package nl.markv.silk.sql_gen.writer;

import java.util.List;

import javax.annotation.Nonnull;

public interface SqlWriter {

	void add(@Nonnull String txt);

	default void addLine(@Nonnull String txt) {
		add(txt);
		newline();
	}

	default void add(@Nonnull String txt, @Nonnull String... more) {
		add(txt);
		for (String t : more) {
			add(" ");
			add(t);
		}
	}

	default void delimitered(@Nonnull String delimiter, @Nonnull List<String> items) {
		boolean isFirst = true;
		for (String item : items) {
			if (isFirst) {
				isFirst = false;
			} else {
				add(delimiter);
			}
			add(item);
		}
	}

	default void addLine(@Nonnull String txt, @Nonnull String... more) {
		add(txt, more);
		newline();
	}

	default void newline() {
		//TODO: which newline symbol?
		add("\n");
	}

	void comment(@Nonnull String text);
}
