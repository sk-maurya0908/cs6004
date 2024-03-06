import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import soot.*;
import soot.jimple.AnyNewExpr;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefUnitGraph;

class PointsToInformation {
    HashMap<String, HashSet<String>> stack;
    HashMap<String, HashSet<String>> heap;  
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
        //System.out.println("\n\nMETHOD:-"+body.getClass());
        
        
        // Initialize points-to sets for each unit
        for (Unit unit : units) {
            pointsToSets.put(unit, new PointsToInformation());
        }

        // Perform points-to analysis using worklist algorithm
        //System.out.println("HEADS"+cfg.getHeads());

        Queue<Unit> worklist = new LinkedList<>(cfg.getHeads());
        //Set<Unit> visited = new HashSet<>();
        List<String> esc = new ArrayList<>();


        while (!worklist.isEmpty()) {
            //System.out.println("START WORKLIST"+worklist);
            Unit unit = worklist.poll();
            //System.out.println(visited);
            // if (visited.contains(unit)) {
            //     continue;baseName
            // }
            //visited.add(unit);

            if(unit instanceof JReturnStmt){
                //System.out.println(((JReturnStmt)unit).getOp().toString());
                esc.add(((JReturnStmt)unit).getOp().toString());
            }
            if(unit instanceof JInvokeStmt){
                //System.out.println("AAAAAAAAAAAAAAAAA:"+((JInvokeStmt)unit).getInvokeExpr().getArgs().toString());
                InvokeExpr invoke = ((JInvokeStmt)unit).getInvokeExpr();
                esc.addAll(invoke.getArgs().stream().map(a -> a.toString()).collect(Collectors.toList()));
                if(invoke instanceof JVirtualInvokeExpr){
                    //System.out.println(((JVirtualInvokeExpr)invoke).getBase().toString());
                    esc.add(((JVirtualInvokeExpr)invoke).getBase().toString());
                }
            }
            if(unit instanceof JThrowStmt){
                esc.add(((JThrowStmt)unit).getOp().toString());
            }
            // Calculate points-to set for the current unit
            // Set<Value> inSet = new HashSet<>();
            PointsToInformation inSet = new PointsToInformation();
            // System.out.println("\nPRED:"+cfg.getPredsOf(unit));
            //System.out.println(unit +"---------"+ unit.getClass());

            // System.out.println("SUCC:"+cfg.getSuccsOf(unit)+"\n");
            for (Unit pred : cfg.getPredsOf(unit)) {
                inSet.stack.putAll(pointsToSets.get(pred).stack);
                inSet.heap.putAll(pointsToSets.get(pred).heap);
            }
            // System.out.println("INStack"+inSet.stack);
            // System.out.println("INHeap"+inSet.heap);
            boolean changed = dataFlowFunction(inSet.stack, inSet.heap, unit, pointsToSets.get(unit), esc);
            //System.out.println("Changes:"+changed);
            pointsToSets.get(unit).heap = inSet.heap;
            pointsToSets.get(unit).stack = inSet.stack;
            if(changed){
                worklist.addAll(cfg.getSuccsOf(unit));
            }
            //System.out.println("END WORKLIST"+worklist);
        }
        // System.out.println("TAILS"+cfg.getTails());
        for (Unit unit : cfg.getTails()) {
            fixedPoint.stack.putAll(pointsToSets.get(unit).stack);
            fixedPoint.heap.putAll(pointsToSets.get(unit).heap);
        }
        // System.out.println(fixedPoint.stack);
        // System.out.println(fixedPoint.heap);
        // System.out.println(esc);
        Set<Integer> ans = new HashSet<>();
        if(esc != null)
        for (String o : esc) {
            //System.out.println("escaping obj:"+o);
            HashSet<String> escObjs = fixedPoint.stack.get(o);
            if(escObjs != null){
                Queue<String> processingObj = new LinkedList<>(escObjs);
                while(!processingObj.isEmpty()){
                    String str = processingObj.poll();
                    HashSet<String> objHeap = fixedPoint.heap.get(str);
                    if(objHeap != null){
                        processingObj.addAll(objHeap);
                    }
                    try {
                        ans.add(Integer.parseInt(str));
                    } catch (Exception e) {
                        //System.out.println(e);//continue;// TODO: handle exception
                    }
                }
            }            
        }
        synchronized(this){
            if(ans != null){
                System.out.print(sm.getDeclaringClass()+":"+sm.getName()+" ");
                String out = ans.stream().map(Object::toString).collect(Collectors.joining(" "));
                System.out.println(out);
            }
        }
    }


    private boolean dataFlowFunction(HashMap<String, HashSet<String>> stack, HashMap<String, HashSet<String>> heap, Unit u, PointsToInformation cpti, List<String> escList) {

        if(u instanceof JIdentityStmt){ //parameters of method
            String par = ((JIdentityStmt)u).leftBox.getValue().toString();
            String parName = ((JIdentityStmt)u).rightBox.getValue().toString().split(":")[0];
            stack.put(par, new HashSet<String>());
            stack.get(par).add(parName);
            escList.add(par);
        }
        if (u instanceof JAssignStmt) {
            JAssignStmt stmt = (JAssignStmt) u;
            Value rhs = stmt.getRightOp();
            Value lhs = stmt.getLeftOp();
            if (rhs instanceof JNewExpr) { // v = new() -- rhs is JNewExpr
                String varName = ((JimpleLocal)lhs).getName();
                stack.putIfAbsent(varName, new HashSet<String>());
                stack.get(varName).add(new Integer(u.getJavaSourceStartLineNumber()).toString());
            }
            else if(rhs instanceof JimpleLocal){
                String rName = ((JimpleLocal)rhs).getName();
                if(lhs instanceof JimpleLocal){ // v = w  -- lhs and rhs are local
                    String lName = ((JimpleLocal)lhs).getName();
                    stack.put(lName, new HashSet<String>(stack.get(rName))); 
                }
                else if(lhs instanceof StaticFieldRef){ //global variables
                    String lName = ((StaticFieldRef)lhs).getFieldRef().name();
                    //System.out.println("GLOBAL______:"+rName+" "+lName+" "+stack.get(rName));
                    stack.putIfAbsent(lName, new HashSet<String>()); 
                    stack.get(lName).addAll(stack.get(rName));
                    escList.add(lName);
                }
                else if(lhs instanceof JInstanceFieldRef){ // v.f = w  -- lhs is JInstanceFieldRef
                    //System.out.println(((JInstanceFieldRef)lhs).getField().getName());
                    String baseName = ((JInstanceFieldRef)lhs).getBase().toString();
                    //String fieldName = ((JInstanceFieldRef)lhs).getField().getName();
                    // System.out.println(baseName + fieldName);
                    // System.out.println(stack.get(baseName));
                    // System.out.println(stack.get(rName));
                    HashSet<String> bases = stack.get(baseName);
                    if(bases != null && !bases.isEmpty()){
                        for (String i : bases) {                           
                            heap.putIfAbsent(i, new HashSet<String>());
                            heap.get(i).addAll(stack.get(rName));
                        }
                    }                    
                }
            }
            else if(rhs instanceof StaticFieldRef){ // v = global  -- rhs is global
                String rName = ((StaticFieldRef)rhs).getFieldRef().name();
                //if(lhs instanceof JimpleLocal){ 
                String lName = ((JimpleLocal)lhs).getName();
                //System.out.println("GLOBAL______:"+lName+" "+rName+" "+stack.get(rName));
                if(stack.get(rName) != null)
                    stack.putIfAbsent(lName, new HashSet<String>(stack.get(rName))); 
                else{
                    stack.putIfAbsent(lName, new HashSet<String>()); 
                    stack.get(lName).add(rName);
                }
                escList.add(lName);
                
            }
            else if(rhs instanceof JInstanceFieldRef){ // v = w.f -- rhs is JInstanceFieldRef
                String lName = ((JimpleLocal)lhs).getName();
                //System.out.println(lName);
                String baseName = ((JInstanceFieldRef)rhs).getBase().toString();
                //System.out.println(stack.get(baseName));
                //String fieldName = ((JInstanceFieldRef)rhs).getField().getName();
                //System.out.println(fieldName);
                //System.out.println(stack);
                //System.out.println(heap);
                stack.put(lName, new HashSet<String>());   
                HashSet<String> bases = stack.get(baseName);        
                if(bases != null && !bases.isEmpty()) {
                    for (String i : bases) {
                        //System.out.println(fieldName+i.toString());
                        HashSet<String> allRef = heap.get(i);
                        if(allRef != null && !allRef.isEmpty()){
                            stack.get(lName).addAll(allRef);
                        }
                            
                    }
                }
                //System.out.println(stack.get(lName));
            }
        }
        //  System.out.println("OUTstack"+stack);
        // // System.out.println(cpti.stack);
        //  System.out.println("OUTheap"+heap);
        // // System.out.println(cpti.heap);
        boolean st = compareHashMaps(stack, cpti.stack);
        boolean he = compareHashMaps(cpti.heap, heap);
        return !(st && he);
    }
    
    public static boolean compareHashMaps(Map<String, HashSet<String>> map1, Map<String, HashSet<String>> map2) {
        // Check if the size of the maps is equal
        if (map1.size() != map2.size()) {
            return false;
        }

        // Iterate over the keys of map1 and check if they exist in map2 and have the same values
        for (Map.Entry<String, HashSet<String>> entry : map1.entrySet()) {
            String key = entry.getKey();
            HashSet<String> set1 = entry.getValue();
            HashSet<String> set2 = map2.get(key);

            // Check if the key exists in map2 and the sets are equal
            if (set2 == null || !set2.equals(set1)) {
                return false;
            }
        }

        return true;
    }
}




 
