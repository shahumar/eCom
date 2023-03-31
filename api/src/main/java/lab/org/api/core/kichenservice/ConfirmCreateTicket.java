package lab.org.api.core.kichenservice;

import io.eventuate.tram.commands.common.Command;

public class ConfirmCreateTicket implements Command {

    private Long ticketId;

    private ConfirmCreateTicket() {
    }


    public ConfirmCreateTicket(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
