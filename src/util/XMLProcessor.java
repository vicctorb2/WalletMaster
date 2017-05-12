package util;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import model.Group;
import model.Month;
import model.State;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMLProcessor {

    public static final String DATA_FILENAME = "wallet-master.xml";
    public static final String ROOT_ELEMENT = "wallet-master";
    public static final String NAME = "name";
    public static final String GROUP = "group";
    public static final String MONTH = "month";
    public static final String STATE = "state";
    public static final String DAY = "day";
    public static final String SUMMARY = "summary";

    private List<String> groupNames = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    public void saveData() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement(ROOT_ELEMENT);
            for (Group group : groups) {
                Element groupElement = document.createElement(GROUP);
                groupElement.setAttribute(NAME, group.getGroupName());
                for (Month month : group.getMonthInfo()) {
                    Element monthElement = document.createElement(MONTH);
                    monthElement.setAttribute(NAME, month.getMonth());
                    for (State state : month.getStateList()) {
                        Element stateElement = document.createElement(STATE);
                        stateElement.setAttribute(NAME, state.getName());
                        for (int i = 0; i < state.getDailyData().size(); i++) {
                            Element dayElement = document.createElement(DAY + (i + 1));
                            dayElement.setTextContent(state.getDailyData().get(i).toString());
                            stateElement.appendChild(dayElement);
                        }
                        Element summary = document.createElement(SUMMARY);
                        summary.setTextContent(state.getSummary() + "");
                        stateElement.appendChild(summary);
                        monthElement.appendChild(stateElement);
                    }
                    groupElement.appendChild(monthElement);
                }
                root.appendChild(groupElement);
            }
            document.appendChild(root);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);
            Writer out = new OutputStreamWriter(new FileOutputStream(DATA_FILENAME), "UTF-8");
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void loadData() throws FileNotFoundException {
        try {
            DOMParser parser = new DOMParser();
            File file = new File(DATA_FILENAME);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");

            InputSource is = new InputSource(reader);

            parser.parse(is);
            Document document = parser.getDocument();

            Element root = document.getDocumentElement();
            List<Group> groupList = new ArrayList<>();

            NodeList groupNodes = root.getElementsByTagName(GROUP);

            for (int i = 0; i < groupNodes.getLength(); i++) {
                Element groupElement = (Element) groupNodes.item(i);
                Group group = new Group();
                group.setGroupName(groupElement.getAttribute(NAME));
                groupNames.add(groupElement.getAttribute(NAME));
                NodeList monthNodes = groupElement.getElementsByTagName(MONTH);
                for (int j = 0; j < monthNodes.getLength(); j++) {
                    Element monthElement = (Element) monthNodes.item(j);
                    Month month = new Month(monthElement.getAttribute(NAME));
                    NodeList stateNodes = monthElement.getElementsByTagName(STATE);
                    for (int k = 0; k < stateNodes.getLength(); k++) {
                        Element stateElement = (Element) stateNodes.item(k);
                        State state = new State();
                        group.addColumn(stateElement.getAttribute(NAME));
                        state.setName(stateElement.getAttribute(NAME));
                        NodeList dayNodes = stateElement.getChildNodes();
                        for (int t = 0; t < dayNodes.getLength(); t++) {
                            if (dayNodes.item(t).getNodeType() == Node.ELEMENT_NODE) {
                                if (dayNodes.item(t).getNodeName().equals(SUMMARY)) {
                                    state.setSummary(Double.parseDouble(dayNodes.item(t).getTextContent()));
                                } else {
                                    state.addValue(Double.parseDouble(dayNodes.item(t).getTextContent()));
                                }
                            }
                        }
                        month.addState(state);
                    }
                    group.addMonth(month);
                }
                groupList.add(group);
            }
            this.groups = groupList;
        } catch (SAXException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void addGroup(Group group) {
        groups.add(group);
        groupNames.add(group.getGroupName());
    }

    public void removeGroup(String groupName) {
        int index = groupNames.indexOf(groupName);
        if (index >= 0) {
            groupNames.remove(index);
            groups.remove(index);
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void changeGroup(String oldName, String newName) {
        groupNames.set(groupNames.indexOf(oldName), newName);
    }
}

