package es.florida.AEV02Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLController {
    public String country;
    public String population;
    public String density;
    public String area;
    public String fertility;
    public String age;
    public String urban;
    public String share;

    public XMLController(String[] values) {
        this.country = values[0];
        this.population = values[1];
        this.density = values[2];
        this.area = values[3];
        this.fertility = values[4];
        this.age = values[5];
        this.urban = values[6];
        this.share = values[7];
    }

    /**
     * 
     * @param tempArr
     * @return the String containing individual XML after creating the file
     */
    public static String generateXMLFromFile(String[] tempArr) {
        XMLController xmlFileContent = new XMLController(tempArr);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("Country");

            doc.appendChild(root);

            Element country = doc.createElement("country");
            country.appendChild(doc.createTextNode(xmlFileContent.country));
            root.appendChild(country);

            Element population = doc.createElement("population");
            population.appendChild(doc.createTextNode(xmlFileContent.population));
            root.appendChild(population);

            Element density = doc.createElement("density");
            density.appendChild(doc.createTextNode(xmlFileContent.density));
            root.appendChild(density);

            Element area = doc.createElement("area");
            area.appendChild(doc.createTextNode(xmlFileContent.area));
            root.appendChild(area);

            Element fertility = doc.createElement("fertility");
            fertility.appendChild(doc.createTextNode(xmlFileContent.fertility));
            root.appendChild(fertility);

            Element age = doc.createElement("age");
            age.appendChild(doc.createTextNode(xmlFileContent.age));
            root.appendChild(age);

            Element urban = doc.createElement("urban");
            urban.appendChild(doc.createTextNode(xmlFileContent.urban));
            root.appendChild(fertility);

            Element share = doc.createElement("share");
            share.appendChild(doc.createTextNode(xmlFileContent.share));
            root.appendChild(share);

            // _________
            File xmlFolder = new File("./XML");
            xmlFolder.mkdir();
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            try {
                FileWriter fw = new FileWriter("XML/" + xmlFileContent.country + ".xml");
                StreamResult result = new StreamResult(fw);
                aTransformer.transform(source, result);
                fw.close();

                StringWriter outputString = new StringWriter();

                aTransformer.transform(new DOMSource(doc), new StreamResult(outputString));

                return outputString.toString();

            } catch (IOException | TransformerException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 
     * @param con
     */
    public static void importXMLIntoDatabase(Connection con) {
        preparePopulationTable(con);
        try {
            File xmlFolder = new File("./XML");
            File[] xmlFiles = xmlFolder.listFiles();
            for (File file : xmlFiles) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                InputStream inputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                InputSource inputSource = new InputSource(reader);
                
                Document document = dBuilder.parse(inputSource);
                document.getDocumentElement().normalize();
                // Document document = dBuilder.parse(file);
                /* Element root = document.getDocumentElement(); */
                NodeList nodeList = document.getElementsByTagName("Country");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        String[] textContextSplit = eElement.getTextContent().split("\\n");
                        String country = textContextSplit[1];
                        System.out.println(country);
                        String population = textContextSplit[2];
                        String density = textContextSplit[3];
                        String area = textContextSplit[4];
                        String age = textContextSplit[5];
                        String fertility = textContextSplit[6];
                        String share = textContextSplit[7];

                        insertCountryIntoDB(con, country, population, density, area, age, fertility, share);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void preparePopulationTable(Connection con) {

        try {
            PreparedStatement createTableStatement = null;
            PreparedStatement truncateTableStatement = null;
            String createTableSQL = "CREATE TABLE IF NOT EXISTS population ("
                    + "country VARCHAR(30), "
                    + "population VARCHAR(30), "
                    + "density VARCHAR(30), "
                    + "area VARCHAR(30), "
                    + "age VARCHAR(30), "
                    + "fertility VARCHAR(30), "
                    + "share VARCHAR(30)"
                    + ");";
            createTableStatement = con.prepareStatement(createTableSQL);
            createTableStatement.execute();

            String truncateTableSQL = "TRUNCATE TABLE population;";
            truncateTableStatement = con.prepareStatement(truncateTableSQL);
            truncateTableStatement.execute();

            if (createTableStatement != null)
                createTableStatement.close();
            if (truncateTableStatement != null)
                truncateTableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertCountryIntoDB(Connection con, String country, String population, String density,
            String area, String age,
            String fertility, String share) {

        PreparedStatement insertStatement = null;

        try {
            String insertSQL = "INSERT INTO population (country, population, density, area, age, fertility, share) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?);";

            insertStatement = con.prepareStatement(insertSQL);
            insertStatement.setString(1, country);
            insertStatement.setString(2, population);
            insertStatement.setString(3, density);
            insertStatement.setString(4, area);
            insertStatement.setString(5, age);
            insertStatement.setString(6, fertility);
            insertStatement.setString(7, share);

            insertStatement.executeUpdate();

            System.out.println("Table content cleared and data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (insertStatement != null)
                    insertStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
