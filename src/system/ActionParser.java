package system;

import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class ActionParser {



    private static ActionParser instance;



    public String execute(List<String> facts, String actionTitle, String parameter){

        if(actionTitle.equals("pridaj")){

            facts.add(parameter);

        } else if(actionTitle.equals("vymaz")){

            for(String fact : facts){
                if(fact.equals(parameter)){
                    facts.remove(fact);
                }
            }

        } else if(actionTitle.equals("sprava")){
            return parameter;
        }

        return null;

    }

    public static ActionParser getInstance(){
        if(instance == null){
            instance = new ActionParser();
        }
        return instance;
    }


}
