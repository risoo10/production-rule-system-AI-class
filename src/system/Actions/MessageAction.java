package system.Actions;

import java.util.List;

/**
 * Created by Riso on 5/13/2017.
 */
public class MessageAction extends Action {
    public MessageAction(String parameter, String title) {
        super(parameter, title);
    }

    @Override
    public String execute(List<String> facts) {
        return parameter;
    }
}
