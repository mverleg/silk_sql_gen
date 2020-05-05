package nl.markv.silk.sql_gen;

import javax.annotation.Nonnull;

public interface SqlWriter {

	void add(@Nonnull String txt);

	default void addLine(@Nonnull String txt) {
		add(txt);
		newline();
	}

	default void newline() {
		//TODO: which newline symbol?
		add("\n");
	}
}
