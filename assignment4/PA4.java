import soot.Local;
import soot.PackManager;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.options.Options;

import java.util.*;

import shelf.*;

public class PA4 {
    public static void main(String[] args) {
        String classPath = "."; 	// change to appropriate path to the test class
        String dir = "./testcases";
        //Set up arguments for Soot
        String[] sootArgs = {
            "-cp", classPath, "-pp", // sets the class path for Soot
            "-keep-line-number", // preserves line numbers in input Java files  
            "-main-class", "Test",	// specify the main class
            "-process-dir", dir,   // list the classes to analyze
            "-p", "jop" ,"enabled:false"
        };  

        // Create an instance of AnalysisResult to hold the analysis results
        AnalysisResult result = new AnalysisResult();
        // Create transformer for analysis
        AnalysisTransformer analysisTransformer = new AnalysisTransformer(result);

        // Add transformer to appropriate pack in PackManager; PackManager will run all packs when soot.Main.main is called
        PackManager.v().getPack("jtp").add(new Transform("jtp.dfa", analysisTransformer));
        // Call Soot's main method with arguments
        soot.Main.main(sootArgs);

        //PackManager.v().getPack("jtp").apply();
        // Print analysis results
        printAnalysisResults(result);

    }

    private static void printAnalysisResults(AnalysisResult result) {
        for (SootMethod sm : result.getAnalyzedMethods()) {
            System.out.println("\n\n\n"+sm.getDeclaringClass()+" : "+sm.getName());
            
            MethodAnalysisResult res = result.getMethodResult(sm);
            System.out.println("Live Variables:");
            for (Map.Entry<Unit, List<Local>> entry : res.getLiveVariables().entrySet()) {
                System.out.println("At " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("\nDead Code:");
            for (Unit unit : res.getDeadCode()) {
                System.out.println(unit);
            }
        }
    }
}
