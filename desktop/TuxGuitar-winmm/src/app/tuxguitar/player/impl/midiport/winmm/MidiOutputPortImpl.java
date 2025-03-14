package app.tuxguitar.player.impl.midiport.winmm;

import app.tuxguitar.gm.port.GMOutputPort;
import app.tuxguitar.gm.port.GMReceiver;

public class MidiOutputPortImpl extends GMOutputPort{

	private int device;
	private String name;
	private MidiReceiverImpl receiver;

	public MidiOutputPortImpl(MidiSystem midiSystem,String name,int device){
		this.name = name;
		this.device = device;
		this.receiver = new MidiReceiverImpl(this,midiSystem);
	}

	public void open(){
		if(!this.receiver.isConnected()){
			this.receiver.connect();
		}
	}

	public void close(){
		this.receiver.disconnect();
	}

	public GMReceiver getReceiver(){
		this.open();
		return this.receiver;
	}

	public void check(){
		// Not implemented
	}

	public int getDevice() {
		return this.device;
	}

	public static String toString(int device){
		return (Integer.toString(device));
	}

	public String getKey() {
		return (Integer.toString(this.device));
	}

	public String getName() {
		return this.name;
	}
}