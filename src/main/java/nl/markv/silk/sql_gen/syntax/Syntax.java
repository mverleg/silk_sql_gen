package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.sql_gen.writer.SqlWriter;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	void prelude(@Nonnull SqlWriter sql);

	void postlude(@Nonnull SqlWriter sql);

	void startTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable String description);

	void endTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name);

	String dataTypeName(@Nonnull SqlWriter sql, @Nonnull String type);

	String autoValueName(@Nonnull SqlWriter sql, @Nonnull LongColumn.AutoOptions autoValue);

	void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull String name, @Nonnull String dataTypeName, boolean nullable, @Nullable String autoValueName, @Nullable String defaultValue);

	void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue);

	void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey);
}