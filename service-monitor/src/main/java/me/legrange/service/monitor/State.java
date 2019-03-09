package me.legrange.service.monitor;

import java.util.List;
import static java.lang.String.format;

/**
 *
 * @author gideon
 */
public class State {

    private final Status status;
    private final String message;
    private final long errors;
    private final long warnings;
    private final List<Measurement> data;

    public State(List<Measurement> data) {
        this(null, null, data);
    }

    public State(Status status, String message, List<Measurement> data) {
        this.data = data;
        errors = data.stream().filter(state -> state.getStatus() == Status.ERROR).count();
        warnings = data.stream().filter(state -> state.getStatus() == Status.WARNING).count();
        Status _status = Status.OK;
        if (message == null) {
            if (errors > 0) {
                if (warnings > 0) {
                    message = format("%d error%s and %d warning%s", errors, (errors > 1) ? "s" : "", warnings, (warnings > 1) ? "s" : "");
                } else {
                    message = format("%d error%s", errors, (errors > 1) ? "s" : "");
                }
                _status = Status.ERROR;
            } else if (warnings > 0) {
                message = format("%d warning%s", warnings, (warnings > 1) ? "s" : "");
                _status = Status.WARNING;

            } else {
                _status = Status.OK;
            }
            this.message = message;
        }
        else {
           this.message = message;
            _status = Status.OK;
        }
        if (status == null) {
            this.status = _status;
        }
        else {
            this.status = status;
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Measurement> getData() {
        return data;
    }


}
