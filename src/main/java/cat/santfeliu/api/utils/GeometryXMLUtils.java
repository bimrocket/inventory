package cat.santfeliu.api.utils;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xembly.Directives;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GeometryXMLUtils {
	private static final Logger log = LoggerFactory.getLogger(GeometryXMLUtils.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static Directives putGeometryObjInXML(Directives dir, JsonNode geomObj) {
		String type = geomObj.get("type").asText();
		if (type.equals("Point")) {
			dir.add("Point").attr("xmlns", "http://www.opengis.net/gml");
			dir.add("coordinates").attr("decimal", ".").attr("cs", ",").attr("ts", " ");
			String pointCoordsStr = "";
			ArrayNode coords = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coords.size(); i++) {
				JsonNode pointCoords = coords.get(i);
					pointCoordsStr += ((i == 0) ? pointCoords.asDouble()
							: ("," + pointCoords.asDouble()));
			}
			dir.set(pointCoordsStr);
			dir.up().up();
			log.debug("putGeometryObjInXML@GeometryXMLUtils - add point {} of geom key", pointCoordsStr);

		} else if (type.equals("LineString")) {
			dir.add("LineString");
			dir.add("coordinates").attr("xmlns", "http://www.opengis.net/gml").attr("decimal", ".").attr("cs", ",").attr("ts", " ");
			String coordsStr = "";
			ArrayNode coords = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coords.size(); i++) {
				if (i != 0) {
					coordsStr += " ";
				}
				ArrayNode coordsLine = mapper.valueToTree(coords.get(i));
				for (int j= 0; j < coordsLine.size(); j++) {
					double coord = coordsLine.get(j).asDouble();
					if (j != 0) {
						coordsStr += ",";
					}
					coordsStr += coord;
				}
			}
			dir.set(coordsStr);
			dir.up().up();
			log.debug("putGeometryObjInXML@GeometryXMLUtils - add LinesString {} of geom key", coordsStr);
		} else if (type.equals("MultiPolygon")) {
			dir.add("MultiPolygon").attr("srsName", "http://www.opengis.net/gml/srs/epsg.xml#25831");
			ArrayNode coordsMultiPolygon = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coordsMultiPolygon.size(); i++) {
				dir.add("polygonMember");
				dir.add("Polygon");
				ArrayNode coordsPolygon = mapper.valueToTree(coordsMultiPolygon.get(i));
				for (int j= 0; j < coordsPolygon.size(); j++) {
					if (j == 0) {
						dir.add("outerBoundaryIs");
					} else {
						dir.add("innerBoundaryIs");
					}
					dir.add("LinearRing");
					dir.add("coordinates").attr("xmlns", "http://www.opengis.net/gml").attr("decimal", ".").attr("cs", ",").attr("ts", " ");
					ArrayNode coordsLinearRing = mapper.valueToTree(coordsPolygon.get(j));
					StringBuilder coordsLinear = new StringBuilder();
					for (int c = 0; c < coordsLinearRing.size(); c++) {
						if (c != 0) {
							coordsLinear.append(" ");
						}
						ArrayNode coordsLinearRingXY = mapper.valueToTree(coordsLinearRing.get(c));
						double x = coordsLinearRingXY.get(0).asDouble();
						double y = coordsLinearRingXY.get(1).asDouble();
						coordsLinear.append(x).append(",").append(y);
					}
					dir.set(coordsLinear.toString());
					dir.up().up().up();
				}
				dir.up().up();
			}
			dir.up();
		}
		return dir;
	}
}
