package system;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import system.Actions.DeleteFactAction;
import system.Actions.Action;
import system.Actions.AddFactAction;
import system.Actions.MessageAction;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlgorithmController {


    @FXML
    private TextArea factsArea;

    @FXML
    private TextArea rulesArea;

    @FXML
    private TextArea outputArea;



    List<String> facts = new LinkedList<>();

    List<Rule> rules = new LinkedList<>();

    ActionParser action = ActionParser.getInstance();

    Queue<ActiveRule> activeRuleQueue = new LinkedList<>();








    @FXML
    void startAlgorithm() {

        // Clear lists
        rules.clear();
        facts.clear();

        // Initialize facts and rules
        initFacts();
        initRules();


        // Produce active rules from factst and basic rules
        processRules(rules);


        // Process first active rule from queue and execute actions
        while(!activeRuleQueue.isEmpty()){

            // Poll active rule
            ActiveRule activeRule = activeRuleQueue.poll();

            // Execute every action
            for(Action ac : activeRule.actions){

                String message = ac.execute(facts);
                printMessage(message);

            }

            // Produce new active rules from newly added or removed facts
            // Correct rules are put in to the queue
            processRules(rules);

        }

        // Algorithm finished - display new facts
        factsArea.setText(null);
        for(String fact : facts){
            factsArea.appendText(fact+"\n");
        }



    }

    // Process Rules
    private void processRules(List<Rule> rules){

        // Start recursion with base rules
        List<ActiveRule> baseRules = new LinkedList<>();

        for(Rule rule : rules){
            baseRules.add(new ActiveRule(rule));
        }

        // Recursively find the correct active rules only in succeded state
        produceCorrectRules(baseRules);

    }

    // Recursively process every rule and map facts to conditions and check if they are met
    private void produceCorrectRules(List<ActiveRule> activeRules){

        // End when every rules and facts hae been considered and processed
        if(activeRules.isEmpty()){
            return;
        }

        // Create new list of active rules to be filled with new rules
        List<ActiveRule> newRules = new LinkedList<>();


        // Check every currently rule and
        for(ActiveRule oldRule : activeRules){

            for(String fact : facts ){

                ActiveRule rule = new ActiveRule(oldRule);

                // Get currently active rule
                String cond = rule.rule.conditions.get(rule.conditionsOK);

                // Check if special condition
                if(cond.matches("^<>.*")){

                    List<String> keys = getKeys(cond);
                    if(!rule.variables.get(keys.get(0)).equals(rule.variables.get(keys.get(1)))){
                        conditionOkey(rule, newRules);
                        break;
                    }


                }



                String match = cond.replaceAll("\\?\\w", "(.*)");

                // Check if fact matches the rule condition
                if(fact.matches(match)){

                    // Get Keys
                    List<String> keys = getKeys(cond);

                    // Get Values
                    List<String> values = getValues(cond, fact, keys.size());

                    // Check if exists keys and if store correct values
                    // Store new keys and values
                    for(int i=0; i<keys.size(); i++){

                        // If variable is not set already
                        if(!rule.variables.containsKey(keys.get(i))){
                            rule.variables.put(keys.get(i), values.get(i));
                        } else {

                            // Variable is aready set
                            // Check if the new one is the same as the set
                            if(!rule.variables.get(keys.get(i)).equals(values.get(i))){
                                rule.removed = true;
                                break;
                            }
                        }
                    }


                    if(!rule.removed){

                        conditionOkey(rule, newRules);

                    }
                }
            }
        }

        // Recursion
        produceCorrectRules(newRules);
    }


    // Check if ALL conditions are met and add action to queue for execution
    private void conditionOkey(ActiveRule rule, List<ActiveRule> newRules){
        rule.conditionsOK++;

        // Rule is finished and okey
        if(rule.conditionsOK == rule.conditions){

            // Process actions
            processActions(rule);


            // Are actions valid ?
            if(areActionsValid(rule)){
                activeRuleQueue.add(rule);
            }
        } else {
            newRules.add(rule);
        }

    }


    private Boolean areActionsValid(ActiveRule rule){

        Boolean state = false;

        // Chcek every actions
        for(Action action : rule.actions){

            // Check if this fact exist
            if(action.title.equals("ADD")){

                state = true;

                for(String fact : facts){
                    if(fact.equals(action.parameter)) {
                        state = false;
                        break;
                    }
                }

            } else if(action.title.equals("REMOVE")){

                state = false;

                // Check if fact to be deleted actually exist
                for(String fact : facts){
                    if(fact.matches(action.parameter)){
                        state = true;
                        break;
                    }

                }

            } else if(action.title.equals("MESSAGE")){
                state = true;
            }

            // Check the active rules in the queue if contains duplicate actions
            for(ActiveRule activeRule : activeRuleQueue){
                for(Action ac : activeRule.actions){
                    if(ac.title.equals(action.title) && ac.parameter.equals(action.parameter)){
                        state = false;
                        break;
                    }
                }
            }

            if(!state){
                return false;
            }

        }

        return true;
    }



    private void processActions(ActiveRule rule){

        // For every action fill variables with correct values
        for(String action : rule.rule.actions){

            String parameter = action;

            // For every key put real values inside
            for(String key : getKeys(action)){
                parameter = parameter.replaceAll("\\?"+key, rule.variables.get(key));
            }

            // Create correct action
            if(action.matches("^ADD .*")){
                rule.actions.add(new AddFactAction(parameter.replaceAll("^ADD ", ""), "ADD"));
            } else if(action.matches("^REMOVE .*")){
                rule.actions.add(new DeleteFactAction(parameter.replaceAll("^REMOVE ", ""), "REMOVE"));
            } else if(action.matches("^MESSAGE .*")){
                rule.actions.add(new MessageAction(parameter.replaceAll("^MESSAGE ", ""), "MESSAGE"));
            }

        }

    }


    private List<String> getKeys(String condition){

        // Get keys
        String keyPatrn = "\\?(\\w)";
        Pattern ptrn = Pattern.compile(keyPatrn);
        Matcher m = ptrn.matcher(condition);

        List<String> keys = new ArrayList<>();
        while (m.find()) {
            keys.add(m.group(0));
        }

        return keys;
    }


    private List<String> getValues(String condition, String fact, int count){

        // Get Values
        String valuesPtrn = condition.replaceAll("\\?(\\w)", "(.*)");
        Pattern ptrn = Pattern.compile(valuesPtrn);
        Matcher m = ptrn.matcher(fact);

        List<String> values = new ArrayList<>();
        if(m.find()){
            for(int i=1; i<=count; i++){
                values.add(m.group(i));
            }
        }

        return values;
    }


    private void initFacts(){

        String[] facts = factsArea.getText().split("\n");
        Collections.addAll(this.facts, facts);

    }

    private void initRules(){

        String ptrnTitle = "(\\w*):";
        String ptrnConditions = "IF (\\(.*\\))";
        String ptrnActions = "THEN (\\(.*\\))";
        Pattern ptrn;

        // Get
        for(String s : rulesArea.getText().split(";")){

            // Regex title
            ptrn = Pattern.compile(ptrnTitle);
            Matcher matcher = ptrn.matcher(s);

            String title = null;
            if(matcher.find()){
                title = matcher.group(1);
            }

            // Regex conditions as array
            ptrn = Pattern.compile(ptrnConditions);
            matcher = ptrn.matcher(s);
            String[] conditions = null;
            if(matcher.find()) {
                conditions = matcher.group(1).replaceAll("\\(|\\)", "").split("\\+");
            }

            // Regex actions as array
            ptrn = Pattern.compile(ptrnActions);
            matcher = ptrn.matcher(s);
            String[] actions = null;
            if(matcher.find()) {
                actions = matcher.group(1).replaceAll("\\(|\\)", "").split("\\+");
            }

            this.rules.add(new Rule(title, conditions, actions));

        }

    }


    private void printMessage(String message){
        if(message != null){
            outputArea.appendText(message + "\n");
        }
    }

}
