RM?=rm -f
CFLAGS?=-mno-cygwin -I../../build-scripts/native-modules/common-include
LDFLAGS?=-mno-cygwin -Wl,--kill-at
LDLIBS?=-lwinmm
LDPATH?=
LIBRARY_NAME?=tuxguitar-winmm-jni
LIBRARY_PREFIX?=
LIBRARY_SUFFIX?=.dll

LIBRARY=$(LIBRARY_PREFIX)$(LIBRARY_NAME)$(LIBRARY_SUFFIX)
OBJECTS=app_tuxguitar_player_impl_midiport_winmm_MidiSystem.o

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

all:    $(LIBRARY)

$(LIBRARY):	$(OBJECTS)
	$(CC) $(LDFLAGS) -shared -o $(LIBRARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean:
	$(RM) $(OBJECTS) $(LIBRARY)
