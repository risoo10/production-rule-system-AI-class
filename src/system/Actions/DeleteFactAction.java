package system.Actions;

import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class DeleteFactAction extends Action {


    public DeleteFactAction(String parameter, String title) {
        super(parameter, title);
    }

    @Override
    public String execute(List<String> facts) {
        String deletedFact = null;
        for(String fact : facts){
            if(fact.equals(parameter)){
                deletedFact = fact;
                break;
            }
        }

        facts.remove(deletedFact);

        return null;
    }
}
