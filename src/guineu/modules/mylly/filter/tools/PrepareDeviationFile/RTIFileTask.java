/*
 * Copyright 2007-2011 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.mylly.filter.tools.PrepareDeviationFile;

import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.StringWriter;
import java.io.Writer;
import org.jfree.xml.writer.AttributeList;
import org.jfree.xml.writer.XMLWriter;
import org.xml.sax.SAXException;

/**
 *
 * @author bicha
 */
public class RTIFileTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private String fileName;
        private String outputFileName;

        public RTIFileTask(RTIFileParameters parameters) {
                fileName = parameters.getParameter(RTIFileParameters.fileNames).getValue().getAbsolutePath();
                outputFileName = parameters.getParameter(RTIFileParameters.outputFileNames).getValue().getAbsolutePath();
        }

        public String getTaskDescription() {
                return "Filtering files with Prepare RTI File... ";
        }

        public double getFinishedPercentage() {
                return 1f;
        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public void cancel() {
                status = TaskStatus.CANCELED;
        }

        public void run() {
                status = TaskStatus.PROCESSING;
                try {
                        createNewFile();
                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(RTIFileTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }

        private void createNewFile() throws FileNotFoundException, IOException {
                BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
                List<RIList> names = new ArrayList<RIList>();
                String line;
                RIList list = null;
                while ((line = br.readLine()) != null) {
                        if (line.contains("Name:")) {
                                if (list != null) {
                                        names.add(list);
                                }
                                list = new RIList();
                                line = line.substring(line.indexOf("Name:") + 6);
                                list.names = line.split(", ");
                        }
                        if (line.contains("CAS#:")) {
                                if (list != null) {
                                        list.CAS = line.substring(line.indexOf("CAS#:") + 6, line.indexOf(" NIST#"));
                                }
                        }

                        if (line.contains("Value:") == true) {
                                if (list != null && list.RI == 0) {
                                        line = line.substring(line.indexOf("Value:") + 7, line.indexOf("iu"));
                                        list.RI = Double.valueOf(line).doubleValue();
                                        line = br.readLine();
                                        if (line.contains("Column Type")) {
                                                line = line.substring(line.indexOf("Type:") + 6);
                                                list.columnType = line;
                                        }
                                        if (line.contains("Column Type: Capillary")) {
                                                list.cap = true;
                                        }
                                } else if (list != null && list.RI != 0) {
                                        line = line.substring(line.indexOf("Value:") + 7, line.indexOf("iu"));
                                        double value = Double.valueOf(line).doubleValue();
                                        line = br.readLine();
                                        if ((line.contains("Column Type: Capillary") && !list.cap) || list.columnType.length() == 0) {
                                                list.RI = value;
                                                line = line.substring(line.indexOf("Type:") + 6);
                                                list.columnType = line;
                                                list.cap = true;
                                        }

                                }
                        }
                }

                createXMLFile(names);


        }

        private void createXMLFile(List<RIList> names) throws FileNotFoundException, IOException {
                FileWriter w = new FileWriter(this.outputFileName);
                XMLWriter xmlW = new XMLWriter(w);
                xmlW.writeXmlDeclaration();
                xmlW.allowLineBreak();

                xmlW.startBlock();
                AttributeList attributes = new AttributeList();
                xmlW.writeTag("Data", false);

                for (RIList list : names) {
                        xmlW.startBlock();
                        xmlW.writeTag("Metabolite", false);
                        for (String name : list.names) {
                                xmlW.startBlock();
                                xmlW.writeTag("Name", false);
                                xmlW.writeText(name);
                                xmlW.writeCloseTag("Name");
                                xmlW.endBlock();
                        }

                        xmlW.startBlock();
                        xmlW.writeTag("CAS", false);
                        xmlW.writeText(list.CAS);
                        xmlW.writeCloseTag("CAS");
                        xmlW.endBlock();

                        xmlW.startBlock();
                        xmlW.writeTag("RI", false);
                        xmlW.writeText(String.valueOf(list.RI));
                        xmlW.writeCloseTag("RI");
                        xmlW.endBlock();

                        attributes.setAttribute("Type", list.columnType);
                        xmlW.startBlock();
                        xmlW.writeTag("Column", attributes, false);
                        xmlW.writeText(String.valueOf(list.RI));
                        xmlW.writeCloseTag("Column");                        
                        xmlW.endBlock();
                        xmlW.writeCloseTag("Metabolite");
                        xmlW.endBlock();
                }

                xmlW.endBlock();

                xmlW.close();


        }

        class RIList {

                boolean cap = false;
                String columnType = "";
                String[] names;
                String CAS = "";
                double RI = 0;
                String[] synonims;
                String Spectrum;
        }
}
