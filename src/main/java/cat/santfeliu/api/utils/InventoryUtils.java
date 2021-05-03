package cat.santfeliu.api.utils;

import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

public class InventoryUtils {

	private static PythonInterpreter pythonInterpreter = new PythonInterpreter();


	public static String getGuid() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("python/guid.py");
		pythonInterpreter.execfile(is);
		pythonInterpreter.exec("x = new()");
		return pythonInterpreter.get("x").asString();
	}
}
