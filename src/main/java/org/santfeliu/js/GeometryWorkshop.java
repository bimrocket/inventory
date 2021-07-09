package org.santfeliu.js;

import java.io.Reader;
import java.io.StringReader;

import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeometryWorkshop {
	private static final Logger log = LoggerFactory.getLogger(GeometryWorkshop.class);

	public static JsonNode toPointCentroid(JsonNode geojson, String epsg) {
		ObjectMapper mapper = new ObjectMapper();
		GeometryJSON gjson = new GeometryJSON();
		Reader reader;
		try {
			reader = new StringReader(mapper.writeValueAsString(geojson).strip());
			Geometry geo = gjson.read(reader);
			Point p = geo.getCentroid();
			return mapper.readTree(gjson.toString(p));
		} catch (Exception e) {
			log.info("toPointCentroid@GeometryWorkshop - couldn't transform to point GeoJson :: {} and exception ::", geojson.toPrettyString(), e);
			return null;
		}
	}
}
