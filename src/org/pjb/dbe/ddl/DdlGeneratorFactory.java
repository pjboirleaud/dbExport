package org.pjb.dbe.ddl;

import org.pjb.dbe.Configuration;

public class DdlGeneratorFactory {
	public static IDdlGenerator getDdlGenerator() {
		String url = Configuration.getInstance().getStringValue("url");
		boolean mysql = url.contains(":mysql:");
		boolean oracle = url.contains(":oracle:");

		boolean proprietary = Configuration.getInstance().getBooleanValue("export.ddl.native", false);

		boolean oracle2mysql = Configuration.getInstance().getBooleanValue("export.ddl.to.mysql", false);
		boolean mysql2oracle = Configuration.getInstance().getBooleanValue("export.ddl.to.oracle", false);

		if (mysql) {
			if (proprietary) {
				return new MySqlDdlGenerator();
			}
			if (mysql2oracle) {
				return new OracleDdlGenerator();
			}
			return new MySqlDdlGenerator();
		}

		if (oracle) {
			if (proprietary) {
				return new OracleDdlGenerator();
			}
			if (oracle2mysql) {
				return new MySqlDdlGenerator();
			}
			return new OracleDdlGenerator();
		}

		return new StandardDdlGenerator();
	}
}