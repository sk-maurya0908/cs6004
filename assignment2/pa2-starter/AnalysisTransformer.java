import java.util.*;
import soot.*;
import soot.jimple.AnyNewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class AnalysisTransformer extends BodyTransformer {
    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        // Construct CFG for the current method's body
        PatchingChain<Unit> units = body.getUnits();
        System.out.println("\n\nMETHOD:-"+body.getMethod());
        HashMap<String, HashSet<Integer>> stack = new HashMap<String, HashSet<Integer>>();
        HashMap<String, HashSet<Integer>> heap = new HashMap<String, HashSet<Integer>>();
        // Iterate over each unit of CFG.
        // Shown how to get the line numbers if the unit is a "new" Statement.
        for (Unit u : units) {
            System.out.println("JAVALine#"+u.getJavaSourceStartLineNumber());
            System.out.println(u.getClass() +"-----"+ u.toString());
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
                    //}
                    // try {
                    //     System.out.println("Unit is " + u + " and the line number is : " + u.getJavaSourceStartLineNumber());
                    // } catch (Exception e) {
                    //     System.out.println("Unit is " + u + " and the line number is : " + -1);
                    // }
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
            // if (u instanceof JAssignStmt) {
            //     JAssignStmt assignStmt = (JAssignStmt) u;
            //     // Check if the left-hand side is a variable
            //     if (assignStmt.getLeftOp() instanceof Local) {
            //         // Check if the right-hand side is an object creation expression
            //         if (assignStmt.getRightOp() instanceof JNewExpr) {
            //             JNewExpr newExpr = (JNewExpr) assignStmt.getRightOp();
            //             // Analyze if the object might escape
            //             if (Scene.v().getCallGraph().mayEscapeMethod(newExpr)) {
            //                 System.out.println("Object creation at line " + u.getJavaSourceStartLineNumber()+ " might escape method " + body.getMethod().getName());
            //             }
            //         }
            //     }
            // }
        }
        System.out.println(stack);
        System.out.println(heap);


    }
}
