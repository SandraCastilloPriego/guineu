package guineu.modules.filter.Alignment;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.modules.filter.Alignment.data.AlignmentStruct;
import guineu.modules.filter.Alignment.data.AlignStructMol;

import guineu.desktop.Desktop;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;

public class Alignment {

    private Dataset[] datasets;
    private SimpleLCMSDataset result;
    private String[] type;
    private float progress;
    private Vector<RegressionStruct> vRegressionStruct;
    private AlignmentParameters AParameters;
    private Desktop desktop;

    public Alignment(Dataset[] datasets, AlignmentParameters AParameters, Desktop desktop) {
        this.datasets = datasets.clone();
        this.desktop = desktop;
        this.AParameters = AParameters;

        this.vRegressionStruct = new Vector<RegressionStruct>();
        this.result = new SimpleLCMSDataset(getFrameName());

        //intro all experiments into the new dataset result      
        for (int i = 0; i < datasets.length; i++) {
            for (String experimentName : ((SimpleLCMSDataset) datasets[i]).getAllColumnNames()) {
                result.addColumnName(experimentName);
            }
        }
        this.init_general_type();
        this.MolNamesCorrection();

        this.progress = 0.0f;
    }

    /**
     * Constructs the name of the new dataset from the name of all coming datasets,
     * puting a " - " between them. 
     * @return the name of the new dataset
     */
    public String getFrameName() {
        String Title = "";
        for (Dataset dataset : datasets) {
            Title += dataset.getDatasetName() + " - ";
        }
        return Title.substring(0, Title.length() - 3);

    }

    /**
     * The field with the name of the lipids has the name of some lipids together:
     * "GPGro(40:8e) - 4; GPA(40:8e) - 4; GPA(39:8e) - 5; GPGro(39:8e) - 5"
     * It takes only the firts one and puts it into the AlignmentName field:
     * "GPGro(40:8e)"
     */
    public void MolNamesCorrection() {
      /*  for (Dataset dataset : datasets) {
            for (PeakListRow row : ((SimpleLCMSDataset) dataset).getRows()) {
                String lipidn = row.getName();
                lipidn = lipidn.split(" ")[0];
                row.setName(lipidn);
            }
        }*/
    }

    public SimpleLCMSDataset getDataset() {
        return this.result;
    }

    public void run() {
        try {
            this.AlignmentCurve();
        } catch (Exception exception) {
            System.out.println("Alignment.java ---> run()" + exception);
        }
    }

    /**
     * It does the alignment of the lipids and fills the table with the result.
     */
    private void AlignmentCurve() {
        try {
            //there is one alignment solution for each combination of datasets. It 
            //stores each solution into one Vector[], and all solutions together
            //into this Vector<Vector[]> result. 
            Vector<Vector[]> vectorResult = new Vector<Vector[]>();

            for (int j = 0; j < this.datasets.length; j++) {
                for (int e = j + 1; e < this.datasets.length; e++) {
                    //creates three Vectors (one for each group of lipids --> 
                    //"Lyso", "GPCho" and "TAG") because it does the alignment
                    //algorithm independently for each group.
                    Vector[] VArray = new Vector[this.type.length];

                    for (int i = 0; i < this.type.length; i++) {
                        Vector<AlignStructMol> v = this.NewDatasets(j, e, this.regularExpression(this.type[i]));
                        VArray[i] = v;
                    }
                    progress++;

                    //does the RANSAC algorithm 
                    CurveAlignment curveAlignment = new CurveAlignment(desktop);
                    Vector[] solution = curveAlignment.CurveRansac(VArray, AParameters);

                    vectorResult.addElement(solution);
                }
            }


            Vector<AlignmentStruct> bestSolutions = new Vector<AlignmentStruct>();
            int data = 0;
            for (int i = 0; i < this.type.length; i++) {
                data = 0;
                for (int j = 0; j < this.datasets.length; j++) {
                    for (int e = j + 1; e < this.datasets.length; e++) {
                        AlignmentStruct chrom = new AlignmentStruct();
                        chrom.v = vectorResult.elementAt(data++)[i];
                        chrom.ID1 = j;
                        chrom.ID2 = e;
                        bestSolutions.add(chrom);
                    }
                }
                if (bestSolutions.size() > 0) {
                    this.fixAlignment(bestSolutions, this.regularExpression(this.type[i]));
                    this.printChart(bestSolutions);
                    this.createResultDataset(bestSolutions);
                }
                bestSolutions.removeAllElements();
            }


            progress = this.datasets.length * this.type.length;

        } catch (Exception exception) {
            System.out.println("Alignment.java--> AlignmentCurve() " + exception);
        }
    }

    private void createResultDataset(Vector<AlignmentStruct> bestSolutions) {
        try {
            for(AlignmentStruct aS : bestSolutions){
                
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    

    

    /**
     * Creates a Vector with all possible alignend lipid. "AlignStructMol" is
     * the information about two lipids with the same name coming from two different
     * datasets. If there are 2 lipids with the name X in one dataset, and in the 
     * other dataset there is only 1, this function will create 2 "AlingStructMol".
     * One for each possible combination. 
     * @param Index1 index of the first dataset
     * @param Index2 index of the second dataset
     * @param type group of lipids
     * @return Vector with all possible combination of lipids with the same name
     * between both datasets.
     */
    private Vector<AlignStructMol> NewDatasets(int Index1, int Index2, String type) {
        try {
         /*   Vector<AlignStructMol> v = new Vector<AlignStructMol>();
            int cont = 0;
            for (PeakListRow row : ((SimpleLCMSDataset) this.datasets[Index1]).getRows()) {
                String name1 = row.getName();

                if (name1.matches(type)) {
                    int cont2 = 0;
                    for (PeakListRow row2 : ((SimpleLCMSDataset) this.datasets[Index2]).getRows()) {
                        String name2 = row2.getName();
                        if (name1.compareTo(name2) == 0) {
                            boolean STD = false;
                            if (row2.getStandard() == 1 && row.getStandard() == 1) {
                                STD = true;
                            }
                            v.addElement(new AlignStructMol(Index1, Index2, row, row2, STD, false, this.getPickAverage(Index1, cont), this.getPickAverage(Index2, cont2++)));
                        }
                    }
                } else if (name1.matches(".*unknown.*") && this.getGroup(row).matches(type)) {
                    int cont2 = 0;
                    for (PeakListRow row2 : ((SimpleLCMSDataset) this.datasets[Index2]).getRows()) {
                        if (row2.getName().matches(".*unknown.*") && this.isPossibleLipid(row.getRT(), row.getMZ(), row2.getRT(), row.getMZ()) && this.getGroup(row2).matches(type)) {
                            // System.out.println(lipid1.getMZ() +" - " + lipid2.getMZ());
                            boolean STD = false;
                            if (row2.getStandard() == 1 && row.getStandard() == 1) {
                                STD = true;
                            }
                            v.addElement(new AlignStructMol(Index1, Index2, row, row2, STD, false, this.getPickAverage(Index1, cont), this.getPickAverage(Index2, cont2++)));
                        }
                    }
                }
                cont++;
            }
            return v;*/
			return null;

        } catch (Exception exception) {
            System.out.println("Alignment.java--> NewDatasets() " + exception);
            return null;
        }

    }

    public boolean isPossibleLipid(double RT, double MZ, double RT2, double MZ2) {
        try {
            if (this.isSameMZ(MZ, MZ2)) {
                return true;
            }
            /*  if(RT <= 300 && MZ <= 550 && RT2 <= 300 && MZ2 <= 550){               
            if(this.isSameMZ(MZ, MZ2+1.007825))return true;
            if(this.isSameMZ(MZ2, MZ+1.007825))return true;            
            }
            if(RT <= 300 && MZ <= 650 && RT2 <= 300 && MZ2 <= 650){
            if(this.isSameMZ(MZ, MZ2+1.007825))return true;
            if(this.isSameMZ(MZ2, MZ+1.007825))return true;            
            }
            if(RT <= 300 && MZ <= 500 && RT2 <= 300 && MZ2 <= 500){
            if(this.isSameMZ(MZ, MZ2-17.0027)) return true;
            if(this.isSameMZ(MZ2, MZ-17.0027)) return true;         
            }
            if(RT <= 421 && RT >= 300 && MZ >= 550 && RT2 <= 421 && RT2 >= 300 && MZ2 >= 550){
            if(this.isSameMZ(MZ, MZ2+1.007825))return true;
            if(this.isSameMZ(MZ2, MZ+1.007825))return true;            
            }
            if(RT <= 430 && MZ >= 340 && RT2 <= 430 && MZ2 >= 340){
            if(this.isSameMZ(MZ, MZ2-17.0027)) return true;
            if(this.isSameMZ(MZ2, MZ-17.0027)) return true;         
            }
            if(RT <= 420 && RT >= 330 && RT2 <= 420 && RT2 >= 330){
            if(this.isSameMZ(MZ, MZ2+1.007825))return true;
            if(this.isSameMZ(MZ2, MZ+1.007825))return true;            
            }
            if(RT <= 410 && MZ >= 550 && RT2 <= 410 && MZ2 >= 550){
            if(this.isSameMZ(MZ, MZ2-96.9691)) return true;
            if(this.isSameMZ(MZ2, MZ-96.9691)) return true;          
            }
            if(RT <= 410 && MZ >= 550 && RT2 <= 410 && MZ2 >= 550){
            if(this.isSameMZ(MZ, MZ2-171.0059)) return true;
            if(this.isSameMZ(MZ2, MZ-171.0059)) return true;         
            }
            if(RT <= 410 && MZ >= 350 && RT2 <= 410 && MZ2 >= 350){
            if(this.isSameMZ(MZ, MZ2+18.0344)) return true;
            if(this.isSameMZ(MZ2, MZ+18.0344)) return true;          
            }
            if(RT >= 410 && RT2 >= 410){
            if(this.isSameMZ(MZ, MZ2+18.0344)) return true;
            if(this.isSameMZ(MZ2, MZ+18.0344)) return true;       
            }
            if(RT >= 350 && MZ >= 550 && RT2 >= 350 && MZ2 >= 550){
            if(this.isSameMZ(MZ, MZ2+18.0344)) return true;
            if(this.isSameMZ(MZ2, MZ+18.0344)) return true;
            }
            if(RT >= 410 && MZ >= 1000 && RT2 >= 410 && MZ2 >= 1000){
            if(this.isSameMZ(MZ, MZ2+(2*22.98977-1.007825))) return true;
            if(this.isSameMZ(MZ2, MZ+(2*22.98977-1.007825))) return true;
            }
            if(RT >= 410 && MZ >= 1000 && RT2 >= 410 && MZ2 >= 1000){
            if(this.isSameMZ(MZ, MZ2+(2*22.98977-1.007825))) return true;
            if(this.isSameMZ(MZ2, MZ+(2*22.98977-1.007825))) return true;         
            }
            if(RT >= 410 && MZ >= 1000 && RT2 >= 410 && MZ2 >= 1000){
            if(this.isSameMZ(MZ, MZ2+22.98977)) return true;
            if(this.isSameMZ(MZ2, MZ+22.98977)) return true;
            }*/
            return false;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 
     * @param Index index of the dataset
     * @param lipidID 
     * @return the average of the peaks of all samples correponding to the 
     * lipid. 
     */
    private double getPickAverage(int Index, int lipidID) {
        double average = 0;
        PeakListRow smol2 = ((SimpleLCMSDataset) this.datasets[Index]).getRow(lipidID);
        for (Object p : smol2.getPeaks()) {
            average += (Double)p;
        }
        average /= smol2.getNumberPeaks();
        return average;
    }

    /**
     * Groups of lipids
     */
    private void init_general_type() {
        type = new String[3];
        // type[0] = "Cer";           
        type[0] = "Lyso";
        type[1] = "GPCho";
        type[2] = "TAG";
    // type[5] = "GPGro";
    }

    /**
     * In every lipid group there are differents kind of lipids. 
     * @param type group of the lipids (this group depend on the RT)
     * @return the regular expression needed to find the lipids of concrete group.
     */
    private String regularExpression(String type) {
        /* if(type.matches("^GPGro.*")){
        this.GPGro = true;
        return "^GPGro.*";
        }else*/ if (type.indexOf("GPCho") > -1) {
            /*if(this.GPGro){
            return "^GPCho.*|^SM.*|^DAG.*|^GPA.*|^GPIns.*";    
            }else{*/
            return "^GPCho.*|^SM.*|^DAG.*|^GPA.*|^GPIns.*|^GPGro.*|^Cer.*|^GPEtn.*|^GPSer.*|^PC.*|^DG.*|^PI.*|^PG.*|^PE.*|^PS.*";
        // }
        } else if (type.indexOf("TAG") > -1) {
            return "^TAG.*|^ChoE.*|^TG.*";
        } else if (type.indexOf("Lyso") > -1) {
            return "^Lyso.*|^MAG.*|^LP.*|^MG.*";
        }/*else if(type.matches("^Cer.*")){
        return "^Cer.*";
        }*/
        return type;
    }

    public float getProgress() {
        float total = this.datasets.length * this.type.length;
        float p = progress / total;
        return p;
    }

    private void fixAlignment(Vector<AlignmentStruct> bestchromosomes, String type) {
        this.fillRegressionStructVector(bestchromosomes, type);
        this.fillScores(bestchromosomes);
        this.seeMultipleIncoherences(bestchromosomes);
    // this.addUnknownLipids(bestchromosomes, type);
    }

    private void fillRegressionStructVector(Vector<AlignmentStruct> bestSolution, String type) {
        for (int i = 0; i < bestSolution.size(); i++) {
            AlignmentStruct solution = bestSolution.elementAt(i);
            RegressionStruct RStruct = new RegressionStruct();
            RStruct.type = type;
            RStruct.ID1 = solution.ID1;
            RStruct.ID2 = solution.ID2;
            SimpleRegression regression = new SimpleRegression();
            for (int j = 0; j < solution.v.size(); j++) {
                if (solution.v.elementAt(j).Aligned) {
                 //   regression.addData(solution.v.elementAt(j).lipid1.getRT(), solution.v.elementAt(j).lipid2.getRT());
                }
            }
            RStruct.intercept = regression.getIntercept();
            RStruct.slope = regression.getSlope();
            if (regression.getR() < 0 || regression.getN() < 2) {
                RStruct.control = false;
            }
            this.vRegressionStruct.addElement(RStruct);
        }
    }

    private void fillScores(Vector<AlignmentStruct> bestchromosomes) {
        for (int i = 0; i < bestchromosomes.size(); i++) {
            AlignmentStruct chrom = bestchromosomes.elementAt(i);
            for (int j = 0; j < chrom.v.size(); j++) {
                AlignStructMol lipid = chrom.v.elementAt(j);
                lipid.score = this.getScore(lipid);
            }
        }
    }

    private double getScore(AlignStructMol lipid) {
      /*  for (int i = 0; i < this.vRegressionStruct.size(); i++) {
            RegressionStruct RStruct = this.vRegressionStruct.elementAt(i);
            Pattern pat = Pattern.compile(RStruct.type);
            Matcher matcher = pat.matcher(lipid.lipid1.getName());
            if (matcher.find()) {
                if (RStruct.ID1 == lipid.IDDataset1 && RStruct.ID2 == lipid.IDDataset2 ||
                        RStruct.ID1 == lipid.IDDataset2 && RStruct.ID2 == lipid.IDDataset1) {
                    if (RStruct.control) {
                      //  return Math.abs(lipid.lipid2.getRT() - ((lipid.lipid1.getRT() * RStruct.slope) + RStruct.intercept));
                    } else {
                        return -2;
                    }
                }

            }
        }*/
        return -1;
    }

    private void seeMultipleIncoherences(Vector<AlignmentStruct> chrom) {
        try {
            AlignStructMol lipid1;
            AlignStructMol lipid2;

            for (int i = 0; i < chrom.size(); i++) {
                int ID1 = chrom.elementAt(i).ID1;
                int ID2 = chrom.elementAt(i).ID2;

                AlignmentStruct chrom1 = chrom.elementAt(i);
                for (int l = 0; l < chrom1.v.size(); l++) {

                    lipid1 = chrom1.v.elementAt(l);
                  //  if (lipid1.Aligned) {
                      //  AlignmentStruct chrom2 = this.getVector(chrom, ID2, i);

                      //  lipid2 = this.getLipid(lipid1.lipid1.getName(), lipid1.lipid2.getRT(), chrom2.v);
                    /*    if (lipid2 != null) {

                            AlignmentStruct chrom3 = this.getVector(chrom, ID1, i);

                            this.fixAlignment(chrom3.v, lipid1, lipid2);

                        }*/
                  //  }
                }

            }
        } catch (Exception exception) {
        // System.out.println("GeneticAlgorims.java --> seeMultiIncoherences() " + exception);
        }
    }

    public void fixAlignment(Vector<AlignStructMol> chrom3, AlignStructMol lipid1, AlignStructMol lipid2) {

      /*  Vector<AlignStructMol> v2 = new Vector<AlignStructMol>();
        for (int i = 0; i < chrom3.size(); i++) {
            if (chrom3.elementAt(i).lipid1.getName().compareTo(lipid1.lipid1.getName()) == 0) {
              //  if (chrom3.elementAt(i).lipid1.getRT() == lipid1.lipid1.getRT() || chrom3.elementAt(i).lipid2.getRT() == lipid1.lipid1.getRT()) {
             //       v2.addElement(chrom3.elementAt(i));
             //   }
            }
        }

        for (int i = 0; i < v2.size(); i++) {
            AlignStructMol lipid3 = v2.elementAt(i);
       /*  //   if (lipid3 != lipid1 && (lipid3.lipid1.getRT() == lipid2.lipid1.getRT() || lipid3.lipid1.getRT() == lipid2.lipid2.getRT() || lipid3.lipid2.getRT() == lipid2.lipid1.getRT() || lipid3.lipid2.getRT() == lipid2.lipid2.getRT())) {
            //System.out.println(lipid1.name +" RT 1: "+ lipid1.RT1 +" - " +lipid1.RT2 + " RT 2: "+ lipid2.RT1  +" - " +lipid2.RT2 + " RT 3: "+ lipid3.RT1 +" - " +lipid3.RT2);       


         //   } else {
                //System.out.println(lipid1.name +" RT 1: "+ lipid1.RT1 +" - " +lipid1.RT2 + " RT 2: "+ lipid2.RT1  +" - " +lipid2.RT2 + " RT 3: "+ lipid3.RT1 +" - " +lipid3.RT2);       
                if ((lipid1.score < 4 && lipid1.score > 0) &&
                        (lipid2.score < 4 && lipid2.score > 0)) {
                    if (lipid3.score < 4) {
                        lipid3.Aligned = true;
                        lipid1.Aligned = true;
                        lipid2.Aligned = true;
                    }
                }

                if (lipid1.score > 4) {
                    lipid1.Aligned = false;
                  //  if (lipid1.lipid1.getRT() == lipid2.lipid1.getRT() || lipid1.lipid2.getRT() == lipid2.lipid2.getRT() || lipid1.lipid2.getRT() == lipid2.lipid1.getRT() || lipid1.lipid1.getRT() == lipid2.lipid2.getRT()) {
                 //       lipid2.Aligned = false;
                 //   }
                 //   if (lipid1.lipid1.getRT() == lipid3.lipid1.getRT() || lipid1.lipid2.getRT() == lipid3.lipid1.getRT() || lipid1.lipid2.getRT() == lipid3.lipid1.getRT() || lipid1.lipid1.getRT() == lipid3.lipid2.getRT()) {
                  //      lipid3.Aligned = false;
                  //  }
                }

                if (lipid2.score > 4) {
                    lipid2.Aligned = false;
                    if (lipid2.lipid1.getRT() == lipid1.lipid1.getRT() || lipid2.lipid2.getRT() == lipid1.lipid2.getRT() || lipid2.lipid2.getRT() == lipid1.lipid1.getRT() || lipid2.lipid1.getRT() == lipid1.lipid2.getRT()) {
                        lipid1.Aligned = false;
                    }
                    if (lipid2.lipid1.getRT() == lipid3.lipid1.getRT() || lipid2.lipid2.getRT() == lipid3.lipid2.getRT() || lipid2.lipid2.getRT() == lipid3.lipid1.getRT() || lipid2.lipid1.getRT() == lipid3.lipid2.getRT()) {
                        lipid3.Aligned = false;
                    }
                }

                if (lipid3.score > 4) {
                    lipid3.Aligned = false;
                    if (lipid3.lipid1.getRT() == lipid1.lipid1.getRT() || lipid3.lipid2.getRT() == lipid1.lipid2.getRT() || lipid3.lipid2.getRT() == lipid1.lipid1.getRT() || lipid3.lipid1.getRT() == lipid1.lipid2.getRT()) {
                        lipid1.Aligned = false;
                    }
                    if (lipid3.lipid1.getRT() == lipid2.lipid1.getRT() || lipid3.lipid2.getRT() == lipid2.lipid2.getRT() || lipid3.lipid2.getRT() == lipid2.lipid1.getRT() || lipid3.lipid1.getRT() == lipid2.lipid2.getRT()) {
                        lipid2.Aligned = false;
                    }
                }
           // }*/
       // }
    }

  /*  public AlignmentStruct getVector(Vector<AlignmentStruct> chrom, int ID, int nonVector) {
        for (int sv = 0; sv < chrom.size(); sv++) {
            if (sv != nonVector) {
                if (chrom.elementAt(sv).ID1 == ID || chrom.elementAt(sv).ID2 == ID) {
                    return chrom.elementAt(sv);
                }
            }
        }
        return null;
    }

    ///mirar esta función, puede que esté mal. A lo mejor tendría que devolver más de un AlignStruct..
    public AlignStructMol getLipid(String name, double RT2, Vector<AlignStructMol> v) {
        Vector<AlignStructMol> v2 = new Vector<AlignStructMol>();
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < v.size(); i++) {
            AlignStructMol lipid = v.elementAt(i);
            if (lipid.lipid1.getName().compareTo(name) == 0) {
              /*  if (RT2 == lipid.lipid2.getRT() || RT2 == lipid.lipid1.getRT()) {
                    v2.addElement(lipid);
                }*/
        //    }

      //      if (lipid.Aligned) {
            //    regression.addData(lipid.lipid1.getRT(), lipid.lipid2.getRT());
      //      }
      //  }

     /*   double intercept = regression.getIntercept();
        double slope = regression.getSlope();

        int Index = 100;
        for (int i = 0; i < v2.size(); i++) {
          /*  double y = v2.elementAt(i).lipid2.getRT();
            double newY = (v2.elementAt(i).lipid1.getRT() * slope) + intercept;
            if (Math.abs(y - newY) < Index) {
                Index = i;
            }*/
     /*   }

        for (int i = 0; i < v2.size(); i++) {
            if (i != Index) {
                v2.elementAt(i).Aligned = false;
            }
        }
        if (Index != 100) {
            return v2.elementAt(Index);
        } else {
            return null;
        }
    }*/

  /*  private void addUnknownLipids(Vector<AlignmentStruct> bestSolutions, String type) {
        for (AlignmentStruct datasetAlignedStruct : bestSolutions) {
            RegressionStruct regStruct = this.getRegressionStruct(datasetAlignedStruct.ID1, datasetAlignedStruct.ID2, type);
            if (regStruct != null) {
                this.setUnknownLipids(datasetAlignedStruct, datasetAlignedStruct.ID1, datasetAlignedStruct.ID2, regStruct);
            }
        }
    }*/

    private RegressionStruct getRegressionStruct(int ID1, int ID2, String type) {
        for (RegressionStruct struct : this.vRegressionStruct) {
            if (struct.ID1 == ID1 && struct.ID2 == ID2 && struct.type.compareTo(type) == 0) {
                return struct;
            }
        }
        return null;
    }

    private void setUnknownLipids(AlignmentStruct datasetAlignedStruct, int simpleDataset, int simpleDataset2, RegressionStruct regStruct) {
        Vector<AlignStructMol> v = new Vector<AlignStructMol>();
        for (PeakListRow smol : ((SimpleLCMSDataset) this.datasets[simpleDataset]).getRows()) {
            for (PeakListRow smol2 : ((SimpleLCMSDataset) this.datasets[simpleDataset2]).getRows()) {
                String type2 = this.getGroup(smol2);
                if (type2 != null && regStruct.type.matches(type2)) {
                    continue;
                }
            /*    if (smol.getName().matches(".*unknown.*") && smol2.getName().matches(".*unknown.*") && regStruct.isAligned(smol.getRT(), smol2.getRT())) {
                    AlignStructMol mol = new AlignStructMol(simpleDataset, simpleDataset2, smol, smol2, false, true, this.getPickAverage(simpleDataset, smol.getID()), this.getPickAverage(simpleDataset2, smol2.getID()));
                    if (isSameMZ(smol.getMZ(), smol2.getMZ())) {
                        //System.out.println(((SimpleLipid) lipid).getMZ() + " - " + lipid2.getMZ() + " -> " + mol.picksScore);
                        v.addElement(mol);
                    }
                }*/
            }
        }
        this.deleteRepeatsAlignments(v);
        for (AlignStructMol mol : v) {
            datasetAlignedStruct.addLipids(mol);
        }
    }

    public boolean isSameMZ(double MZ, double MZ2) {
        if (MZ < MZ2 + 0.1 && MZ > MZ2 - 0.1) {
            return true;
        }
        if (MZ2 < MZ + 0.1 && MZ2 > MZ - 0.1) {
            return true;
        }
        return false;
    }

    private void deleteRepeatsAlignments(Vector<AlignStructMol> v) {
        for (int i = 0; i < v.size(); i++) {
            AlignStructMol Alignment1 = v.elementAt(i);
            if (Alignment1.Aligned) {
                for (int j = 0; j < v.size(); j++) {
                    if (i != j) {
                        AlignStructMol Alignment2 = v.elementAt(j);
                        if (Alignment2.Aligned) {
                          /*  if (Alignment1.lipid1.getRT() == Alignment2.lipid1.getRT() || Alignment1.lipid2.getRT() == Alignment2.lipid2.getRT()) {
                                //System.out.println(lipid1.RT1 +" - "+ lipid1.RT2 + ": " + lipid1.Aligned + " / " + lipid2.RT1 +" - "+ lipid2.RT2 + ": " + lipid2.Aligned);
                                if (Alignment1.picksScore < Alignment2.picksScore) {
                                    Alignment2.Aligned = false;
                                } else {
                                    Alignment1.Aligned = false;
                                }

                            }*/
                        }
                    }
                }
            }
        }
    }

    public String getGroup(PeakListRow lipid1) {

      /*  if (lipid1.getRT() < 300) {
            return "Lyso";
        }
        if (lipid1.getRT() >= 300 && lipid1.getRT() < 410) {
            return "GPCho";
        }
        if (lipid1.getRT() >= 410) {
            return "TAG";
        }*/
        return " ---- ";
    }

    public void printChart(Vector<AlignmentStruct> bestSolutions) {
        AlignmentChart chart = new AlignmentChart("final result");
        chart.printAlignmentChart();
        for (AlignmentStruct datasetAlignedStruct : bestSolutions) {
            chart.addSeries(datasetAlignedStruct.v, "serie");
        }
        this.desktop.addInternalFrame(chart);
        chart.setVisible(true);
    }
}
