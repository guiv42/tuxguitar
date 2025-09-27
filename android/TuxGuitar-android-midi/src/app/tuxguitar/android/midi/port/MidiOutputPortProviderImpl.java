package app.tuxguitar.android.midi.port;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class MidiOutputPortProviderImpl implements MidiOutputPortProvider{

	private TGContext context;
	private List<MidiOutputPort> ports;

	public MidiOutputPortProviderImpl(TGContext context){
		this.context = context;
	}

	public List<MidiOutputPort> listPorts() {
		if( this.ports == null ) {
			this.ports = new ArrayList<MidiOutputPort>();
		} else {
			this.ports.clear();
		}

		if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ) {
			TGActivity activity = TGActivityController.getInstance(this.context).getActivity();
			if( activity != null && activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
				MidiManager midiManager = (MidiManager) activity.getSystemService(Context.MIDI_SERVICE);
				MidiDeviceInfo[] infos = midiManager.getDevices();
				for(MidiDeviceInfo info : infos) {
					if( info.getInputPortCount() > 0 ) {
						MidiDeviceInfo.PortInfo[] portInfos = info.getPorts();
						for(MidiDeviceInfo.PortInfo portInfo : portInfos) {
							if( portInfo.getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
								this.ports.add(new MidiOutputPortImpl(this.context, info, portInfo));
							}
						}
					}
				}
			}
		}

		return this.ports;
	}

	public void closeAll() throws MidiPlayerException{
		if( this.ports != null ) {
			for(int i = 0 ; i < this.ports.size() ; i++) {
				this.ports.get(i).close();
			}
			this.ports.clear();
		}
	}
}
