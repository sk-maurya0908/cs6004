import java.util.*;

import com.google.common.base.Objects;

import shelf.*;
//import fj.data.List;
//import fj.data.List;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.JastAddJ.IfStmt;
import soot.jimple.AssignStmt;
import soot.jimple.GotoStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.util.Chain;


public class AnalysisTransformer extends BodyTransformer {

    private AnalysisResult analysisResult;

    public AnalysisTransformer(AnalysisResult result) {
        this.analysisResult = result;
    }



    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {

        SootMethod sm = body.getMethod();
        MethodAnalysisResult result = new MethodAnalysisResult();

        boolean changed = true;
        while(changed){

            // Set to keep track of units containing assignments to dead variables
            Set<Unit> unitsToRemove = new HashSet<>();

            // Analyze parameters and detect unused parameters
            Chain<Local> cLocals = sm.getActiveBody().getLocals();
            List<Local> locals = new ArrayList<>();
            for (Local local : cLocals) {
                locals.add(local);
            }
            List<Local> parameters = sm.getActiveBody().getParameterLocals();
            //System.out.println(parameters);
            List<Local> usedParametersLocals = new ArrayList<>();
            for (Unit u : body.getUnits()) {
                for (ValueBox vb : u.getUseAndDefBoxes()) {
                    //System.out.println(vb);
                    Value v = vb.getValue();
                    if (v instanceof Local && (parameters.contains(v) || locals.contains(v))) {
                        usedParametersLocals.add((Local) v);
                    }
                }
            }
            parameters.removeAll(usedParametersLocals);
            locals.removeAll(usedParametersLocals);

            // Remove unused parameters from method signature
            for (Local unusedParam : parameters) {
                sm.getActiveBody().getLocals().remove(unusedParam);
            }


            // Construct a UnitGraph for the body
            UnitGraph unitGraph = new BriefUnitGraph(body);
            
            // Perform live variable analysis on the body
            SimpleLiveLocals liveLocals = new SimpleLiveLocals(unitGraph);

            // Iterate over each unit in the body
            for (Unit u : body.getUnits()) {

                // Get live variables BEFORE the current program point
                List<Local> liveBeforeU = liveLocals.getLiveLocalsBefore(u);
                // Print live variables before the current program point
                //System.out.println("\n\nLive variables before " + u + ": " + liveBeforeU);
                System.out.println(u);
                // Get live variables AFTER the current program point
                List<Local> liveAfterU = liveLocals.getLiveLocalsAfter(u);
                // Print live variables after the current program point
                //System.out.println("Live variables after " + u + ": " + liveAfterU);
                
                result.addLiveVariables(u, liveAfterU);
                //System.out.println("\n---");

                // Remove assignments to variables that are not live after the current program point
                if (u instanceof AssignStmt) {
                    AssignStmt assignStmt = (AssignStmt) u;
                    Value leftOp = assignStmt.getLeftOp();
                    if (leftOp instanceof Local && !liveAfterU.contains(leftOp)) {
                        // Remove the assignment statement
                        unitsToRemove.add(u);
                        result.addDeadCode(u);
                    }
                }
            }

            if(unitsToRemove == null || unitsToRemove.isEmpty()){
                changed = false;
                continue;
            }
            // Remove the units marked for removal
            // for (Unit unitToRemove : unitsToRemove) {
            //     body.getUnits().remove(unitToRemove);
            // }
        }

        analysisResult.addMethodResult(sm, result);
    }
}




 
