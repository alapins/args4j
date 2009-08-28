package org.kohsuke.args4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Metadataconfiguration.
 * This class holds all metadata for a class, mainly a list of @Options and @Arguments.
 *
 * @author Jan Mat�rne
 */
public class Config {

	/** All @Options. */
	public List<ConfigElement> options = new ArrayList<ConfigElement>();;

	/** All @Arguments. */
	public List<ConfigElement> arguments = new ArrayList<ConfigElement>();


	/**
	 * The ConfigElement is an <tt>&lt;option></tt> or <tt>&lt;argument></tt> tag
	 * in the xml configuration file.
	 * @author Jan Mat�rne
	 */
	public class ConfigElement {
		public String field;
		public String method;
		public String name;
		public String usage = "";
		public String handler;
		public String metavar = "";
		public String[] aliases = {};
		public boolean multiValued = false;
		public boolean required = false;
		/**
		 * Ensures that only a field XOR a method is set.
		 * @return
		 */
		public boolean isInvalid() {
			return field == null && method == null || field != null && method != null;
		}
	}

	/**
	 * SAX-Handler for reading the configuration file.
	 * @author Jan Mat�rne
	 */
	public class ConfigHandler extends DefaultHandler {
		public ConfigHandler(Config config) {
			super();
			this.config = config;
		}

		Config config;
		Config.ConfigElement currentCE;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("option") || qName.equals("argument")) {
				currentCE = new ConfigElement();
				currentCE.field = attributes.getValue("field");
				currentCE.handler = attributes.getValue("handler");
				currentCE.metavar = attributes.getValue("metavar");
				currentCE.method = attributes.getValue("method");
				currentCE.name = attributes.getValue("name");
				currentCE.usage = attributes.getValue("usage");
				currentCE.multiValued = Boolean.getBoolean(attributes.getValue("multiValued"));
				currentCE.required = Boolean.getBoolean(attributes.getValue("required"));
				if (attributes.getValue("aliases") != null) {
					currentCE.aliases = attributes.getValue("aliases").split(",");
				} else {
					currentCE.aliases = new String[]{};
				}
				(qName.equals("option") ? config.options : config.arguments).add(currentCE);
			}
		}
	}

	/**
	 * Parses a XML file and returns a Config object holding the information.
	 * @param fileInputStream source of the xml data
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Config parse(InputStream fileInputStream) throws IOException, SAXException {
		Config rv = new Config();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		ConfigHandler handler = rv.new ConfigHandler(rv);
		reader.setContentHandler(handler);
		reader.parse(new InputSource(fileInputStream));
		return rv;
	}

}