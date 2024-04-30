import java.util.*;

import shelf.*;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.JastAddJ.IfStmt;
import soot.jimple.AssignStmt;
import soot.jimple.GotoStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.util.Chain;


public class AnalysisTransformer1 extends SceneTransformer {

    private AnalysisResult analysisResult;

    public AnalysisTransformer1(AnalysisResult result) {
        this.analysisResult = result;
    }



    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {

        // Get the main method
        SootMethod mainMethod = Scene.v().getMainMethod();

        // If main method is null, return
        if (mainMethod == null) {
            System.err.println("No main method found.");
            return;
        }

        // Construct the call graph
        CallGraph callGraph = Scene.v().getCallGraph();

        // Mark reachable methods starting from the main method
        Set<SootMethod> reachableMethods = new HashSet<>();
        Queue<SootMethod> worklist = new LinkedList<>();
        worklist.add(mainMethod);
        reachableMethods.add(mainMethod);
        System.out.println(reachableMethods);
        while (!worklist.isEmpty()) {
            SootMethod method = worklist.poll();
            Iterator<Edge> edges = callGraph.edgesOutOf(method);
            while (edges.hasNext()) {
                Edge edge = edges.next();
                SootMethod targetMethod = edge.tgt();
                System.out.println(targetMethod);
                if (!reachableMethods.contains(targetMethod)) {
                    reachableMethods.add(targetMethod);
                    worklist.add(targetMethod);
                }
            }
        }

        // Create a list to hold methods to be removed
        List<SootMethod> methodsToRemove = new ArrayList<>();

        // Iterate over all classes and mark unreachable methods for removal
        for (SootClass sootClass : Scene.v().getClasses()) {
            if (!sootClass.isApplicationClass()) {
                continue; // Skip library classes
            }
            for (SootMethod method : sootClass.getMethods()) {
                if (!reachableMethods.contains(method)) {
                    System.out.println("Marking method for removal: " + method);
                    methodsToRemove.add(method);
                }
            }
        }

        // Remove the unreachable methods
        for (SootMethod method : methodsToRemove) {
            method.getDeclaringClass().removeMethod(method);
            System.out.println("Removed unreachable method: " + method);
        }

        // SootMethod sm = body.getMethod();
        // MethodAnalysisResult result = new MethodAnalysisResult();

        // boolean changed = true;
        // while(changed){

        //     // Set to keep track of units containing assignments to dead variables
        //     Set<Unit> unitsToRemove = new HashSet<>();

        //     // Analyze parameters and detect unused parameters
        //     Chain<Local> cLocals = sm.getActiveBody().getLocals();
        //     List<Local> locals = new ArrayList<>();
        //     for (Local local : cLocals) {
        //         locals.add(local);
        //     }
        //     List<Local> parameters = sm.getActiveBody().getParameterLocals();
        //     List<Local> usedParametersLocals = new ArrayList<>();
        //     for (Unit u : body.getUnits()) {
        //         for (ValueBox vb : u.getUseAndDefBoxes()) {
        //             Value v = vb.getValue();
        //             if (v instanceof Local && (parameters.contains(v) || locals.contains(v))) {
        //                 usedParametersLocals.add((Local) v);
        //             }
        //         }
        //     }
        //     parameters.removeAll(usedParametersLocals);
        //     locals.removeAll(usedParametersLocals);

        //     // Remove unused parameters from method signature
        //     for (Local unusedParam : parameters) {
        //         sm.getActiveBody().getLocals().remove(unusedParam);
        //     }


        //     // Construct a UnitGraph for the body
        //     UnitGraph unitGraph = new BriefUnitGraph(body);
            
        //     // Perform live variable analysis on the body
        //     SimpleLiveLocals liveLocals = new SimpleLiveLocals(unitGraph);

        //     // Iterate over each unit in the body
        //     for (Unit u : body.getUnits()) {

        //         // Get live variables BEFORE the current program point
        //         List<Local> liveBeforeU = liveLocals.getLiveLocalsBefore(u);
        //         // Print live variables before the current program point
        //         System.out.println("Live variables before " + u + ": " + liveBeforeU);

        //         // Get live variables AFTER the current program point
        //         List<Local> liveAfterU = liveLocals.getLiveLocalsAfter(u);
        //         // Print live variables after the current program point
        //         System.out.println("Live variables after " + u + ": " + liveAfterU);
                
        //         result.addLiveVariables(u, liveAfterU);
        //         //System.out.println("\n---");

        //         // Remove assignments to variables that are not live after the current program point
        //         if (u instanceof AssignStmt) {
        //             AssignStmt assignStmt = (AssignStmt) u;
        //             Value leftOp = assignStmt.getLeftOp();
        //             if (leftOp instanceof Local && !liveAfterU.contains(leftOp)) {
        //                 // Remove the assignment statement
        //                 unitsToRemove.add(u);
        //                 result.addDeadCode(u);
        //             }
        //         }
        //     }

        //     if(unitsToRemove == null || unitsToRemove.isEmpty()){
        //         changed = false;
        //         continue;
        //     }
        //     // Remove the units marked for removal
        //     // for (Unit unitToRemove : unitsToRemove) {
        //     //     body.getUnits().remove(unitToRemove);
        //     // }
        // }

        //analysisResult.addMethodResult(sm, result);
    }
}




 
