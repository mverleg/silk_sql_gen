package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import nl.markv.silk.sql_gen.writer.SqlWriter;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	void prelude(@Nonnull SqlWriter sql);

	void postlude(@Nonnull SqlWriter sql);

}
