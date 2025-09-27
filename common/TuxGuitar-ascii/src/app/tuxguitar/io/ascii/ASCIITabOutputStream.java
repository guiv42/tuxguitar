package app.tuxguitar.io.ascii;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

public class ASCIITabOutputStream {

	private static final String[] TONIC_NAMES = new String[]{"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

	private static final int MAX_LINE_LENGTH = 80;

	private TGSongManager manager;
	private PrintStream stream;
	private ASCIIOutputStream out;

	public ASCIITabOutputStream(PrintStream stream){
		this.stream = stream;
	}

	public ASCIITabOutputStream(OutputStream stream){
		this(new PrintStream(stream));
	}

	public ASCIITabOutputStream(String fileName) throws FileNotFoundException{
		this(new FileOutputStream(fileName));
	}

	public void writeSong(TGSong song){
		this.manager = new TGSongManager();

		this.out = new ASCIIOutputStream(this.stream);
		this.drawSong(song);
		this.out.flush();
		this.out.close();
	}

	private void drawSong(TGSong song){
		//Propiedades de cancion
		this.out.drawStringLine("Title: " + song.getName());
		this.out.drawStringLine("Artist: " + song.getArtist());
		this.out.drawStringLine("Album: " + song.getAlbum());
		this.out.drawStringLine("Author: " + song.getAuthor());

		Iterator<TGTrack> it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = it.next();
			this.out.nextLine();
			drawTrack(track);
			this.out.nextLine();
		}
	}

	private void drawTrack(TGTrack track){
		//Propiedades de pista
		this.out.nextLine();
		this.out.drawStringLine("Track " + track.getNumber() + ": " + track.getName());

		//Obtengo los nombres de la afinacion, y el ancho maximo que ocupa
		String[] tuning = new String[track.getStrings().size()];
		int maxTuningLength = 1;
		for(int i = 0; i < track.getStrings().size();i++){
			TGString string = track.getStrings().get(i);
			tuning[i] = TONIC_NAMES[(string.getValue() % TONIC_NAMES.length)];
			maxTuningLength = Math.max(maxTuningLength,tuning[i].length());
		}

		int nextMeasure = 0;
		boolean eof = false;
		while(!eof){
			this.out.nextLine();
			int index = nextMeasure;
			int measureCount = track.countMeasures();

			// draw chord names
			this.out.drawTuneSegment("",maxTuningLength);
			for(int j = index; j < measureCount; j++){
				TGMeasure measure = track.getMeasure(j);

				// drawMeasure
				this.out.drawSpace(); // one space for bar segment
				this.out.drawSpace(); // one space for one string segment
				TGBeat beat = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
				while(beat != null){
					TGBeat nextBeat = this.manager.getMeasureManager().getNextBeat( measure.getBeats() , beat);
					int maxOutLength=getMaxOutLength(beat, nextBeat, track.getStrings().size());
					int outLength = this.out.drawChord(beat.getChord());

					long length = (nextBeat != null ? nextBeat.getStart() - beat.getStart() : (measure.getStart() + measure.getLength()) - beat.getStart());
					int beatWidth=getDurationScaping(length);
					if (beatWidth<=maxOutLength) {
						beatWidth=maxOutLength+1;
					}
					this.out.drawSpace(beatWidth - outLength);
					beat = nextBeat;
				}

				//Si se supero el ancho maximo, bajo de linea
				if(this.out.getPosX() > MAX_LINE_LENGTH){
					break;
				}
			}
			this.out.nextLine();

			// draw strings
			for(int currentString = 0; currentString < track.getStrings().size();currentString++){
				//Dibujo la afinacion de la cuerda
				this.out.drawTuneSegment(tuning[currentString],maxTuningLength);

				for(int j = index; j < measureCount; j++){
					TGMeasure measure = track.getMeasure(j);
					drawMeasure(measure,currentString, track.getStrings().size());
					nextMeasure = (j + 1);

					//Calculo si era el ultimo compas
					eof = (this.manager.getTrackManager().isLastMeasure(measure));

					//Si se supero el ancho maximo, bajo de linea
					if(this.out.getPosX() > MAX_LINE_LENGTH){
						break;
					}
				}
				//Cierro los compases
				this.out.drawBarSegment();
				this.out.nextLine();
			}
			this.out.nextLine();
		}
		this.out.nextLine();
	}

	private int getMaxOutLength(TGBeat beat, TGBeat nextBeat, int stringCount) {
		int maxOutLength=(beat.getChord() !=null ? beat.getChord().getName().length() : 0);
		for(int checkedString = 0; checkedString < stringCount;checkedString++){
			int checkedOutLength=this.out.drawNote(
					this.manager.getMeasureManager().getNote(beat, checkedString+1),
					(nextBeat != null ? this.manager.getMeasureManager().getNote(nextBeat, checkedString+1) : null),
					false);
			if (checkedOutLength>maxOutLength) {
				maxOutLength=checkedOutLength;
			}
		}
		return maxOutLength;
	}

	private void drawMeasure(TGMeasure measure, int currentString, int stringCount){
		//Abro el compas
		this.out.drawBarSegment();
		this.out.drawStringSegments(1);
		TGBeat beat = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
		while(beat != null){
			int outLength = 0;

			TGBeat nextBeat = this.manager.getMeasureManager().getNextBeat( measure.getBeats() , beat);
			TGNote nextNote=(nextBeat != null ? this.manager.getMeasureManager().getNote(nextBeat, currentString+1) : null);

			TGNote note = this.manager.getMeasureManager().getNote(beat, currentString+1);
			outLength=this.out.drawNote(note, nextNote, true);
			int maxOutLength=getMaxOutLength(beat, nextBeat, stringCount);

			//Agrego espacios correspondientes hasta el proximo pulso.
			long length = (nextBeat != null ? nextBeat.getStart() - beat.getStart() : (measure.getStart() + measure.getLength()) - beat.getStart());
			int beatWidth=getDurationScaping(length);
			if (beatWidth<=maxOutLength) {
				beatWidth=maxOutLength+1;
			}
			this.out.drawStringSegments(beatWidth - outLength);

			beat = nextBeat;
		}
	}

	private int getDurationScaping(long length){
		int spacing = 6;
		if(length <= (TGDuration.QUARTER_TIME / 8)){
			spacing = 1;
		}
		else if(length <= (TGDuration.QUARTER_TIME / 4)){
			spacing = 2;
		}
		else if(length <= (TGDuration.QUARTER_TIME / 2)){
			spacing = 3;
		}
		else if(length <= TGDuration.QUARTER_TIME){
			spacing = 4;
		}
		else if(length <= (TGDuration.QUARTER_TIME * 2)){
			spacing = 5;
		}
		return spacing;
	}
}
