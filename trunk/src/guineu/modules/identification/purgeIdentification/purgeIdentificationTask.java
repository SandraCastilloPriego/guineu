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
package guineu.modules.identification.purgeIdentification;

import guineu.data.Dataset;
import guineu.data.IdentificationType;
import guineu.data.PeakListRow;
import guineu.data.parser.impl.Lipidclass;
import guineu.data.LCMSColumnName;
import guineu.desktop.Desktop;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class purgeIdentificationTask extends AbstractTask {

        private Desktop desktop;
        private Dataset dataset;
        private double count;
        private int NRows;
        Lipidclass LipidClassLib;

        public purgeIdentificationTask(Dataset dataset, Desktop desktop) {
                this.dataset = dataset;
                this.desktop = desktop;
                this.LipidClassLib = new Lipidclass();
        }

        public String getTaskDescription() {
                return "Purge Identification ";
        }

        public double getFinishedPercentage() {
                return (count / this.NRows);
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        if (dataset != null) {
                                for (int i = 0; i < dataset.getNumberRows(); i++) {
                                        PeakListRow lipid = dataset.getRow(i);
                                        if (lipid == null || ((String) lipid.getVar("getIdentificationType")).compareTo(IdentificationType.MSMS.toString()) == 0) {
                                                continue;
                                        }
                                        this.getName(lipid);
                                }
                        }
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public void getName(PeakListRow lipid) {

                try {
                        if (((String) lipid.getVar("getName")).matches(".*Cer.*")) {
                                if ((Double) lipid.getVar("getRT") > 430 || (Double) lipid.getVar("getMZ") < 340 || !((String) lipid.getVar("getName")).matches(".*d18:1.*")) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        String name = ((String) lipid.getVar("getName"));
                                        name = name.substring(name.indexOf("/"));
                                        Pattern carbons = Pattern.compile("\\d\\d?");
                                        Matcher matcher = carbons.matcher(name);
                                        if (matcher.find()) {
                                                double num = Double.valueOf(name.substring(matcher.start(), matcher.end()));

                                                if (num < 12) {
                                                        this.getFirstName(lipid);
                                                        this.getName(lipid);
                                                }
                                        }
                                }

                        } else if (((String) lipid.getVar("getName")).matches(".*Lyso.*")) {
                                if ((Double) lipid.getVar("getRT") > 300 || (Double) lipid.getVar("getMZ") > 650) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        Pattern carbons = Pattern.compile("\\d\\d?");
                                        Matcher matcher = carbons.matcher(((String) lipid.getVar("getName")));
                                        if (matcher.find()) {
                                                double num = Double.valueOf(((String) lipid.getVar("getName")).substring(matcher.start(), matcher.end()));
                                                if (num < 12 || num > 24) {
                                                        this.getFirstName(lipid);
                                                        this.getName(lipid);
                                                }
                                        }
                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*PC.*") || ((String) lipid.getVar("getName")).matches(".*PE.*")) {
                                if ((Double) lipid.getVar("getRT") < 300 || (Double) lipid.getVar("getRT") > 420 || (Double) lipid.getVar("getMZ") < 550) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        Pattern carbons = Pattern.compile("\\d\\d?");
                                        Matcher matcher = carbons.matcher(((String) lipid.getVar("getName")));
                                        if (matcher.find()) {
                                                double num = Double.valueOf(((String) lipid.getVar("getName")).substring(matcher.start(), matcher.end()));
                                                if (num < 30 || num > 40) {
                                                        this.getFirstName(lipid);
                                                        this.getName(lipid);
                                                }
                                        }
                                }

                        } else if (((String) lipid.getVar("getName")).matches(".*PS.*") || ((String) lipid.getVar("getName")).matches(".*PI.*")) {
                                lipid.setVar("setName", "unknown");
                                lipid.setVar(LCMSColumnName.VTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.ALLVTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.IDENTIFICATION.getSetFunctionName(), IdentificationType.UNKNOWN.toString());
                                /*if (lipid.getRT() < 300 || lipid.getRT() > 420 || lipid.getMZ() < 550) {
                                this.getFirstName(lipid);
                                this.getName(lipid);
                                } else {
                                Pattern carbons = Pattern.compile("\\d\\d?");
                                Matcher matcher = carbons.matcher(lipid.getName());
                                if (matcher.find()) {
                                double num = Double.valueOf(lipid.getName().substring(matcher.start(), matcher.end()));
                                if (num > 41) {
                                this.getFirstName(lipid);
                                this.getName(lipid);
                                }
                                }
                                }*/
                        } else if (((String) lipid.getVar("getName")).matches(".*MG.*")) {
                                /* if (lipid.getRT() > 300 || lipid.getMZ() > 500) {
                                this.getFirstName(lipid);
                                this.getName(lipid);
                                }*/
                                lipid.setVar("setName", "unknown");
                                lipid.setVar(LCMSColumnName.VTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.ALLVTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.IDENTIFICATION.getSetFunctionName(), IdentificationType.UNKNOWN.toString());
                        } else if (((String) lipid.getVar("getName")).matches(".*SM.*")) {
                                if ((Double) lipid.getVar("getRT") > 420 || (Double) lipid.getVar("getRT") < 330 || !((String) lipid.getVar("getName")).matches(".*d18:1.*")) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        String name = ((String) lipid.getVar("getName"));
                                        name = name.substring(name.indexOf("/") + 1);
                                        //System.out.println("name" + name);
                                        //System.out.println(name.substring(name.indexOf(":") + 1, name.indexOf(")")));
                                        double num = Double.valueOf(name.substring(name.indexOf(":") + 1, name.indexOf(")")));
                                        //System.out.println("numero" + num);
                                        if (num > 3) {
                                                this.getFirstName(lipid);
                                                this.getName(lipid);
                                        }

                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*PA.*") || ((String) lipid.getVar("getName")).matches(".*PG.*")) {
                                lipid.setVar("setName", "unknown");
                                lipid.setVar(LCMSColumnName.VTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.ALLVTT.getSetFunctionName(), "");
                                lipid.setVar(LCMSColumnName.IDENTIFICATION.getSetFunctionName(), IdentificationType.UNKNOWN.toString());
                                /* if (lipid.getRT() > 410 || lipid.getMZ() < 550) {
                                this.getFirstName(lipid);
                                this.getName(lipid);
                                }else{
                                Pattern carbons = Pattern.compile("\\d\\d?");
                                Matcher matcher = carbons.matcher(lipid.getName());
                                if (matcher.find()) {
                                double num = Double.valueOf(lipid.getName().substring(matcher.start(), matcher.end()));
                                if (num > 41) {
                                this.getFirstName(lipid);
                                this.getName(lipid);
                                }
                                }
                                }*/
                        } else if (((String) lipid.getVar("getName")).matches(".*DG.*")) {
                                if ((Double) lipid.getVar("getRT") > 410 || (Double) lipid.getVar("getMZ") < 350 || ((String) lipid.getVar("getName")).matches(".*e.*")) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        Pattern carbons = Pattern.compile("\\d\\d?");
                                        Matcher matcher = carbons.matcher(((String) lipid.getVar("getName")));
                                        if (matcher.find()) {
                                                double num = Double.valueOf(((String) lipid.getVar("getName")).substring(matcher.start(), matcher.end()));
                                                if (num > 40) {
                                                        this.getFirstName(lipid);
                                                        this.getName(lipid);
                                                }
                                        }
                                }

                        } else if (((String) lipid.getVar("getName")).matches(".*TG.*")) {
                                if ((Double) lipid.getVar("getRT") < 410) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        Pattern carbons = Pattern.compile("\\d\\d?");
                                        Matcher matcher = carbons.matcher(((String) lipid.getVar("getName")));
                                        if (matcher.find()) {
                                                double num = Double.valueOf(((String) lipid.getVar("getName")).substring(matcher.start(), matcher.end()));
                                                if (num < 42 || num > 60) {
                                                        this.getFirstName(lipid);
                                                        this.getName(lipid);
                                                }
                                        }
                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*ChoE.*")) {
                                if ((Double) lipid.getVar("getRT") > 350 || (Double) lipid.getVar("getMZ") < 550) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*CL.*")) {
                                if ((Double) lipid.getVar("getRT") < 410 || (Double) lipid.getVar("getMZ") < 1000) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*FA.*")) {
                                if ((Double) lipid.getVar("getRT") > 300 || (Double) lipid.getVar("getMZ") > 550) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                }
                        } else if (((String) lipid.getVar("getName")).matches(".*unknown.*")) {
                                if (((String) lipid.getVar("getAllNames")).length() > 7) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                }
                        }

                        if (((String) lipid.getVar("getName")).matches(".*ee.*")) {
                                if (((String) lipid.getVar("getAllNames")).length() > 7) {
                                        this.getFirstName(lipid);
                                        this.getName(lipid);
                                } else {
                                        lipid.setVar("setName", "unknown");
                                        lipid.setVar(LCMSColumnName.VTT.getSetFunctionName(), "");
                                        lipid.setVar(LCMSColumnName.ALLVTT.getSetFunctionName(), "");
                                        lipid.setVar(LCMSColumnName.IDENTIFICATION.getSetFunctionName(), IdentificationType.UNKNOWN.toString());
                                }
                        }
                } catch (Exception e) {
                        lipid.setVar("setName", "unknown");
                        lipid.setVar(LCMSColumnName.VTT.getSetFunctionName(), "");
                        lipid.setVar(LCMSColumnName.ALLVTT.getSetFunctionName(), "");
                        lipid.setVar(LCMSColumnName.IDENTIFICATION.getSetFunctionName(), IdentificationType.UNKNOWN.toString());
                        // System.out.println("getName ->  " + e.getMessage());
                        return;
                }
        }

        public void getFirstName(PeakListRow lipid) {

                String[] lipidNames = null;
                try {
                        lipidNames = ((String) lipid.getVar("getAllNames")).split(" // ");
                } catch (Exception e) {
                        lipid.setVar("setName", "unknown");
                        // System.out.println("e ->  " + e.getMessage());
                }
                if (lipidNames == null || lipidNames.length < 2) {
                        try {
                                if (((String) lipid.getVar("getAllNames")).length() > 7) {
                                        lipid.setVar("setName", (String) lipid.getVar("getAllNames"));
                                        lipid.setVar("setAllNames", "");
                                } else {
                                        lipid.setVar("setName", "unknown");
                                }
                        } catch (Exception ee) {
                                lipid.setVar("setName", "unknown");
                                // System.out.println("ee ->  " + ee.getMessage());
                        }
                } else {
                        if (lipidNames[0].length() > 7) {
                                lipid.setVar("setName", lipidNames[0]);
                        }
                        if (lipidNames.length > 1) {
                                String newName = "";
                                for (int i = 1; i < lipidNames.length; i++) {
                                        newName += lipidNames[i] + " // ";
                                }
                                lipid.setVar("setAllNames", newName);
                        } else {
                                lipid.setVar("setAllNames", "");
                        }
                }
        }
}
