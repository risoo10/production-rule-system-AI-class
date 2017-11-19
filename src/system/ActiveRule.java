package system;

import system.Actions.Action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class ActiveRule {

    public HashMap<String, String> variables = new LinkedHashMap<>();

    public Rule rule;

    public Boolean removed;

    public int conditions;

    public int conditionsOK;

    public List<Action> actions = new LinkedList<>();


    public ActiveRule(Rule rule){
        this.rule = rule;
        conditions = rule.conditions.size();
        conditionsOK = 0;
        removed = false;
    }

    public ActiveRule(ActiveRule rule){
        this.rule = rule.rule;
        this.conditionsOK = rule.conditionsOK;
        this.conditions = rule.conditions;
        this.removed = rule.removed;
        this.variables = (HashMap<String, String>) rule.variables.clone();
    }

}
