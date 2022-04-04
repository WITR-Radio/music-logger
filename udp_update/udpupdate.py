#!/usr/bin/python
# -*- coding: utf-8 -*-
"""
UDP and Icecast Updater
**Version:** 2.0
**Author:** Chris Cmolik and WITR Radio (witr.rit.edu)

Usage
==

Send a datagram (UDP packet) containing "ARTIST --- SONG" to port 9999, substituting ARTIST with the ARTIST name and SONG with the song name.


Lo-Fi Changelog
==

* 2013-11-18 (tmk1587/Max Kelley) Updated the list of specialty show categories in post_logger
* 2018-06-01 (mxc9798/Mike Canning) Pushing newer version in use in Studio X with a few bug fixes
* 2020-05-27 (wel2138/William Leuschner) Update to modern Python coding standards, fix logger URL, use a proper XML generator, use requests instead of urllib directly
* 2022-04-03 (aty3425/Adam Yarris) Removed icecast/rds updating (to be moved to the logger), and changed logger posting to JSON
"""
import syslog
import requests
from twisted.internet.protocol import DatagramProtocol
from twisted.internet import reactor

UNDERGROUND = False

##Globals - CHANGE THESE.##
if UNDERGROUND:
    LOGGER2_FULL_URL = "https://moonbounce.rit.edu/tracks/broadcast?underground=true"
else:
    LOGGER2_FULL_URL = "https://moonbounce.rit.edu/tracks/broadcast"
LAST_SONG = ""

def post_logger(artist, song, group):
    response = requests.post(LOGGER2_FULL_URL, json={
        "artist": artist,
        "track": song,
        "group": group,
    })

    syslog.syslog("Got response from logger (%d): %s" % (response.status_code, response.text))


# TODO: This really should be rewritten
class UDPListener(DatagramProtocol):
    def datagramReceived(self, data, foo):
        global LAST_SONG
        (host, port) = foo
        syslog.syslog("received %r from %s:%d" % (data, host, port))
        stripdata = data.split()
        # This nasty bit actually seperates the song from the artist by the ---
        end_of_artist = 0
        done = False
        artdone = False
        ARTIST = ""
        SONG = ""
        GROUP = ""
        end_of_song = 0
        for i in range(0, len(stripdata) - 1):
            if stripdata[i] == "---":
                done = True
                end_of_artist = i
            if not done:
                ARTIST += " "
                ARTIST += stripdata[i]
        for j in range(end_of_artist + 1, len(stripdata)):
            if stripdata[j] == "::":
                artdone = True
                end_of_song = j
            if not artdone:
                SONG += " "
                SONG += stripdata[j]
        GROUPlist = stripdata[end_of_song + 1 :]
        for item in GROUPlist:
            GROUP += item
        ARTIST = ARTIST[1:].replace("\0", "")
        SONG = SONG[1:].replace("\0", "")
        if ((ARTIST + SONG) != LAST_SONG) and SONG.strip() and ARTIST.strip():
            # Prioritizing our local stuff before remote updates
            LAST_SONG = ARTIST + SONG
            post_logger(ARTIST, SONG, GROUP)
            # Updating the RDS with the blank gives the default text.

if __name__ == "__main__":
    syslog.syslog("RDS and Icecast UDP watcher started...")
    reactor.listenUDP(9999, UDPListener())
    reactor.run()
