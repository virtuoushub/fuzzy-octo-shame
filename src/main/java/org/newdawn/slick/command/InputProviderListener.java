package org.newdawn.slick.command;

/**
 * Description of any class wishing to receive notifications of command invocations. Implementations
 * should be added to an appropriate input provider to receive input notification
 * 
 * @author joverton
 */
interface InputProviderListener {

    /**
     * A control representing an control was pressed relating to a given command.
     * 
     * @param command The command that the control related to
     */ 
    public void controlPressed(Command command);

    /**
     * A control representing an control was released relating to a given command.
     * 
     * @param command The command that the control related to
     */ 
    public void controlReleased(Command command);
}
