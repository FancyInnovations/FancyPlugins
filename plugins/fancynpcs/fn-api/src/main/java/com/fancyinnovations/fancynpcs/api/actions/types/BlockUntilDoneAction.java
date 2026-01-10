package com.fancyinnovations.fancynpcs.api.actions.types;

import com.fancyinnovations.fancynpcs.api.actions.NpcAction;
import com.fancyinnovations.fancynpcs.api.actions.executor.ActionExecutionContext;

/**
 * The BlockUntilDoneAction class is a specific implementation of the
 * NpcAction class that represents an action requiring the NPC (Non-Player
 * Character) to block its subsequent actions until the current interaction is
 * completed.
 * <p>
 */
public class BlockUntilDoneAction extends NpcAction {

    public BlockUntilDoneAction() {
        super("block_until_done", false);
    }

    @Override
    public void execute(ActionExecutionContext context, String value) {

    }
}
