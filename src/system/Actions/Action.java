package system.Actions;

import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public abstract class Action {

    public String title ;

    public String parameter;

    public abstract String execute(List<String> facts);

    public Action(String parameter, String title){
        this.parameter = parameter;
        this.title = title;
    }




}
