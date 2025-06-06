/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 * This class is used to create AudioFloatInputStream from AudioInputStream and
 * byte buffers.
 *
 * @author Karl Helgason
 */
public abstract class AudioFloatInputStream {

    private static class BytaArrayAudioFloatInputStream
            extends AudioFloatInputStream {

        private int pos = 0;
        private int markpos = 0;
        private AudioFloatConverter converter;
        private AudioFormat format;
        private byte[] buffer;
        private int buffer_offset;
        private int buffer_len;
        private int framesize_pc;

        public BytaArrayAudioFloatInputStream(AudioFloatConverter converter,
                byte[] buffer, int offset, int len) {
            this.converter = converter;
            this.format = converter.getFormat();
            this.buffer = buffer;
            this.buffer_offset = offset;
            framesize_pc = format.getFrameSize() / format.getChannels();
            this.buffer_len = len / framesize_pc;

        }

        public AudioFormat getFormat() {
            return format;
        }

        public long getFrameLength() {
            return buffer_len;// / format.getFrameSize();
        }

        public int read(float[] b, int off, int len) throws IOException {
            if (b == null)
                throw new NullPointerException();
            if (off < 0 || len < 0 || len > b.length - off)
                throw new IndexOutOfBoundsException();
            if (pos >= buffer_len)
                return -1;
            if (len == 0)
                return 0;
            if (pos + len > buffer_len)
                len = buffer_len - pos;
            converter.toFloatArray(buffer, buffer_offset + pos * framesize_pc,
                    b, off, len);
            pos += len;
            return len;
        }

        public long skip(long len) throws IOException {
            if (pos >= buffer_len)
                return -1;
            if (len <= 0)
                return 0;
            if (pos + len > buffer_len)
                len = buffer_len - pos;
            pos += len;
            return len;
        }

        public int available() throws IOException {
            return buffer_len - pos;
        }

        public void close() throws IOException {
        }

        public void mark(int readlimit) {
            markpos = pos;
        }

        public boolean markSupported() {
            return true;
        }

        public void reset() throws IOException {
            pos = markpos;
        }
    }

    private static class DirectAudioFloatInputStream
            extends AudioFloatInputStream {

        private AudioInputStream stream;
        private AudioFloatConverter converter;
        private int framesize_pc; // framesize / channels
        private byte[] buffer;

        public DirectAudioFloatInputStream(AudioInputStream stream) {
            converter = AudioFloatConverter.getConverter(stream.getFormat());
            if (converter == null) {
                AudioFormat format = stream.getFormat();
                AudioFormat newformat;

                AudioFormat[] formats = AudioSystem.getTargetFormats(
                        AudioFormat.Encoding.PCM_SIGNED, format);
                if (formats.length != 0) {
                    newformat = formats[0];
                } else {
                    float samplerate = format.getSampleRate();
                    int samplesizeinbits = format.getSampleSizeInBits();
                    int framesize = format.getFrameSize();
                    float framerate = format.getFrameRate();
                    samplesizeinbits = 16;
                    framesize = format.getChannels() * (samplesizeinbits / 8);
                    framerate = samplerate;

                    newformat = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED, samplerate,
                            samplesizeinbits, format.getChannels(), framesize,
                            framerate, false);
                }

                stream = AudioSystem.getAudioInputStream(newformat, stream);
                converter = AudioFloatConverter.getConverter(stream.getFormat());
            }
            framesize_pc = stream.getFormat().getFrameSize()
                    / stream.getFormat().getChannels();
            this.stream = stream;
        }

        public AudioFormat getFormat() {
            return stream.getFormat();
        }

        public long getFrameLength() {
            return stream.getFrameLength();
        }

        public int read(float[] b, int off, int len) throws IOException {
            int b_len = len * framesize_pc;
            if (buffer == null || buffer.length < b_len)
                buffer = new byte[b_len];
            int ret = stream.read(buffer, 0, b_len);
            if (ret == -1)
                return -1;
            converter.toFloatArray(buffer, b, off, ret / framesize_pc);
            return ret / framesize_pc;
        }

        public long skip(long len) throws IOException {
            long b_len = len * framesize_pc;
            long ret = stream.skip(b_len);
            if (ret == -1)
                return -1;
            return ret / framesize_pc;
        }

        public int available() throws IOException {
            return stream.available() / framesize_pc;
        }

        public void close() throws IOException {
            stream.close();
        }

        public void mark(int readlimit) {
            stream.mark(readlimit * framesize_pc);
        }

        public boolean markSupported() {
            return stream.markSupported();
        }

        public void reset() throws IOException {
            stream.reset();
        }
    }

    public static AudioFloatInputStream getInputStream(URL url)
            throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem
                .getAudioInputStream(url));
    }

    public static AudioFloatInputStream getInputStream(File file)
            throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem
                .getAudioInputStream(file));
    }

    public static AudioFloatInputStream getInputStream(InputStream stream)
            throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem
                .getAudioInputStream(stream));
    }

    public static AudioFloatInputStream getInputStream(
            AudioInputStream stream) {
        return new DirectAudioFloatInputStream(stream);
    }

    public static AudioFloatInputStream getInputStream(AudioFormat format,
            byte[] buffer, int offset, int len) {
        AudioFloatConverter converter = AudioFloatConverter
                .getConverter(format);
        if (converter != null)
            return new BytaArrayAudioFloatInputStream(converter, buffer,
                    offset, len);

        InputStream stream = new ByteArrayInputStream(buffer, offset, len);
        long aLen = format.getFrameSize() == AudioSystem.NOT_SPECIFIED
                ? AudioSystem.NOT_SPECIFIED : len / format.getFrameSize();
        AudioInputStream astream = new AudioInputStream(stream, format, aLen);
        return getInputStream(astream);
    }

    public abstract AudioFormat getFormat();

    public abstract long getFrameLength();

    public abstract int read(float[] b, int off, int len) throws IOException;

    public int read(float[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public float read() throws IOException {
        float[] b = new float[1];
        int ret = read(b, 0, 1);
        if (ret == -1 || ret == 0)
            return 0;
        return b[0];
    }

    public abstract long skip(long len) throws IOException;

    public abstract int available() throws IOException;

    public abstract void close() throws IOException;

    public abstract void mark(int readlimit);

    public abstract boolean markSupported();

    public abstract void reset() throws IOException;
}
