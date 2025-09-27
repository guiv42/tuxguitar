package app.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGString;

public class TGSongSegment {

	private List<TGMeasureHeader> headers;
	private List<TGTrackSegment> tracks;

	public TGSongSegment(){
		this.headers = new ArrayList<TGMeasureHeader>();
		this.tracks = new ArrayList<TGTrackSegment>();
	}

	public List<TGMeasureHeader> getHeaders() {
		return this.headers;
	}

	public List<TGTrackSegment> getTracks() {
		return this.tracks;
	}

	public void addTrack(int track, List<TGMeasure> measures, List<TGString> strings, boolean isPercussionTrack){
		List<Integer> trackStringsValues = new ArrayList<Integer>();
		for (TGString string : strings) {
			trackStringsValues.add(string.getValue());
		}
		this.tracks.add(new TGTrackSegment(track, measures, trackStringsValues, isPercussionTrack));
	}

	public boolean isEmpty(){
		return (this.headers.isEmpty() || this.tracks.isEmpty());
	}

	public TGSongSegment clone(TGFactory factory){
		TGSongSegment segment = new TGSongSegment();
		for(int i = 0;i < getHeaders().size();i++){
			TGMeasureHeader header = getHeaders().get(i);
			segment.getHeaders().add(header.clone(factory));
		}
		for(int i = 0;i < getTracks().size();i++){
			TGTrackSegment trackMeasure = getTracks().get(i);
			segment.getTracks().add((TGTrackSegment)trackMeasure.clone(factory,segment.getHeaders()));
		}
		return segment;
	}
}
