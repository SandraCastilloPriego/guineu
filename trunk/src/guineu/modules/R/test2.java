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

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RConsoleOutputStream;

public class test2 {

    public test2() {
        String[] args = new String[3];
        Rengine re = new Rengine(args, true, new TextConsole2());
        System.out.println("Rengine created, waiting for R");
        if (!re.waitForR()) {
            System.out.println("Cannot load R");

        }
        System.out.println("re-routing stdout/err into R console");
        System.setOut(new PrintStream(new RConsoleOutputStream(re, 0)));
        System.setErr(new PrintStream(new RConsoleOutputStream(re, 1)));

        System.out.println("Letting go; use main loop from now on");
    }
}

class TextConsole2 implements RMainLoopCallbacks {

    JInternalFrame f;
    public JTextArea textarea = new JTextArea();

    public TextConsole2() {
        f = new JInternalFrame("hol",true,true,true,true);
        f.getContentPane().add(new JScrollPane(textarea));
        f.setSize(new Dimension(800, 600));
        f.show();
        GuineuCore.getDesktop().addInternalFrame(f);
    }

    public void rWriteConsole(Rengine re, String text, int oType) {
        textarea.append(text);
    }

    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy(" + which + ")");
    }

    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
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
        System.out.println("rShowMessage \"" + message + "\"");
    }

    public String rChooseFile(Rengine re, int newFile) {
        return"hola";
    }

    public void rFlushConsole(Rengine re) {
    }

    public void rLoadHistory(Rengine re, String filename) {
    }

    public void rSaveHistory(Rengine re, String filename) {
    }
}