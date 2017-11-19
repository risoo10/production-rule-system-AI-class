package system;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class Rule {

    public String title;

    public List<String> conditions = new LinkedList<>();

    public List<String> actions = new LinkedList<>();


    public Rule(String title, String[] conditions, String[] actions){

        this.title = title;

        Collections.addAll(this.conditions, conditions);

        Collections.addAll(this.actions, actions);

    }


}
