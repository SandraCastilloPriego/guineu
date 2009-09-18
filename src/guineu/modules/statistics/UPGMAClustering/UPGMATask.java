/*
 * Copyright 2007-2008 VTT Biotechnology
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

package guineu.modules.statistics.UPGMAClustering;




import guineu.data.impl.SimpleLCMSDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class UPGMATask implements Task {
    
   
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;      
    private Desktop desktop;
    private double progress = 0.0f;
    private String[] group1;
    private SimpleLCMSDataset dataset;
   // private Vector<node> nodes;
    
    public UPGMATask(String[] group1, SimpleLCMSDataset dataset, Desktop desktop){
        this.group1 = group1;       
        this.dataset = dataset;
        this.desktop = desktop;
       // this.nodes = new Vector<node>();
    }
    public String getTaskDescription() {
        return "UPGMA Clustering... ";
    }

    public double getFinishedPercentage() {
        return progress;
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
        try{   
            status = TaskStatus.PROCESSING; 
            UPGMA(createDistanceMatrix());           
            status = TaskStatus.FINISHED;
        }catch(Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
    
    public double[][] createDistanceMatrix(){
        double[][] distances = new double[group1.length][group1.length];        
        int cont1 = 0;       
        /*for(String sample : group1){
            SimpleExperiment exp = (SimpleExperiment) dataset.getExperiment(sample);
            int cont2 = 0;
            for(String sample2 : group1){
                SimpleExperiment exp2 = (SimpleExperiment) dataset.getExperiment(sample2);
                distances[cont1][cont2] = getDistance(exp.getConcentrations(), exp2.getConcentrations());
                //System.out.println(exp.getExperimentName() + " / " + exp2.getExperimentName() + " --> " + distances[cont1][cont2]);
                cont2++;
            }
            cont1++;
        }*/
        return distances;
    }

    private double getDistance(Vector<Double> concentrations, Vector<Double> concentrations2) {
        double distance = 0;
        for(int i = 0; i < concentrations.size(); i++){
            distance += Math.abs((Double)concentrations.elementAt(i) - (Double)concentrations2.elementAt(i));
        }
        distance /= concentrations.size();
        return distance;
    }
    
    private void UPGMA(double[][]distances){
        String result = "";
        Vector<cluster> Clusters = new Vector<cluster>();
        String[] names = group1;
        for(int i = 0; i < names.length; i++){           
            result += "("; 
        }
        for(int i = 0; i < names.length; i++){
            double minimun = 1000000;
            int x = 0, y = 0;
            for(int row = 0; row < distances.length; row++){
                for(int column = 0; column < row; column++){
                    if(distances[row][column] != -1 && distances[row][column] < minimun){
                        minimun = distances[row][column];
                        x = row;
                        y = column;                        
                    }
                }
            }
                     
            try{
                cluster Cluster1 = null;
                int index1 = -1, index2 = -1;
                cluster Cluster2 = null;
                for(int j = 0; j < Clusters.size(); j++){
                    if(Clusters.elementAt(j).isNameInside(names[x]) ){
                        Cluster1 = Clusters.elementAt(j);
                        index1 = j;                  
                    }else if(Clusters.elementAt(j).isNameInside(names[y])){
                        Cluster2 = Clusters.elementAt(j);
                        index2 = j;                   
                    }
                }
                if(Cluster1 != null && Cluster2 != null){
                    cluster Cluster = new cluster(Cluster1, Cluster2, minimun/2);
                    Clusters.setElementAt(Cluster, index1); 
                    Clusters.removeElementAt(index2);
                }else if(Cluster1 != null){
                    cluster Cluster = new cluster(names[y], Cluster1, minimun/2);
                    Clusters.setElementAt(Cluster, index1);                
                }else if(Cluster2 != null){
                    cluster Cluster = new cluster(names[x], Cluster2, minimun/2);
                    Clusters.setElementAt(Cluster, index2);     
                }else{
                    cluster Cluster = new cluster(names[x], names[y], minimun/2);
                    Clusters.addElement(Cluster);
                }
            
            }catch(Exception e){
                e.printStackTrace();                
            } 
            //arreglar matriz
            for(int j = 0; j < distances.length; j++){
                distances[x][j] = (distances[x][j] + distances[y][j])/2;
                distances[y][j] = -1;
                
                distances[j][x] = (distances[j][x] + distances[j][y])/2;
                distances[j][y] = -1;
            }

        }        
        
        result = Clusters.elementAt(0).print(result);
        System.out.println(result);
    }
    
    
    
    public class cluster{       
        String specie1 = "", specie2 = "";      
        cluster Cluster1, Cluster2;
        double distance;
        cluster(String specie1, String specie2, double distance){            
            this.specie1 = specie1;
            this.specie2 = specie2;
            this.distance = distance;
        }

        cluster(String specie, cluster Cluster1, double distance){
            this.distance = distance;
            this.specie1 = specie;
            this.Cluster1 = Cluster1;
        }  
        
        cluster(cluster Cluster1, cluster Cluster2, double distance){
            this.distance = distance;
            this.Cluster2 = Cluster2;
            this.Cluster1 = Cluster1; 
        }
        public boolean isNameInside(String name){
            try{
                if(specie1.matches(name) || specie2.matches(name) || Cluster1.isNameInside(name) || Cluster2.isNameInside(name)){
                    return true;
                }
                return false;
            }catch(Exception e){                
                return false;
            }
        }
        
        public String print(String dnd){
            
          /*  System.out.println(specie1);
            System.out.println(specie2);
            System.out.println(distance);
            System.out.println("------");*/
            if(!specie1.isEmpty()){
               dnd += "("+specie1+":"+distance+",";  
            }if(!specie2.isEmpty()){
                dnd += specie2+":"+distance+")";   
            }
            if(Cluster1 != null){
                dnd = "("+Cluster1.print(dnd)+":"+ distance+ ",";
            }
            if(Cluster2 != null){
                dnd = Cluster2.print(dnd)+":"+distance+")";
            }
            return dnd;
        }
    }

    
    
}
    
    
    
    
    
    
    
    
    

   /* private void UPGMA(double[][] distances){
        double[][] initMatrix = distances.clone();
        int[][] notation = new int[distances.length][distances[0].length * 2];
        for(int i = 0; i < notation.length; i++){
            for(int j = 0; j < notation[0].length; j++){
                if(j == 0){
                    notation[i][0] = i;
                }else{
                    notation[i][j] = -1;
                }
            }
        }
        double[] minimun = new double[initMatrix.length];
        
        
        for(int i = 0; i < initMatrix.length; i++){
            
            int minCont = 0, maxCont = 0, cont = 0;            
            minimun[i] = 1000000;
            int x = 0, y = 0;
            for(int row = 0; row < initMatrix.length; row++){
                for(int column = 0; column < row; column++){
                    if(initMatrix[row][column] < minimun[i]){
                        minimun[i] = initMatrix[row][column];
                        x = row;
                        y = column;
                    }
                }
            }
            
           if(minimun[i]!= 0){
                minimun[i] /= 2;
            }
            
            node Node = new node();
            Node.distance = minimun[i];
            if(x < y){
                Node.x = x;
                Node.y = y;
            }else{
                Node.x = y;
                Node.y = x;
            }
            nodes.addElement(Node);
            
            //cuenta el número de "índices" que hay en la fila 'x' de la matriz notacion[][]
            for(int col=0; notation[Node.x][col] != -1; col++){
                    minCont++;
            }
            //cuenta el número de "índices" que hay en la fila 'y' de la misma matriz
            for(int col=0; notation[Node.y][col] != -1; col++){
                    maxCont++;
            }
            cont = minCont + maxCont;
           // System.out.println(minCont + " - " + maxCont);
            //Copia los "índices" de la columna 'y' en la columna 'x' para que queden registrados los "índices" asociados (las secuencias)
            for(int col=0; notation[Node.y][col]!=-1; col++){
                    notation[Node.x][minCont++]= notation[Node.y][col];                   
            }
           
            //Asigna valores muy altos a los espacios que deberían estar vacíos en la matriz, de esta forma nunca serán seleccionados al buscar el valor más bajo

            for(int fil=0; fil < initMatrix.length; fil++){
                for(int col=0; col< initMatrix[0].length; col++){
                    if(fil == Node.y || col == Node.y){
                        distances[fil][col]= 10000;
                    }
                }
            }

            //UPGMA
		
            for(int fil = 0; fil < initMatrix.length; fil++){
                for(int col=0; col < fil; col++){
                    double result = 0;
                    int contador2 = 0;
                    int contador_fila=0;
                    if(col == x  && fil != col && distances[fil][col] != 10000 && distances[fil][col]!='\0'){
                        for(int e=0; notation[fil][e]!= -1;e++){
                                contador_fila++;
                        }
                        for(int fila=0; fila < contador_fila; fila++){
                            for(int colu = 0; colu < cont; colu++){
                                result = result + initMatrix[notation[fil][fila]][notation[x][colu]];							
                            }
                        }

                        contador2 = cont * contador_fila;
                        distances[fil][col]= '\0';
                        distances[fil][col]= result/contador2;
                        distances[col][fil]= '\0';
                        distances[col][fil]= result/contador2;					
                    }
                }
            }
		
        }
      //  System.out.println(distances.length);
        String result = paintNode(distances.length);
        System.out.println(result);
    }
   
    public String paintNode(int length){
        String tree = null;
        try{
            System.out.println(length);           
            String[] Names = dataset.getExperimentNames();
            node Node = nodes.elementAt(length - 1);        

            tree = "(";
            int index = Son(length - 1, Node.x, Node);
             System.out.println(index);
            if(index != -1){
                tree = paintNode(index);
            }else{
                tree += Names[Node.x];
            }        
            tree += ",";

            index = Son(length - 1, Node.y, Node);
            if(index != -1){
                tree = paintNode(index);
            }else{
                tree += Names[Node.y];
            }  
            tree += ":" + Node.distance;

            return tree;
        }catch(Exception e){
            e.printStackTrace();
            return tree;
        }
    }
    
    /*Función recursiva encargada de fabricar el archivo .dnd a partir de la estructura creada por la función anterior.

void pinta_nodo (int indice){
    struct nodo tmp;
    int h, i=0;
    char buffer[100];
    tmp=dict[indice];

    if (indice==0){
	    printf("(%s,%s:%f)",nombre[tmp.x],nombre[tmp.y],tmp.dist);
		sprintf(buffer,"(%s,%s:%f)",nombre[tmp.x],nombre[tmp.y],tmp.dist);
		fputs(buffer, pf);
	}else{
	    sprintf(buffer,"(");
	
    
    	h=hijo(indice,tmp.y);
		if (h!=-1){
			pinta_nodo(h);
		}else{
		   sprintf(buffer,"%s",nombre[tmp.y]);
		
		}
		sprintf(buffer,":%f)",tmp.dist);    	
    }
}

        public int Son(int index, int X, node Node){ 
            try{
                Node = nodes.elementAt(index);
                int i = index;
                while ((i >= 0) && (Node.x != X) && (Node.y != X)){
                    i--;
                    Node = nodes.elementAt(i);
                }
                if(Node.x == X || Node.y == X){
                    return i;
                }else{
                    return -1;
                }
            }catch(Exception exception){
                exception.printStackTrace();
                return -1;
            }
        }   
      

/*Función auxiliar que informa a la función pinta_nodo() de si se encuentra en una hoja o un nodo

int hijo (int indice,int valor)
{
    struct nodo tmp;
    int i=indice-1;
    tmp=dict[i];
    while ((i>=0)&&((tmp.x!=valor)&&(tmp.y!=valor))){
		i--;
		tmp=dict[i];
    }
   	if ((tmp.x==valor)||(tmp.y==valor)){
		return i;
    }else{
		return -1;
    }
}
class node{
    int x;
    int y;
    double distance;
}
*/
    
    
    
    
  
    
    

