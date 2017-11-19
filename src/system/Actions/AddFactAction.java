package system.Actions;

import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class AddFactAction extends Action {


    public AddFactAction(String parameter, String title) {
        super(parameter, title);
    }

    @Override
    public String execute(List<String> facts) {
        facts.add(parameter);
        return null;
    }
}
