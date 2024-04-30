import java.util.*;

import shelf.*;
import soot.Local;
import soot.SootMethod;
import soot.Unit;

public class AnalysisResult {
    private Map<SootMethod, MethodAnalysisResult> methodResults;

    public AnalysisResult() {
        this.methodResults = new HashMap<>();
    }

    public void addMethodResult(SootMethod method, MethodAnalysisResult result) {
        methodResults.put(method, result);
    }

    public MethodAnalysisResult getMethodResult(SootMethod method) {
        return methodResults.get(method);
    }

    public Set<SootMethod> getAnalyzedMethods() {
        return methodResults.keySet();
    }
}

class MethodAnalysisResult {
    private Map<Unit, List<Local>> liveVariables;
    private Set<Unit> deadCode;

    public MethodAnalysisResult() {
        this.liveVariables = new HashMap<>();
        this.deadCode = new HashSet<>();
    }

    public void addLiveVariables(Unit unit, List<Local> liveVars) {
        liveVariables.put(unit, liveVars);
    }

    public void addDeadCode(Unit unit) {
        deadCode.add(unit);
    }

    public Map<Unit, List<Local>> getLiveVariables() {
        return liveVariables;
    }

    public Set<Unit> getDeadCode() {
        return deadCode;
    }
}
