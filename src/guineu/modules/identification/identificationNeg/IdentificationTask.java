/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.identification.identificationNeg;

import com.softwareag.tamino.db.api.accessor.TAccessLocation;
import com.softwareag.tamino.db.api.accessor.TXMLObjectAccessor;
import com.softwareag.tamino.db.api.accessor.TXQuery;
import com.softwareag.tamino.db.api.connection.TConnection;
import com.softwareag.tamino.db.api.connection.TConnectionFactory;
import com.softwareag.tamino.db.api.objectModel.TXMLObject;
import com.softwareag.tamino.db.api.objectModel.TXMLObjectIterator;
import com.softwareag.tamino.db.api.objectModel.dom.TDOMObjectModel;
import com.softwareag.tamino.db.api.response.TResponse;
import guineu.data.Dataset;
import guineu.data.parser.impl.Lipidclass;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTableModel;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;

/**
 *
 * @author scsandra
 */
public class IdentificationTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private SimpleLCMSDataset dataset;
        private List<MZmineData> lines;
        private JTable table;
        private double count;
        private int NRows;
        Lipidclass LipidClassLib;

        public IdentificationTask(JTable table, Dataset dataset) {
                this.dataset = (SimpleLCMSDataset) dataset;
                this.table = table;
                this.lines = new ArrayList<MZmineData>();
                this.LipidClassLib = new Lipidclass();
        }

        public String getTaskDescription() {
                return "Identification... ";
        }

        public double getFinishedPercentage() {
                return (count / this.NRows);
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
                try {
                        status = TaskStatus.PROCESSING;
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(i);
                                MZmineData MZdata = new MZmineData();
                                MZdata.MZ = lipid.getMZ();
                                MZdata.RT = lipid.getRT();
                                MZdata.N_Found = (int) lipid.getNumFound();
                                if (lipid.getName() != null && !lipid.getName().isEmpty()) {
                                        MZdata.name = lipid.getName();
                                }
                                MZdata.std = lipid.getStandard();
                                MZdata.addPossibilities();
                                this.lines.add(MZdata);
                        }
                        this.findOtherDates();
                        DataTableModel model = new DatasetLCMSDataModel(dataset);
                        this.table.setModel(model);

                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        /**
         * Finds all possible lipids for each MZ.
         */
        public void findOtherDates() {
                lipidomicIdentification lipidLibrary = new lipidomicIdentification();
                lipidLibrary.Introhash_ToCommonNamesConversion();
                count = 0;
                try {
                        NRows = this.lines.size();
                        for (int i = 0; i < NRows; i++) {
                                if (status == TaskStatus.CANCELED || status == TaskStatus.ERROR) {
                                        break;
                                }
                                if ((Boolean) table.getValueAt(i, 0)) {
                                        MZmineData dates = this.lines.get(i);
                                        int NPosL = dates.possibleLipids.size();
                                        this.findLipids(NPosL, dates, lipidLibrary);
                                        this.writeTableResults(dates, i);
                                        this.lines.get(i).resetLipid();
                                }
                                count++;
                        }
                } catch (Exception exception) {
                        System.out.println("Identification.java -> findOtherDates() " + exception);
                }
        }

        /**
         * Writes the identified lipid in the table.
         * @param dates
         * @param Index
         */
        private void writeTableResults(MZmineData dates, int Index) {
                try {
                        Vector<String> lipidNames = new Vector<String>();

                        dates.removeRepeats();
                        dates.sortAbundaneScore();

                        for (int e = 0; e < dates.possibleLipids.size(); e++) {
                                MZlipidStore lipidStore = dates.possibleLipids.get(e);
                                if (lipidStore.commonName.compareTo("unknown") != 0 && !lipidStore.commonName.isEmpty()) {
                                        lipidNames.addElement(new String(lipidStore.commonName + " - " + lipidStore.scoreAbundance));
                                }
                        }

                        String LipidName = "";
                        for (int i = 0; i < lipidNames.size(); i++) {
                                if (i > 0) {
                                        LipidName += ";";
                                }
                                LipidName += lipidNames.elementAt(i);
                        }
                        if (lipidNames.size() == 0) {
                                LipidName = "unknown";
                        }
                        SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(Index);
                        lipid.setName(LipidName);
                        int lipidClass = this.LipidClassLib.get_class(lipid.getName());
                        lipid.setLipidClass(String.valueOf(lipidClass));
                        if (table != null) {
                                table.setValueAt(LipidName, Index, 4);
                                table.setValueAt(new Integer(lipidClass), Index, 5);
                                table.setValueAt(new Boolean(false), Index, 0);
                                //table.repaint();
                                table.revalidate();
                        }

                } catch (Exception e) {
                }
        }

        /**
         * Finds all dades for each possible lipid into the tamino database.
         * @param NPosL
         * @param dates
         * @param lipidLibrary
         */
        public void findLipids(int NPosL, MZmineData dates, lipidomicIdentification lipidLibrary) {
                try {
                        for (int e = 0; e < NPosL; e++) {
                                MZlipidStore lipidStore = dates.possibleLipids.get(e);
                                // System.out.println(dates.MZ + " "+ dates.RT+" "+ lipidStore.lipidClass + " " + String.valueOf(dates.getmzMax(e)) + " "+String.valueOf(dates.getmzMin(e)));
                                String querystring = "declare namespace tf = \"http://namespaces.softwareag.com/tamino/TaminoFunction\" for $a in input()/Compound where  tf:containsText($a/Molecule/Class,\"" + lipidStore.lipidClass + "\") and $a/Molecule/Isotopicdistribution/Mass[1] < " + String.valueOf(dates.getmzMax(e)) + " and  $a/Molecule/Isotopicdistribution/Mass[1] > " + String.valueOf(dates.getmzMin(e)) + " return  <Results> {$a/Molecule/Molecular_Formula} \n {$a/Molecule/Name}  \n{$a/Molecule/Isotopicdistribution/Mass} \n {$a/Molecule/Score_abundance} } </Results> sort by (Score_abundance)";


                                String lipid = this.TaminoConnect(querystring);
                                // System.out.println(lipid);
                                this.fillLipidData(lipid, lipidStore, dates, lipidLibrary);
                                this.findCommonName(dates, lipidStore);
                        }
                } catch (Exception e) {
                        System.out.println("Identification.java -> findLipids() " + e);
                }
        }

        /**
         * Extracts the data from the string "lipid". This string is the result from
         * the tamino database.
         * @param lipid
         * @param lipidStore
         * @param dates
         * @param lipidLibrary
         */
        public void fillLipidData(String lipid, MZlipidStore lipidStore, MZmineData dates, lipidomicIdentification lipidLibrary) {
                try {
                        if (lipid.compareTo("unknown") != 0) {
                                while (lipid.indexOf("</Results>") > -1) {
                                        String str = lipid.substring(0, lipid.indexOf("</Results>"));
                                        lipid = lipid.substring(lipid.indexOf("</Results>") + 9);
                                        String[] lipidNames = this.extractData(str, "<Name>", "</Name>");
                                        int Abundance = Integer.valueOf((this.extractData(str, "<Score_abundance>", "</Score_abundance>"))[0]);

                                        if (lipidStore.completeName != null) {
                                                MZlipidStore newStore = lipidStore.clone();
                                                dates.possibleLipids.add(newStore);
                                                this.setLipidStore(newStore, lipidNames[0], Abundance, this.extractData(str, "<Mass>", "</Mass>"), lipidLibrary, dates);
                                        } else {
                                                this.setLipidStore(lipidStore, lipidNames[0], Abundance, this.extractData(str, "<Mass>", "</Mass>"), lipidLibrary, dates);
                                        }
                                }

                        } else {
                                if (lipidStore.completeName != null) {
                                        MZlipidStore newStore = lipidStore.clone();
                                        dates.possibleLipids.add(newStore);
                                        this.setLipidStore(newStore, "unknown", 0, null, lipidLibrary, dates);
                                } else {
                                        this.setLipidStore(lipidStore, "unknown", 0, null, lipidLibrary, dates);
                                }
                        }
                } catch (Exception e) {
                        System.out.println("Identification.java -> fillLipidData() " + e);
                }
        }

        /**
         * Writes the dates into the class MZlipidStore "Store".
         * @param Store
         * @param lipidName
         * @param Abundance
         * @param Mass
         * @param lipidLibrary to get the commonName
         * @param dates
         */
        public void setLipidStore(MZlipidStore Store, String lipidName, int Abundance, String[] Mass, lipidomicIdentification lipidLibrary, MZmineData dates) {
                try {
                        dates.setCompleteName(Store, lipidName);
                        Store.scoreAbundance = Abundance;
                        Store.mass = Mass;
                        Store.setCommonName(lipidLibrary.getCommonName(Store.completeName, Store));
                        // System.out.println(dates.MZ + " - " + dates.RT + "----> " +Store.CExpected  +" ---> " + lipidName + "---->"+ Store.completeName +"--->" + Store.commonName);
                } catch (Exception e) {
                        dates.setCompleteName(Store, "unknow");
                        Store.scoreAbundance = 0;
                        Store.mass = null;
                        Store.setCommonName("unknown");
                        System.out.println("Identification.java -> setLipidStore() " + e);
                }
        }

        /**
         * Searches in "CommonLipid" tamino database all possible lipids with the
         * same MZ. Then calls the function "setCommonName(String, MZmineData)" to
         * get the common name of the lipid.
         * @param dates
         */
        public void findCommonName(MZmineData dates, MZlipidStore store) {
                try {
                        String querystring = "for $a in input()/CommonLipid where  $a/BasePeak < " + String.valueOf(dates.MZ + dates.getTolerance()) + "and  $a/BasePeak > " + String.valueOf(dates.MZ - dates.getTolerance()) + " return  <Element>{$a/Name} \n{$a/MonoIsoMass} \n{$a/BasePeak}</Element>";

                        String lipid = this.TaminoConnect(querystring);

                        this.setCommonName(lipid, dates, store);

                } catch (Exception exception) {
                        System.out.println("Identification.java -> run() " + exception);
                }
        }

        /**
         * Gets the commonName from the string "lipid" and writes it in the dates.
         * @param lipid String where are the possible lipids.
         * @param dates
         */
        public void setCommonName(String lipid, MZmineData dates, MZlipidStore store) {
                try {
                        String commonName = "";

                        if (lipid != null && !lipid.isEmpty()) {
                                String[] basePeak = this.extractData(lipid, "<BasePeak>", "</BasePeak>");
                                if (lipid.compareTo("unknown") == 0) {
                                        commonName = "unknown";
                                } else {
                                        String[] names = this.extractData(lipid, "<Name>", "</Name>");
                                        int Index = this.chooseName(basePeak, dates.MZ);
                                        commonName = names[Index];
                                        // commonName = this.RoleOutData(dates.RT, names[Index]);
                                }
                                MZlipidStore newStore = store.clone();
                                newStore.scoreAbundance = 0;
                                dates.setCommonName(newStore, commonName);
                                dates.possibleLipids.add(newStore);
                        }
                } catch (Exception e) {
                        System.out.println("Identification.java --> setCommonName() " + e);
                }
        }

        /**
         * Chooses the index of the mass nearest to MZ
         * @param basePeak
         * @param MZ
         * @return the index of the mass nearest to MZ
         */
        public int chooseName(String[] basePeak, double MZ) {
                try {
                        double rest = 0;
                        int Index = 0;
                        for (int i = 0; i < basePeak.length; i++) {
                                double dbasePeak = Double.valueOf(basePeak[i]);
                                double x = Math.abs(MZ - dbasePeak);
                                if (rest > x) {
                                        rest = x;
                                        Index = i;
                                }
                        }
                        return Index;
                } catch (Exception e) {
                        System.out.println("Identification.java --> chooseName() " + e);
                        return 0;
                }
        }

        /**
         * Finds all strings between "start" and "end".
         * @param lipid String with all lipids, from the tamino database
         * @param start
         * @param end
         * @return all strings between "start" and "end".
         */
        private String[] extractData(String lipid, String start, String end) {
                try {
                        Vector s = new Vector();
                        int i = 0;
                        while (lipid.indexOf(start) > -1) {
                                s.addElement(lipid.substring(lipid.indexOf(start) + start.length(), lipid.indexOf(end)));
                                lipid = lipid.substring(lipid.indexOf(end) + end.length());
                                i++;
                        }

                        String[] cad = new String[s.size()];
                        for (i = 0; i < s.size(); i++) {
                                cad[i] = (String) s.elementAt(i);
                        }
                        return cad;
                } catch (Exception e) {
                        System.out.println("Identification.java---> extractData()" + e);
                        return null;
                }
        }

        /**
         * Finds in the (teorical) tamino database lipids with m/z value between min and max.
         * @param min
         * @param max
         * @return Returns a string with the result of this search.
         */
        public synchronized String TaminoConnect(String querystring) {
                try {
                        TConnection connection = this.TaminoConnection();
                        TXMLObjectAccessor accessor = connection.newXMLObjectAccessor(TAccessLocation.newInstance("Lipidomics"), TDOMObjectModel.getInstance());
                        TXQuery query_safe = TXQuery.newInstance(querystring);
                        TResponse response_safe = accessor.xquery(query_safe);
                        connection.close();

                        return this.getTaminoResult(response_safe.getXMLObjectIterator());

                } catch (Exception exception) {
                        System.out.println("This database is not working now" + exception);
                        return null;
                }

        }

        /**
         * Connects to the Tamino Database
         * @return TConnection
         */
        public synchronized TConnection TaminoConnection() {
                try {
                        String DATABASE_URI = "http://sbtamino1.ad.vtt.fi:8060/tamino/BfxDB03";
                        TConnection connection = TConnectionFactory.getInstance().newConnection(DATABASE_URI);
                        return connection;
                } catch (Exception exception) {
                        System.out.println("This database is not working now" + exception);
                        return null;
                }
        }

        /**
         * Gets result and puts it in the var "Result";
         * @param iterator_safe
         * @return
         */
        public synchronized String getTaminoResult(TXMLObjectIterator iterator_safe) {
                try {
                        TXMLObject doc_safe;
                        String Result = "";
                        while (iterator_safe.hasNext()) {
                                StringWriter strw = new StringWriter();
                                doc_safe = iterator_safe.next();
                                doc_safe.writeTo(strw);
                                strw.close();
                                Result = Result + strw.toString();
                        }
                        iterator_safe.close();

                        if (Result.isEmpty()) {
                                Result = "unknown";
                        }

                        return Result;
                } catch (Exception exception) {
                        System.out.println("Indentification.java -> getTaminoResult() " + exception);
                        return "unknown";
                }
        }
}
