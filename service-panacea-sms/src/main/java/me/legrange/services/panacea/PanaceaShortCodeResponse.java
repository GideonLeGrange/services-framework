package me.legrange.services.panacea;

import java.util.List;

/**
 *
 * @author matthewl
 */
public final class PanaceaShortCodeResponse extends PanaceaApiResponse {

    private List<ShortCodeMessage> details;

    public List<ShortCodeMessage> getDetails() {
        return details;
    }

    public void setDetails(List<ShortCodeMessage> details) {
        this.details = details;
    }

}
