/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.database.openQualityControlFileDB;

import guineu.data.impl.peaklists.SimplePeakListRowOther;
import java.text.DecimalFormat;
import java.text.ParseException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class SampleInfo {

        String sampleName;
        String date;
        Data LysoPC, PC_50, TG_50, LPC, PC, TGC;
        DecimalFormat formatter = new DecimalFormat("####.##");

        public void setSampleName(String name, String date) {
            this.sampleName = name;
            this.date = date;
        }

        public void setLysoPC(String[] data) {
            LysoPC = new Data(data);
        }

        public void setPC(String[] data) {
            PC_50 = new Data(data);
        }

        public void setTG(String[] data) {
            TG_50 = new Data(data);
        }

        public void setLPC(String[] data) {
            LPC = new Data(data);
        }

        public void setPCD(String[] data) {
            PC = new Data(data);
        }

        public void setTGC(String[] data) {
            TGC = new Data(data);
        }

        public SimplePeakListRowOther getRow(DescriptiveStatistics Stats[]) {
            SimplePeakListRowOther row = new SimplePeakListRowOther();
            int cont = 1;
            row.setPeak(String.valueOf(cont++), sampleName);
            //Lyso
            row.setPeak(String.valueOf(cont++), String.valueOf(LysoPC.RT));
            Stats[0].addValue(LysoPC.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(LysoPC.heightArea));
            Stats[1].addValue(LysoPC.heightArea);

            row.setPeak(String.valueOf(cont++), getLysoPCratio());
            Stats[2].addValue(Double.valueOf(getLysoPCratio()));

            //PC
            row.setPeak(String.valueOf(cont++), String.valueOf(PC_50.RT));
            Stats[3].addValue(PC_50.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(PC_50.heightArea));
            Stats[4].addValue(PC_50.heightArea);

            row.setPeak(String.valueOf(cont++), getPCratio());
            Stats[5].addValue(Double.valueOf(getPCratio()));

            //TG
            row.setPeak(String.valueOf(cont++), String.valueOf(TG_50.RT));
            Stats[6].addValue(TG_50.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(TG_50.heightArea));
            Stats[7].addValue(TG_50.heightArea);

            row.setPeak(String.valueOf(cont++), getTGratio());
            Stats[8].addValue(Double.valueOf(getTGratio()));

            row.setPeak(String.valueOf(cont++), date);
            row.setPeak(String.valueOf(cont++), LysoPC.time);
            return row;
        }

        public SimplePeakListRowOther getRow2(DescriptiveStatistics superStats[]) {
            SimplePeakListRowOther row = new SimplePeakListRowOther();
            int cont = 1;
            row.setPeak(String.valueOf(cont++), sampleName);
            row.setPeak(String.valueOf(cont++), getlysoPCHeight());
            superStats[0].addValue(Double.valueOf(getlysoPCHeight()));
            row.setPeak(String.valueOf(cont++), getPC_50Height());
            superStats[1].addValue(Double.valueOf(getPC_50Height()));
            row.setPeak(String.valueOf(cont++), getTG_50Height());
            superStats[2].addValue(Double.valueOf(getTG_50Height()));
            row.setPeak(String.valueOf(cont++), getLPCHeight());
            superStats[3].addValue(Double.valueOf(getLPCHeight()));
            row.setPeak(String.valueOf(cont++), getLPCRT());
            superStats[4].addValue(Double.valueOf(getLPCRT()));
            row.setPeak(String.valueOf(cont++), getPCHeight());
            superStats[5].addValue(Double.valueOf(getPCHeight()));
            row.setPeak(String.valueOf(cont++), getPCRT());
            superStats[6].addValue(Double.valueOf(getPCRT()));
            row.setPeak(String.valueOf(cont++), getTGCHeight());
            superStats[7].addValue(Double.valueOf(getTGCHeight()));
            row.setPeak(String.valueOf(cont++), getTGCRT());
            superStats[8].addValue(Double.valueOf(getTGCRT()));
            return row;
        }

        public String getLysoPCratio() {
            try {
                return formatter.format(LysoPC.height / LPC.height).toString();
            } catch (Exception e) {
                return String.valueOf(LysoPC.height / LPC.height);
            }
        }

        public String getLysoPCRT() {
            return String.valueOf(LysoPC.RT);
        }

        public String getPCratio() {
            try {
                return formatter.format(PC_50.height / PC.height).toString();
            } catch (Exception e) {
                return String.valueOf(PC_50.height / PC.height);
            }
        }

        public String getPC_50RT() {
            return String.valueOf(PC_50.RT);
        }

        public String getTGratio() {
            try {
                return formatter.format(TG_50.height / TGC.height).toString();
            } catch (Exception e) {
                return String.valueOf(TG_50.height / TGC.height);
            }
        }

        public String getTGRT() {
            return String.valueOf(TG_50.RT);
        }

        public String getTGCRT() {
            return String.valueOf(TGC.RT);
        }

        public String getPCRT() {
            return String.valueOf(PC.RT);
        }

        public String getLPCRT() {
            return String.valueOf(LPC.RT);
        }

        public String getPC_50Height() {
            return String.valueOf(PC_50.height);
        }

        public String getlysoPCHeight() {
            return String.valueOf(LysoPC.height);
        }

        public String getTG_50Height() {
            return String.valueOf(TG_50.height);
        }

        public String getLPCHeight() {
            return String.valueOf(LPC.height);
        }

        public String getPCHeight() {
            return String.valueOf(PC.height);
        }

        public String getTGCHeight() {
            return String.valueOf(TGC.height);
        }

        public String getFormat() {
            String format = "<td>" + String.valueOf(LysoPC.RT) + " </td>" +
                    "<td>" + String.valueOf(LysoPC.heightArea) + "</td>" +
                    "<td>" + getLysoPCratio() + "</td>" +
                    "<td>" + String.valueOf(PC_50.RT) + "</td>" +
                    "<td>" + String.valueOf(PC_50.heightArea) + "</td>" +
                    "<td>" + getPCratio() + "</td>" +
                    "<td>" + String.valueOf(TG_50.RT) + "</td>" +
                    "<td>" + String.valueOf(TG_50.heightArea) + "</td>" +
                    "<td>" + getTGratio() + "</td>" +
                    "<td>" + date + "</td>" +
                    "<td>" + LysoPC.time + "</td></tr>";

            return format;
        }
    

   class Data {

    String Name;
    double RT;
    double height;
    double ratio;
    double heightArea;
    double signalToNoise;
    String time;

    public Data(String[] fields) {
        try {
            DecimalFormat formatter = new DecimalFormat("####.##");
            this.Name = fields[0];
            this.RT = formatter.parse(fields[1]).doubleValue();
            this.height = formatter.parse(fields[2]).doubleValue();
            this.ratio = formatter.parse(fields[3]).doubleValue();
            this.heightArea = formatter.parse(fields[4]).doubleValue();
            this.time = fields[5];
        } catch (ParseException ex) {
            this.Name = fields[0];
            this.RT = Double.valueOf(fields[1]).doubleValue();
            this.height = Double.valueOf(fields[2]).doubleValue();
            this.ratio = Double.valueOf(fields[3]).doubleValue();
            this.heightArea = Double.valueOf(fields[4]).doubleValue();
            this.time = fields[5];

            //Logger.getLogger(ReportTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}