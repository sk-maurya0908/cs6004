import java.util.*;
import java.util.stream.Collectors;

import soot.*;
import soot.jimple.AnyNewExpr;
import soot.jimple.AssignStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefUnitGraph;

class PointsToInformation {
    HashMap<String, HashSet<Integer>> stack;
    HashMap<String, HashSet<Integer>> heap;  
    PointsToInformation(){
        stack = new HashMap<>();
        heap = new HashMap<>();
    }
    PointsToInformation(PointsToInformation p){
        this.stack.putAll(p.stack);
        this.heap.putAll(p.heap);
    }  
}

public class AnalysisTransformer extends BodyTransformer {
    
    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {

        // Construct CFG for the current method's body
        PatchingChain<Unit> units = body.getUnits();
        Map<Unit, PointsToInformation> pointsToSets = new HashMap<>();
        BriefUnitGraph cfg = new BriefUnitGraph(body);
        //Queue<Unit> workList = new LinkedList<Unit>();
        PointsToInformation fixedPoint = new PointsToInformation();
        SootMethod sm = body.getMethod();
        System.out.println("\n\n\n\n\n\n\nMETHOD:-"+sm.getName());
        // Initialize points-to sets for each unit
        for (Unit unit : cfg.getHeads()) {
            pointsToSets.put(unit, new PointsToInformation());
        }

        // Perform points-to analysis using worklist algorithm
        Queue<Unit> worklist = new LinkedList<>(cfg.getHeads());
        Set<Unit> visited = new HashSet<>();
        
        while (!worklist.isEmpty()) {
            Unit unit = worklist.poll();
            if (visited.contains(unit)) {
                continue;
            }
            visited.add(unit);

            // Calculate points-to set for the current unit
            // Set<Value> inSet = new HashSet<>();
            PointsToInformation inSet = new PointsToInformation();
            for (Unit pred : cfg.getPredsOf(unit)) {
                inSet.stack.putAll(pointsToSets.get(pred).stack);
                inSet.heap.putAll(pointsToSets.get(pred).heap);
            }

            boolean changed = dataFlowFunction(inSet.stack, inSet.heap, unit);
            if(changed){
                pointsToSets.get(unit).heap = inSet.heap;
                pointsToSets.get(unit).stack = inSet.stack;

                worklist.addAll(cfg.getSuccsOf(unit));
            }
        }
        // int nUnits = units.size();
        // SootMethod sm = body.getMethod();
        // System.out.println("\n\n\n\n\n\n\nMETHOD:-"+sm.getName());
        // HashMap<String, HashSet<Integer>> stack = new HashMap<>();
        // HashMap<String, HashSet<Integer>> heap = new HashMap<>();

        // boolean changed;
        // Iterate over each unit of CFG.
        // Shown how to get the line numbers if the unit is a "new" Statement.
        // for (Unit u : units) {
        //     System.out.println("\nPRED:"+cfg.getPredsOf(u));
        //     System.out.println(u);
        //     System.out.println("SUCC:"+cfg.getSuccsOf(u)+"\n");

        //     // HashMap<String, HashSet<Integer>> stack = new HashMap<String, HashSet<Integer>>();
        //     // HashMap<String, HashSet<Integer>> heap = new HashMap<String, HashSet<Integer>>();
        //     changed = dataFlowFunction(stack, heap, u);
        //     if(changed){

        //     }
        // }
        for (Unit unit : cfg.getTails()) {
            fixedPoint.stack.putAll(pointsToSets.get(unit).stack);
            fixedPoint.heap.putAll(pointsToSets.get(unit).heap);
        }
        System.out.println(fixedPoint.stack);
        System.out.println(fixedPoint.heap);
    }

    private boolean dataFlowFunction(HashMap<String, HashSet<Integer>> parStack, HashMap<String, HashSet<Integer>> parHeap, Unit u) {
        //System.out.println("JAVALine#"+u.getJavaSourceStartLineNumber());
        System.out.println(u.getClass() +"-----"+ u.toString());
        HashMap<String, HashSet<Integer>> stack = new HashMap<>(parStack);
        HashMap<String, HashSet<Integer>> heap = new HashMap<>(parHeap);
        if(u instanceof JIdentityStmt){ //parameters of method
            //System.out.println(((JIdentityStmt)u).leftBox.getValue());
            String parName = ((JIdentityStmt)u).leftBox.getValue().toString();
            stack.put(parName, new HashSet<Integer>());
        }
        if (u instanceof JAssignStmt) {
            JAssignStmt stmt = (JAssignStmt) u;
            Value rhs = stmt.getRightOp();
            Value lhs = stmt.getLeftOp();
            System.out.println(stmt.getLeftOp().getClass()+" ====== "+stmt.getRightOp().getClass());
            if (rhs instanceof JNewExpr) { // v = new() -- rhs is JNewExpr
                //(lhs instanceof JimpleLocal || lhs instanceof StaticFieldRef){ if//local and global variables
                String varName = ((JimpleLocal)lhs).getName();
                //System.out.println(varName);
                stack.putIfAbsent(varName, new HashSet<Integer>());
                stack.get(varName).add(u.getJavaSourceStartLineNumber());
            }
            else if(rhs instanceof JimpleLocal){
                String rName = ((JimpleLocal)rhs).getName();
                if(lhs instanceof JimpleLocal){ // v = w  -- lhs and rhs are local
                    String lName = ((JimpleLocal)lhs).getName();
                    stack.put(lName, new HashSet<Integer>(stack.get(rName))); 
                }
                else if(lhs instanceof StaticFieldRef){ //global variables
                    String lName = ((StaticFieldRef)lhs).getFieldRef().name();
                    //System.out.println(rName+lName+stack.get(rName));
                    heap.putIfAbsent(lName, new HashSet<Integer>(stack.get(rName))); 
                }
                else{ // v.f = w  -- lhs is JInstanceFieldRef
                    //System.out.println(((JInstanceFieldRef)lhs).getField().getName());
                    String baseName = ((JInstanceFieldRef)lhs).getBase().toString();
                    String fieldName = ((JInstanceFieldRef)lhs).getField().getName();
                    // System.out.println(baseName + fieldName);
                    // System.out.println(stack.get(baseName));
                    // System.out.println(stack.get(rName));
                    for (Integer i : stack.get(baseName)) {                            
                        heap.putIfAbsent(fieldName + i.toString(), new HashSet<Integer>(stack.get(rName)));
                    }
                }
            }
            else if(rhs instanceof StaticFieldRef){ // v = global  -- rhs is global
                String rName = ((StaticFieldRef)rhs).getFieldRef().name();
                //if(lhs instanceof JimpleLocal){ 
                String lName = ((JimpleLocal)lhs).getName();
                if(stack.get(rName) != null)
                    stack.putIfAbsent(lName, new HashSet<Integer>(stack.get(rName))); 
                else{
                    stack.putIfAbsent(lName, new HashSet<Integer>()); 
                }
                
            }
            else if(rhs instanceof JInstanceFieldRef){ // v = w.f -- rhs is JInstanceFieldRef
                String lName = ((JimpleLocal)lhs).getName();
                //System.out.println(lName);
                String baseName = ((JInstanceFieldRef)rhs).getBase().toString();
                //System.out.println(stack.get(baseName));
                String fieldName = ((JInstanceFieldRef)rhs).getField().getName();
                //System.out.println(fieldName);
                //System.out.println(stack);
                //System.out.println(heap);
                stack.put(lName, new HashSet<Integer>());   
                stack.get(baseName);         
                for (Integer i : stack.get(baseName)) {
                    //System.out.println(fieldName+i.toString());
                    stack.get(lName).addAll(heap.get(fieldName + i.toString()));
                }
                //System.out.println(stack.get(lName));
            }
        }
           
        return compareHashMaps(stack, parStack) && compareHashMaps(parHeap, heap);
    }
    
    public static boolean compareHashMaps(Map<String, HashSet<Integer>> map1, Map<String, HashSet<Integer>> map2) {
        // Check if the size of the maps is equal
        if (map1.size() != map2.size()) {
            return false;
        }

        // Iterate over the keys of map1 and check if they exist in map2 and have the same values
        for (Map.Entry<String, HashSet<Integer>> entry : map1.entrySet()) {
            String key = entry.getKey();
            HashSet<Integer> set1 = entry.getValue();
            HashSet<Integer> set2 = map2.get(key);

            // Check if the key exists in map2 and the sets are equal
            if (set2 == null || !set2.equals(set1)) {
                return false;
            }
        }

        return true;
    }
}




 
