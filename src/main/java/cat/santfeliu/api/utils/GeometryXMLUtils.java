package cat.santfeliu.api.utils;

import cat.santfeliu.api.exceptions.ApiErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.operation.IncompatibleOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xembly.Directives;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;

public class GeometryXMLUtils {
	private static final Logger log = LoggerFactory.getLogger(GeometryXMLUtils.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static Directives putGeometryObjInXML(Directives dir, JsonNode geomObj) {
		String type = geomObj.get("type").asText();
		if (type.equals("Point")) {
			dir.add("Point").attr("xmlns", "http://www.opengis.net/gml");
			dir.add("coordinates");
			String pointCoordsStr = "";
			ArrayNode coords = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coords.size(); i++) {
				JsonNode pointCoords = coords.get(i);
				pointCoordsStr += ((i == 0) ? pointCoords.asDouble()
						: (" " + pointCoords.asDouble()));
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
		else if(type.equals("Polygon")){
			dir.add("Polygon");
			dir.add("coordinates");
			ArrayNode coordsPolygon = mapper.valueToTree(geomObj.get("coordinates"));
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
		else if(type.equals("MultiPoint")){
			dir.add("MultiPoint");
			ArrayNode coordsMultiPoint = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coordsMultiPoint.size(); i++) {
				dir.add("pointMember");
				dir.add("Point").attr("xmlns", "http://www.opengis.net/gml");
				dir.add("coordinates");
				String pointCoordsStr = "";
				ArrayNode coords = mapper.valueToTree(coordsMultiPoint.get(i));
				for (int j = 0; j < coords.size(); j++) {
					JsonNode pointCoords = coords.get(j);
					pointCoordsStr += ((j == 0) ? pointCoords.asDouble()
							: (" " + pointCoords.asDouble()));
				}
				dir.set(pointCoordsStr);
				dir.up().up().up();
				log.debug("putGeometryObjInXML@GeometryXMLUtils - add point {} of geom key", pointCoordsStr);
			}
			dir.up();
			log.debug("putGeometryObjInXML@GeometryXMLUtils - add MultiPoint of geom key");
		}
		else if(type.equals("MultiLineString")){
			dir.add("MultiLineString");
			ArrayNode coordsMultiLineString = mapper.valueToTree(geomObj.get("coordinates"));
			for (int i = 0; i < coordsMultiLineString.size(); i++) {
				dir.add("lineStringMember");
				dir.add("LineString");
				dir.add("coordinates").attr("xmlns", "http://www.opengis.net/gml").attr("decimal", ".").attr("cs", ",").attr("ts", " ");
				String coordsStr = "";
				ArrayNode coordsLineString = mapper.valueToTree(coordsMultiLineString.get(i));
				for (int j = 0; j < coordsLineString.size(); j++) {
					if (j != 0) {
						coordsStr += " ";
					}
					ArrayNode coordsLine = mapper.valueToTree(coordsLineString.get(j));
					for (int k= 0; k < coordsLine.size(); k++) {
						double coord = coordsLine.get(k).asDouble();
						if (k != 0) {
							coordsStr += ",";
						}
						coordsStr += coord;
					}
				}
				dir.set(coordsStr);
				dir.up().up().up();
				log.debug("putGeometryObjInXML@GeometryXMLUtils - add LinesString {} of geom key", coordsStr);
			}
			dir.up();
		}
		else if(type.equals("GeometryCollection")){
			dir.add("GeometryCollection");
			ArrayNode collection = mapper.valueToTree(geomObj.get("geometries"));
			for(int i = 0; i<collection.size(); i++){
				dir = putGeometryObjInXML(dir,collection.get(i));
			}
			dir.up();
		}

		return dir;
	}

	public static JsonNode toSingleGeometry(JsonNode geometry) {
		String type = geometry.get("type").asText();

		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(toSingleGeometry(collection.get(i)));
			}
			geometry = setNode(type,newCollection,true);
		}
		else {
			if(type.contains("Multi")){
				geometry = setNode(type.substring(5),mapper.valueToTree(geometry.get("coordinates").get(0)),false);
			}
		}

		return geometry;
	}

	public static JsonNode toMultiGeometry(JsonNode geometry) {
		String type = geometry.get("type").asText();

		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(toMultiGeometry(collection.get(i)));
			}
			geometry = setNode(type,newCollection,true);
		}
		else{
			if(!type.contains("Multi")) {
				ArrayNode arr = mapper.createArrayNode();
				arr.add(geometry.get("coordinates"));
				geometry = setNode("Multi"+ type,arr,false);
			}
		}
		return geometry;
	}

	public static JsonNode toPoint(JsonNode geometry) {
		String type = geometry.get("type").asText();

		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(toPoint(collection.get(i)));
			}
			geometry = setNode(type,newCollection,true);
		}
		else {
			Geometry geo = null;
			if (!type.contains("Multi")) {
				geo = nodeToGeometry(geometry);
				Point p = geo.getInteriorPoint();
				ArrayNode arrGeo = mapper.createArrayNode();
				arrGeo.add(p.getX()).add(p.getY());
				geometry= setNode("Point",arrGeo,false);
			} else {
				JsonNode aux;
				ArrayNode arMultiPoint = mapper.createArrayNode();
				for (int i = 0; i < geometry.get("coordinates").size(); i++) {
					aux = setNode(type,mapper.valueToTree(geometry.get("coordinates").get(i)),false);
					geo = nodeToGeometry(aux);
					Point p = geo.getInteriorPoint();

					ArrayNode arPoint = mapper.createArrayNode();
					arPoint.add(p.getX()).add(p.getY());
					arMultiPoint.add(arPoint);
				}
				geometry = setNode("MultiPoint",arMultiPoint,false);
			}
		}
		return geometry;
	}

	public static JsonNode toLineString(JsonNode geometry) {

		String type = geometry.get("type").asText();
		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(toLineString(collection.get(i)));
			}
			geometry = setNode(type,newCollection,true);
		}
		else{
			if(!type.equals("LineString") && !type.equals("MultiLineString")) {
				if (type.equals("Point")) {
					String error = String.format("Incorrect transformation of type '%s' to LineString", type);
					log.error("toLineString@GeometryXMLUtils - {}", error);
					throw new UnsupportedOperationException(error);
				} else {
					if (type.equals("MultiPolygon")) {
						ArrayNode arrPolygon = mapper.createArrayNode();
						for (int i = 0; i < geometry.get("coordinates").size(); i++) {
							for (int j = 0; j < geometry.get("coordinates").get(i).size(); j++) {
								arrPolygon.add(geometry.get("coordinates").get(i).get(j));
							}
						}
						geometry = setNode("MultiLineString", arrPolygon,false);
					} else {
						ArrayNode arrLineString = mapper.createArrayNode();
						if (type.equals("MultiPoint")) {
							arrLineString = mapper.valueToTree(geometry.get("coordinates"));
						} else if (type.equals("Polygon")) {
							for (int i = 0; i < geometry.get("coordinates").size(); i++) {
								for (int j = 0; j < geometry.get("coordinates").get(i).size(); j++) {
									arrLineString.add(geometry.get("coordinates").get(i).get(j));
								}
							}
						}
						geometry = setNode("LineString",arrLineString,false);
					}
				}
			}
		}
		return geometry;
	}

	public static JsonNode toPolygon(JsonNode geometry) throws JsonProcessingException {

		String type = geometry.get("type").asText();
		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(toPolygon(collection.get(i)));
			}
			geometry = setNode(type,newCollection,true);
		}
		else {
			if(!type.equals("Polygon") && !type.equals("MultiPolygon")) {
				if (type.equals("Point")) {
					String error = String.format("Incorrect transformation of type '%s' to Polygon", type);
					log.error("toPoligon@GeometryXMLUtils - {}", error);
					throw new UnsupportedOperationException(error);
				}
				else {
					if (type.equals("LineString") || type.equals("MultiPoint")) {
						if (isPolygon(geometry.get("coordinates"))) {
							ArrayNode arrPolygon = mapper.createArrayNode();
							arrPolygon.add(mapper.valueToTree(geometry.get("coordinates")));
							geometry = setNode("Polygon",arrPolygon,false);
						} else {
							String error = String.format("Incorrect transformation of type '%s' to Polygon, does not meet the conditions", type);
							log.error("toPoligon@GeometryXMLUtils - {}", error);
							throw new UnsupportedOperationException(error);
						}
					} else if (type.equals("MultiLineString")) {
						ArrayNode arrPolygon = mapper.createArrayNode();
						for (int i = 0; i < geometry.get("coordinates").size(); i++) {
							if (isPolygon(geometry.get("coordinates").get(i))) {
								ArrayNode arrLinearRing = mapper.createArrayNode();
								arrLinearRing.add(geometry.get("coordinates").get(i));
								arrPolygon.add(arrLinearRing);
							} else {
								String error = String.format("Incorrect transformation of type '%s' to Polygon, does not meet the conditions", type);
								log.error("toPoligon@GeometryXMLUtils - {}", error);
								throw new UnsupportedOperationException(error);
							}
						}
						geometry = setNode("MultiPolygon", arrPolygon,false);
					}
				}
			}
		}
		return geometry;
	}

	public static JsonNode bufferPoint(JsonNode geometry, double radius) {

		String type = geometry.get("type").asText();
		if(type.equals("GeometryCollection")){
			ArrayNode collection = mapper.valueToTree(geometry.get("geometries"));
			ArrayNode newCollection = mapper.createArrayNode();
			for(int i = 0; i<collection.size(); i++){
				newCollection.add(bufferPoint(collection.get(i),radius));
			}
			geometry = setNode(type,newCollection,true);
		}
		else{
			Geometry geo = nodeToGeometry(geometry);
			if (geo != null) {
				geo = geo.buffer(radius);

				String polygon = new GeometryJSON().toString(geo);
				try {
					geometry = mapper.readTree(polygon);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		return geometry;
	}

	private static Boolean isPolygon(JsonNode ar){
		ArrayNode first = mapper.valueToTree(ar.get(0));
		ArrayNode last = mapper.valueToTree(ar.get(ar.size() - 1));
		Boolean polygon = false;
		if (first.get(0).asDouble() == last.get(0).asDouble() && first.get(1).asDouble() == last.get(1).asDouble()) polygon = true;
		return polygon;
	}

	private static JsonNode setNode(String type, ArrayNode array, Boolean collection){
		JsonNode node = null;
		ObjectNode ob = mapper.createObjectNode();
		ob.put("type",type);
		if(collection){
			ob.put("geometries",array);
		}
		else{
			ob.put("coordinates",array);
		}

		try {
			node = mapper.readTree(mapper.writeValueAsString(ob));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return node;
	}

	private static Geometry nodeToGeometry(JsonNode geometry){
		GeometryJSON gjson = new GeometryJSON();
		Reader reader = null;
		Geometry geo = null;
		try {
			reader = new StringReader(mapper.writeValueAsString(geometry));
			geo = gjson.read(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return geo;
	}

}
