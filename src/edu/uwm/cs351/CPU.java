package edu.uwm.cs351;

/**
 * The Class CPU.
 */
public class CPU {
	
	/** The previous and active processes in the CPU. */
	private Process active, previous;
	
	/** Instantiates a new CPU */
	public CPU(){}
	
	/** Checks if there is an active process in the CPU.
	 * @return true, if successful */
	public boolean hasProcess(){return active != null;}
	
	/** Gets the active process of the CPU.
	 * @return the active process */
	public Process getProcess(){return active;}
	
	/** Loads a new process into the CPU.
	 * @param p the process */
	public void load(Process p){
		if (previous != null)
			previous.updateProgressBar(false);
		active = p;
		active.updateProgressBar(true);}
	
	/** Unload the active process of the CPU.
	 * @return the process */
	public Process unload(){
		previous = active;
		active = null;
		return previous;}
	
	/** Performs one instruction of the active process. */
	public void step(){
		active.performInstruction();
		active.updateProgressBar(true);}
}
