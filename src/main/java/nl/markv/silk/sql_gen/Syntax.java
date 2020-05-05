package nl.markv.silk.sql_gen;

import javax.annotation.Nonnull;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	void prelude(@Nonnull SqlWriter sql);

	void postlude(@Nonnull SqlWriter sql);

}
