import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        String fileNameXML = "data.xml";
        List<Employee> listXML = parseXML(fileNameXML);
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "dataXML.json");
    }

    public static void writeString(String json, String fileNameJSON) {
        try (FileWriter fileWriter = new FileWriter(fileNameJSON)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        //System.out.println(json);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
            // list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<Employee> parseXML(String fileNameXML) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> listEmployee = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXML));
        Node root = doc.getDocumentElement();
        NodeList childs = root.getChildNodes();
        for (int childIndex = 0; childIndex < childs.getLength(); ++childIndex) {
            Node child = childs.item(childIndex);
            String nodeName = child.getNodeName();
            if (nodeName.equals("employee")) {
                addEmployee(child, listEmployee);
            }
        }
        return listEmployee;
    }

    private static void addEmployee(Node node, List<Employee> employees) {
        Employee employee = new Employee();
        NodeList childs = node.getChildNodes();
        Map<String, String> fields = new HashMap<>();
        for (int childIndex = 0; childIndex < childs.getLength(); ++childIndex) {
            Node child = childs.item(childIndex);
            fields.put(child.getNodeName(), child.getTextContent());
        }
        employee.id = Long.parseLong(fields.get("id"));
        employee.firstName = fields.get("firstName");
        employee.lastName = fields.get("lastName");
        employee.country = fields.get("country");
        employee.age = Integer.parseInt(fields.get("age"));

        employees.add(employee);
    }
}

