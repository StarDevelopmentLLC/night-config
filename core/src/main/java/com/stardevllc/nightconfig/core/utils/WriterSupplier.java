package com.stardevllc.nightconfig.core.utils;

import java.io.IOException;
import java.io.Writer;

public interface WriterSupplier {
	Writer get() throws IOException;
}
