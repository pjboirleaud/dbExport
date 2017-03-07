package org.pjb.dbe.data;

public class DataGeneratorFactory {
	public static IDataGenerator getDataGenerator() {
		return new StandardDataGenerator();
	}
}