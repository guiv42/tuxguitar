package app.tuxguitar.song.models;

import app.tuxguitar.song.factory.TGFactory;

public abstract class TGLyric {

	private static final String REGEX = " ";

	private int from;
	private String lyrics;

	public TGLyric(){
		this.from = 1;
		this.lyrics = new String();
	}

	public int getFrom() {
		return this.from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public String getLyrics() {
		return this.lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public String[] getLyricBeats(){
		String lyrics = getLyrics();
		lyrics = lyrics.replaceAll("\n",REGEX);
		lyrics = lyrics.replaceAll("\r",REGEX);
		return lyrics.split(REGEX);
	}

	public boolean isEmpty(){
		return (getLyrics().length() == 0);
	}

	public void copyFrom(TGLyric lyric){
		this.setFrom(lyric.getFrom());
		this.setLyrics(lyric.getLyrics());
	}

	public TGLyric clone(TGFactory factory) {
		TGLyric tgLyric = factory.newLyric();
		tgLyric.copyFrom(this);
		return tgLyric;
	}
}
