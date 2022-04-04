package edu.rit.witr.musiclogger.broadcast.icecast;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * A mapping of the XML body from the {@link edu.rit.witr.musiclogger.broadcast.IcecastBroadcaster#LISTMOUNTS_URL}
 * endpoint.
 */
@XmlType
@XmlRootElement(name = "icestats")
public class IceStats {

    private List<Source> sources;

    @XmlElement(name = "source")
    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<Source> getSources() {
        return sources;
    }

    public static class Source {
        private int listeners;
        private int connected;
        private String contentType;
        private String mount;

        @XmlElement(name = "listeners")
        public void setListeners(int listeners) {
            this.listeners = listeners;
        }

        public int getListeners() {
            return listeners;
        }

        @XmlElement(name = "Connected")
        public void setConnected(int connected) {
            this.connected = connected;
        }

        public int getConnected() {
            return connected;
        }

        @XmlElement(name = "content-type")
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }

        @XmlAttribute(name = "mount", required = true)
        public void setMount(String mount) {
            this.mount = mount;
        }

        public String getMount() {
            return mount;
        }
    }
}
