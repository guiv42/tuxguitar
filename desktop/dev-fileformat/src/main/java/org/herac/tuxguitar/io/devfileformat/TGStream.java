package org.herac.tuxguitar.io.devfileformat;

import java.util.HashMap;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.util.TGVersion;

/* this class hosts everything common to read/write tg file operations */

public class TGStream {
	
	public static final String MODULE_NAME = "dev-fileformat";

	protected static final TGVersion FILE_FORMAT_TGVERSION = new TGVersion(2,0,0);
	
	public static final String TG_FORMAT_NAME = ("TuxGuitar File Format");
	public static final String TG_FORMAT_VERSION = (TG_FORMAT_NAME + " - " + FILE_FORMAT_TGVERSION.getVersion() );
	public static final String TG_FORMAT_CODE = ("xml");
	// force format recognition through content only
	// to force xml input to be checked against xsd before any attempt to decode it
	public static final TGFileFormat TG_FORMAT = new TGFileFormat("TuxGuitar 2.0", "application/x-tuxguitar", new String[]{ TG_FORMAT_CODE }, true, false, false);

	// XML tags and values of enums
	protected static final String TAG_TGFile = "TuxGuitarFile";
	protected static final String TAG_TGSONG = "TGSong" ;
	protected static final String TAG_NAME = "name";
	protected static final String TAG_ARTIST= "artist";
	protected static final String TAG_ALBUM = "album";
	protected static final String TAG_AUTHOR = "author";
	protected static final String TAG_DATE = "date";
	protected static final String TAG_COPYRIGHT = "copyright";
	protected static final String TAG_WRITER = "writer";
	protected static final String TAG_TRANSCRIBER = "transcriber";
	protected static final String TAG_COMMENTS = "comments";
	protected static final String TAG_CHANNEL = "TGChannel";
	protected static final String TAG_ID = "id";
	protected static final String TAG_BANK = "bank";
	protected static final String TAG_PROGRAM = "program";
	protected static final String TAG_VOLUME = "volume";
	protected static final String TAG_BALANCE = "balance";
	protected static final String TAG_CHORUS = "chorus";
	protected static final String TAG_REVERB = "reverb";
	protected static final String TAG_PHASER = "phaser";
	protected static final String TAG_TREMOLO = "tremolo";
	protected static final String TAG_CHANNEL_PARAMETER = "TGChannelParameter";
	protected static final String TAG_KEY = "key";
	protected static final String TAG_VALUE = "value";
	protected static final String TAG_MEASURE_HEADER = "TGMeasureHeader";
	protected static final String TAG_TIME_SIGNATURE = "timeSignature";
	protected static final String TAG_NUMERATOR = "numerator";
	protected static final String TAG_DENOMINATOR = "denominator";
	protected static final String TAG_TEMPO = "tempo";
	protected static final String TAG_REPEAT_OPEN = "repeatOpen";
	protected static final String TAG_REPEAT_CLOSE = "repeatClose";
	protected static final String TAG_REPEAT_ALTERNATIVE = "repeatAlternative";
	protected static final String TAG_ALTERNATIVE = "alternative";
	protected static final String TAG_MARKER = "marker";
	protected static final String TAG_COLOR_R = "R";
	protected static final String TAG_COLOR_G = "G";
	protected static final String TAG_COLOR_B = "B";
	protected static final String TAG_TRIPLET_FEEL = "tripletFeel";
	protected static final String TAG_TGTRACK = "TGTrack";
	protected static final String TAG_SOLOMUTE = "soloMute";
	protected static final String VAL_SOLO = "solo";
	protected static final String VAL_MUTE = "mute";
	protected static final String TAG_CHANNELID = "channelId";
	protected static final String TAG_OFFSET = "offset";
	protected static final String TAG_COLOR = "color";
	protected static final String TAG_TGSTRING = "TGString";
	protected static final String TAG_TGLYRIC = "TGLyric";
	protected static final String TAG_FROM = "from";
	protected static final String TAG_TGMEASURE = "TGMeasure";
	protected static final String TAG_CLEF = "clef";
	protected static final String TAG_KEYSIGNATURE = "keySignature";
	protected static final String TAG_TGBEAT = "TGBeat";
	protected static final String TAG_START = "start";
	protected static final String TAG_STROKE = "stroke";
	protected static final String TAG_DIRECTION = "direction";
	protected static final String TAG_CHORD = "chord";
	protected static final String TAG_STRING = "string";
	protected static final String TAG_FIRSTFRET = "firstFret";
	protected static final String TAG_TEXT = "text";
	protected static final String TAG_VOICE = "voice";
	protected static final String TAG_DURATION = "duration";
	protected static final String TAG_DOTTED = "dotted";
	protected static final String VAL_DOTTED = "dotted";
	protected static final String VAL_DOUBLEDOTTED = "doubleDotted";
	protected static final String TAG_DIVISIONTYPE = "divisionType";
	protected static final String TAG_ENTERS = "enters";
	protected static final String TAG_TIMES = "times";
	protected static final String TAG_NOTE = "note";
	protected static final String TAG_VELOCITY = "velocity";
	protected static final String TAG_TIEDNOTE = "tiedNote";
	protected static final String TAG_VIBRATO ="vibrato";
	protected static final String TAG_DEADNOTE = "deadNote";
	protected static final String TAG_SLIDE = "slide";
	protected static final String TAG_HAMMER = "hammer";
	protected static final String TAG_GHOSTNOTE = "ghostNote";
	protected static final String TAG_ACCENTUATEDNOTE = "accentuatedNote";
	protected static final String TAG_HEAVYACCENTUATEDNOTE = "heavyAccentuatedNote";
	protected static final String TAG_PALMMUTE = "palmMute";
	protected static final String TAG_STACCATO = "staccato";
	protected static final String TAG_TAPPING = "tapping";
	protected static final String TAG_SLAPPING = "slapping";
	protected static final String TAG_POPPING = "popping";
	protected static final String TAG_FADEIN = "fadeIn";
	protected static final String TAG_LETRING = "letRing";
	protected static final String TAG_BEND = "bend";
	protected static final String TAG_TREMOLOBAR = "tremoloBar";
	protected static final String TAG_HARMONIC = "harmonic";
	protected static final String TAG_GRACE = "grace";
	protected static final String TAG_TRILL = "trill";
	protected static final String TAG_TREMOLOPICKING = "tremoloPicking";
	protected static final String TAG_POINT = "point";
	protected static final String TAG_POSITION = "position";
	protected static final String TAG_TYPE = "type";
	protected static final String TAG_DATA = "data";
	protected static final String TAG_FRET = "fret";
	protected static final String TAG_DYNAMIC = "dynamic";
	protected static final String TAG_ONBEAT = "onBeat";
	protected static final String TAG_DEAD = "dead";
	protected static final String TAG_TRANSITION = "transition";

	protected HashMap<String, Integer> tripletsReadMap;
	protected HashMap<Integer, String> tripletsWriteMap;
	protected HashMap<String, Integer> mapReadClefs;
	protected HashMap<Integer, String> mapWriteClefs;
	protected HashMap<String, Integer> mapReadStroke;
	protected HashMap<Integer, String> mapWriteStroke;
	protected HashMap<String, Integer> mapReadDirection;
	protected HashMap<Integer, String> mapWriteDirection;
	protected HashMap<String, Integer> harmonicReadMap;
	protected HashMap<Integer, String> harmonicWritedMap;
	protected HashMap<String, Integer> mapReadTransition;
	protected HashMap<Integer, String> mapWriteTransition;



	public TGStream() {
		this.tripletsReadMap = new HashMap<String,Integer>();
		this.tripletsWriteMap = new HashMap<Integer, String>();
		this.tripletsReadMap.put("none", TGMeasureHeader.TRIPLET_FEEL_NONE);
		this.tripletsWriteMap.put(TGMeasureHeader.TRIPLET_FEEL_NONE, "none");
		this.tripletsReadMap.put("eighth", TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		this.tripletsWriteMap.put(TGMeasureHeader.TRIPLET_FEEL_EIGHTH, "eighth");
		this.tripletsReadMap.put("sixteenth", TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		this.tripletsWriteMap.put(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, "sixteenth");

		this.mapReadClefs = new HashMap<String, Integer>();
		this.mapWriteClefs = new HashMap<Integer, String>();
		this.mapReadClefs.put("treble", TGMeasure.CLEF_TREBLE);
		this.mapWriteClefs.put(TGMeasure.CLEF_TREBLE, "treble");
		this.mapReadClefs.put("bass", TGMeasure.CLEF_BASS);
		this.mapWriteClefs.put(TGMeasure.CLEF_BASS, "bass");
		this.mapReadClefs.put("tenor", TGMeasure.CLEF_TENOR);
		this.mapWriteClefs.put(TGMeasure.CLEF_TENOR, "tenor");
		this.mapReadClefs.put("alto", TGMeasure.CLEF_ALTO);
		this.mapWriteClefs.put(TGMeasure.CLEF_ALTO, "alto");
		
		this.mapReadStroke = new HashMap<String, Integer>();
		this.mapWriteStroke = new HashMap<Integer, String>();
		this.mapReadStroke.put("none", TGStroke.STROKE_NONE);
		this.mapWriteStroke.put(TGStroke.STROKE_NONE, "none");
		this.mapReadStroke.put("up", TGStroke.STROKE_UP);
		this.mapWriteStroke.put(TGStroke.STROKE_UP, "up");
		this.mapReadStroke.put("down", TGStroke.STROKE_DOWN);
		this.mapWriteStroke.put(TGStroke.STROKE_DOWN,"down");
		
		this.mapReadDirection = new HashMap<String, Integer>();
		this.mapWriteDirection = new HashMap<Integer, String>();
		this.mapReadDirection.put("up", TGVoice.DIRECTION_UP);
		this.mapWriteDirection.put(TGVoice.DIRECTION_UP, "up");
		this.mapReadDirection.put("down", TGVoice.DIRECTION_DOWN);
		this.mapWriteDirection.put(TGVoice.DIRECTION_DOWN, "down");
		
		this.harmonicReadMap = new HashMap<String, Integer>();
		this.harmonicWritedMap = new HashMap<Integer, String>();
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_NATURAL, TGEffectHarmonic.TYPE_NATURAL);
		this.harmonicWritedMap.put(TGEffectHarmonic.TYPE_NATURAL, TGEffectHarmonic.KEY_NATURAL);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_ARTIFICIAL, TGEffectHarmonic.TYPE_ARTIFICIAL);
		this.harmonicWritedMap.put(TGEffectHarmonic.TYPE_ARTIFICIAL, TGEffectHarmonic.KEY_ARTIFICIAL);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_PINCH, TGEffectHarmonic.TYPE_PINCH);
		this.harmonicWritedMap.put(TGEffectHarmonic.TYPE_PINCH, TGEffectHarmonic.KEY_PINCH);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_SEMI, TGEffectHarmonic.TYPE_SEMI);
		this.harmonicWritedMap.put(TGEffectHarmonic.TYPE_SEMI, TGEffectHarmonic.KEY_SEMI);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_TAPPED, TGEffectHarmonic.TYPE_TAPPED);
		this.harmonicWritedMap.put(TGEffectHarmonic.TYPE_TAPPED, TGEffectHarmonic.KEY_TAPPED);
		
		this.mapReadTransition = new HashMap<String, Integer>();
		this.mapWriteTransition = new HashMap<Integer, String>();
		this.mapReadTransition.put("none", TGEffectGrace.TRANSITION_NONE);
		this.mapWriteTransition.put(TGEffectGrace.TRANSITION_NONE, "none");
		this.mapReadTransition.put("slide", TGEffectGrace.TRANSITION_SLIDE);
		this.mapWriteTransition.put(TGEffectGrace.TRANSITION_SLIDE, "slide");
		this.mapReadTransition.put("bend", TGEffectGrace.TRANSITION_BEND);
		this.mapWriteTransition.put(TGEffectGrace.TRANSITION_BEND, "bend");
		this.mapReadTransition.put("hammer", TGEffectGrace.TRANSITION_HAMMER);
		this.mapWriteTransition.put(TGEffectGrace.TRANSITION_HAMMER, "hammer");
	}
	
	// xml to tg: 64->1 (default), 32->2, 16->3
	protected int readGraceDuration(int duration) {
		if (duration==16) return 3;
		if (duration==32) return 2;
		return 1;
	}
	// tg to xml
	protected int writeGraceDuration(int duration) {
		if (duration==3) return 16;
		if (duration==2) return 32;
		return 64;
	}

	
	protected class PositionValue {
		private int position;
		private int value;
		protected PositionValue(int pos, int val) {
			this.position = pos;
			this.value=val;
		}
		protected int getPosition() {
			return this.position;
		}
		protected int getValue() {
			return this.value;
		}
	}
	
	
}
