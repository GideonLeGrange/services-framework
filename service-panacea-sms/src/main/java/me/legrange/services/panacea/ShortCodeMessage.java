package me.legrange.services.panacea;

import java.util.Date;

/**
 *
 * @author matthewl
 */
public final class ShortCodeMessage {

    private int id;

    private Date created;

    private String from;

    private String to;

    private String data;

    private String charset;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "ShortCodeMessage{" + "id=" + id + ", created=" + created + ", from=" + from + ", to=" + to + ", data=" + data + ", charset=" + charset + '}';
    }

}
