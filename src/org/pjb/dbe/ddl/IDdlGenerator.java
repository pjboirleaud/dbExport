package org.pjb.dbe.ddl;

import java.sql.Connection;
import java.util.List;

public abstract interface IDdlGenerator {
	public abstract void generateDdl(Connection paramConnection, String paramString1, String paramString2,
			List<String> paramList1, List<String> paramList2);

	public abstract void generateNativeDdl(Connection paramConnection, String paramString1, String paramString2,
			List<String> paramList1, List<String> paramList2);
}