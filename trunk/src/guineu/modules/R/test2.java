/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.R;

/**
 *
 * @author bicha
 */
import guineu.main.GuineuCore;
import java.io.*;

import java.awt.*;
import javax.swing.*;
import org.rosuda.JRI.RConsoleOutputStream;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

public class test2 implements Runnable{

	public test2() {
	}

	public void run() {
		String[] args = new String[1];
		args[0] = "--no-save";
		System.out.println("hoooola");
		try {
			Rengine re = new Rengine();

			System.out.println("hoooola2");
			System.out.println("Rengine created, waiting for R");

			if (!re.waitForR()) {
				System.out.println("Cannot load R");
				return;
			}
			re.eval("library(lattice)");
			REXP y = re.idleEval("print(1:10)");
			y = re.eval("feq <- read.delim('c:/minist.txt',header=T)");
			y = re.eval("feq1<-as.matrix(feq)");
			System.out.println(y = re.eval("feq1[1,]"));
			y = re.eval("jpeg()");
			y = re.eval("levelplot(feq1)");
			re.eval("dev.off()");
			re.end();
			System.out.println("end");
			REXP x;
			re.eval("data(iris)", false);
			x = re.eval("iris");
			RVector v = x.asVector();
			System.out.println("re-routing stdout/err into R console");
			System.setOut(new PrintStream(new RConsoleOutputStream(re, 0)));
			System.setErr(new PrintStream(new RConsoleOutputStream(re, 1)));

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}

/*class TextConsole2 implements RMainLoopCallbacks {

	JInternalFrame f;
	public JTextArea textarea = new JTextArea();

	public TextConsole2() {
		System.out.println("2222");
		f = new JInternalFrame("hol", true, true, true, true);
		System.out.println("2223");
		f.getContentPane().add(new JScrollPane(textarea));
		System.out.println("2224");
		f.setSize(new Dimension(800, 600));
		System.out.println("2225");
		GuineuCore.getDesktop().addInternalFrame(f);
		System.out.println("2226");
	}

	public void  rWriteConsole(Rengine re, String text, int oType) {
		textarea.append(text);
			System.out.println("2227");
	}

	public void rBusy(Rengine re, int which) {
		System.out.println("rBusy(" + which + ")");
			System.out.println("2228");
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
			System.out.println("2229");
		System.out.print(prompt);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();
			return (s == null || s.length() == 0) ? s : s + "\n";
		} catch (Exception e) {
			System.out.println("jriReadConsole exception: " + e.getMessage());
		}
		return null;
	}

	public void rShowMessage(Rengine re, String message) {
			System.out.println("2210");
		System.out.println("rShowMessage \"" + message + "\"");
	}

	public String rChooseFile(Rengine re, int newFile) {
		System.out.println("2211");
		return "";
	}

	public void rFlushConsole(Rengine re) {
		System.out.println("2212");
	}

	public void rLoadHistory(Rengine re, String filename) {
		System.out.println("2213");
	}

	public void rSaveHistory(Rengine re, String filename) {
		System.out.println("2214");
	}
}*/