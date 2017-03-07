package org.pjb.dbe.data;

import java.sql.Connection;
import java.util.List;

public abstract interface IDataGenerator {
	public abstract void generateData(Connection paramConnection, String paramString1, String paramString2,
			List<String> paramList);
}