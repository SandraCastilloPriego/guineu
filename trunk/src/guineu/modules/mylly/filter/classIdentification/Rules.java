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
package guineu.modules.mylly.filter.classIdentification;

import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.modules.mylly.gcgcaligner.datastruct.ComparablePair;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class Rules {

    String rules;
    List<MiniRule> separateRules = new ArrayList<MiniRule>();
    SimplePeakListRowGCGC row;
    List<ComparablePair<Integer, Integer>> spectrumRow;
    boolean result = false;

    public Rules(SimplePeakListRowGCGC row, List<ComparablePair<Integer, Integer>> spectrumRow, String rules) {
        this.rules = rules;
        this.row = row;
        this.spectrumRow = spectrumRow;
        MiniRule mRule = new MiniRule(rules);
        result = mRule.getResult();
      //  System.out.println("result: " + mRule.getResult());
    }

    public boolean getResult() {
        return result;
    }

   
    public MiniRule returnRule(String rule) {
        int contOpen = 0;
        int contClose = 0;
        int end = 0;
        int init = 0;

        MiniRule miniRule = null;

        for (Character c : rule.toCharArray()) {
            end++;
            if (c == '(') {
                contOpen++;
                if (contOpen == 1) {
                    init = end;
                }
            } else if (c == ')') {
                if (contOpen > 0) {
                    contClose++;
                }
            }
            if (contOpen > 1 && contOpen == contClose) {
                String newRule = rule.substring(init, end - 1);
                miniRule = new MiniRule(newRule);
                return miniRule;
            }

        }
        return null;

    }

    public class MiniRule {

        String ruleString;
        List<MiniRule> rules;
        Function function;
        int n;
        Operator operator;
        double condition;
        boolean result = false;
        List<Operator> supraOperator;

        public MiniRule(String ruleString) {

            this.ruleString = ruleString;
            rules = new ArrayList<MiniRule>();
            supraOperator = new ArrayList<Operator>();

          //  System.out.print("rule : "+ this.ruleString + " count: "+ this.countFunctions(ruleString));

            while (this.countFunctions(this.ruleString) > 1 || this.hasSupraRule(this.ruleString)) {
                // System.out.println("malo");
                // System.out.println("rule: " + this.ruleString);
                MiniRule rule = returnRule(this.ruleString);
                if (rule != null) {

                    rules.add(rule);
                    try {
                      //  System.out.println("antes rule: " + this.ruleString);
                      //    System.out.println("New rule: " + rule.ruleString);
                        //  System.out.println("New rule len: " + rule.ruleString.length());
                        this.ruleString = this.ruleString.substring(rule.ruleString.length()+2);
                      //   System.out.println("despues rule: " + this.ruleString);
                        this.getSupraRule();
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.ruleString = "";
                    }
                }
            }

            if (rules.isEmpty()) {
                this.parse(ruleString);
                this.evaluate();
            } else {
                this.supraEvaluate();
            }
        }

        public boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public int countFunctions(String s) {
            String string = new String(s);
            Pattern p = Pattern.compile("Ordinal|Intensity|Percent|Retention|Relative");
            Matcher m = p.matcher(string);
            int cont = 0;

            while(m.find()){
                cont++;
            }
            return cont;

        }

     

        private void getSupraRule() {
            try {
                String substring = ruleString.substring(0, ruleString.indexOf("("));
                if (substring.contains("+")) {
                    this.supraOperator.add(Operator.PLUS);
                } else if (substring.contains("&")) {
                    this.supraOperator.add(Operator.AND);
                } else if (substring.contains("|")) {
                    this.supraOperator.add(Operator.OR);
                }
            } catch (Exception e) {
            }
        }

        private boolean hasSupraRule(String s) {          
            if(s.contains("+")| s.contains("&")| s.contains("|")){
                return true;
            }
            return false;
        }

        private void parse(String ruleString) {
            try {
                if (ruleString.contains("Ordinal")) {
                    this.function = Function.ORDINAL;
                    String number = this.ruleString.substring(this.ruleString.indexOf("Ordinal") + 8, this.ruleString.indexOf(")"));
                    this.n = Integer.parseInt(number);
                } else if (ruleString.contains("Intensity")) {
                    this.function = Function.INTENSITY;
                    String number = this.ruleString.substring(this.ruleString.indexOf("Intensity") + 10, this.ruleString.indexOf(")"));
                    this.n = Integer.parseInt(number);
                } else if (ruleString.contains("Percent")) {
                    this.function = Function.PERCENT;
                    String number = this.ruleString.substring(this.ruleString.indexOf("Percent") + 8, this.ruleString.indexOf(")"));
                    this.n = Integer.parseInt(number);
                } else if (ruleString.contains("Relative")) {
                    this.function = Function.RELATIVE;
                    String number = this.ruleString.substring(this.ruleString.indexOf("Relative") + 9, this.ruleString.indexOf(")"));
                    this.n = Integer.parseInt(number);
                } else if (ruleString.contains("Retention")) {
                    this.function = Function.RETENTION;
                    String number = this.ruleString.substring(this.ruleString.indexOf("Retention") + 10, this.ruleString.indexOf(")"));
                    this.n = Integer.parseInt(number);
                }

                if (ruleString.contains(">=")) {
                    this.operator = Operator.MOREOREQUAL;
                    String number = this.ruleString.substring(this.ruleString.indexOf(">=") + 3);
                    this.condition = Integer.parseInt(number);
                } else if (ruleString.contains(">")) {
                    this.operator = Operator.MORE;
                    String number = this.ruleString.substring(this.ruleString.indexOf(">") + 2);
                    this.condition = Integer.parseInt(number);
                } else if (ruleString.contains("<=")) {
                    this.operator = Operator.LESSOREQUAL;
                    String number = this.ruleString.substring(this.ruleString.indexOf("<=") + 3);
                    this.condition = Integer.parseInt(number);
                } else if (ruleString.contains("<")) {
                    this.operator = Operator.LESS;
                    String number = this.ruleString.substring(this.ruleString.indexOf("<") + 2);
                    this.condition = Integer.parseInt(number);
                } else if (ruleString.contains("!=")) {
                    this.operator = Operator.DIFFERENT;
                    String number = this.ruleString.substring(this.ruleString.indexOf("!=") + 3);
                    this.condition = Integer.parseInt(number);
                }
            } catch (Exception e) {
            }
        }

        public void evaluate() {
            try {
                int value = -1;
                if (function == Function.ORDINAL) {
                    value = Ordinal(n);
                } else if (function == Function.INTENSITY) {
                    value = Intensity(n);
                } else if (function == Function.PERCENT) {
                    value = Percent(n);
                } else if (function == Function.RELATIVE) {
                    value = Relative(n);
                } else if (function == Function.RETENTION) {
                    value = (int) Retention(n);
                }

                if (this.operator == Operator.MOREOREQUAL) {
                    if (value >= condition) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else if (this.operator == Operator.MORE) {
                    if (value > condition) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else if (this.operator == Operator.LESSOREQUAL) {
                    System.out.println("less :" + function + " - "+ value);
                    if (value <= condition) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else if (this.operator == Operator.LESS) {
                    if (value < condition) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else if (this.operator == Operator.DIFFERENT) {
                    if (value != condition) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
            } catch (Exception e) {
            }
        }

        private void supraEvaluate() {
            try {
                int cont = 0;
                boolean result1 = this.rules.get(cont).getResult();
                 boolean result2 = this.rules.get(cont++).getResult();
                for (Operator o : this.supraOperator) {
                  //  System.out.println(result1 + " - "+ result2);
                  //  System.out.println(o);
                    if (o == Operator.AND || o == Operator.PLUS) {
                        if (result1 && result2) {
                            this.result = true;
                        } else {
                            this.result = false;
                        }
                    } else if (o == Operator.OR) {
                        if (result1 || result2) {
                            this.result = true;
                        } else {
                            this.result = false;
                        }
                    }
                  result1 = this.result;
                  result2 = this.rules.get(cont++).getResult();
                }
            } catch (Exception e) {
                this.result = true;
            }
        }
    }

    public double Retention(int dimension) {
        if (dimension == 1) {
            return row.getRT1();
        } else if (dimension == 2) {
            return row.getRT2();
        }
        return 0;
    }

    public int Intensity(int mass) {
        for (ComparablePair<Integer, Integer> spectrum : spectrumRow) {
            if (spectrum.getFirst() == mass) {
                return spectrum.getSecond();
            }
        }
        return 0;
    }

    public int Ordinal(int mass) {
        int ordinal = 1;
        for (ComparablePair<Integer, Integer> spectrum : spectrumRow) {            
            if (spectrum.getFirst() == mass) {
                //System.out.println("spectrum: "+spectrum.getFirst());
               // System.out.println("es: " +ordinal);
                return ordinal;
            }
            ordinal++;
        }
         //System.out.println("no es: " +ordinal);
        return ordinal;
    }

    public int Percent(int mass) {
        int totalIntensity = 1;
        int massIntensity = 1;
        for (ComparablePair<Integer, Integer> spectrum : spectrumRow) {
            if (spectrum.getFirst() == mass) {
                massIntensity += spectrum.getSecond();
            }
            totalIntensity += spectrum.getSecond();
        }
        return massIntensity / totalIntensity;
    }

    public int Relative(int mass) {
        int largestIntensity = spectrumRow.get(0).getSecond();
        for (ComparablePair<Integer, Integer> spectrum : spectrumRow) {
            if (spectrum.getFirst() == mass) {
                return spectrum.getSecond() / largestIntensity;
            }
        }
        return 0;
    }

    private enum Function {

        ORDINAL, RETENTION, RELATIVE, INTENSITY, USERULES, PERCENT;
    }

    private enum Operator {

        AND, OR, MORE, LESS, MOREOREQUAL, LESSOREQUAL, PLUS, NUMBER, DIFFERENT;
    }
}
