package cat.santfeliu.api.utils;

import com.fasterxml.jackson.databind.JsonNode;

import cat.santfeliu.api.model.InputOutputIoModel;

/**
 *
 * @author realor
 */
public abstract class ObjectConverter {
	protected final InputOutputIoModel io;

	public ObjectConverter(InputOutputIoModel io) {
		this.io = io;
	}

	public void begin() throws Exception {
	}

	public abstract JsonNode convert(JsonNode object);

	public void end() throws Exception {
	}
}
