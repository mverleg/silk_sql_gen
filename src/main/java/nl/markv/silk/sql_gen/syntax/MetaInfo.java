package nl.markv.silk.sql_gen.syntax;

public class MetaInfo {
	/**
	 * Indicates whether a given column is the whole primary key, part of a larger key, or not a part at all.
	 */
	public enum PrimaryKey {
		Single,
		Composite,
		NotPart,
	}
}
