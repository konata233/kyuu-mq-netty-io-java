package mq.net;

import java.util.ArrayList;
import java.util.List;

public class RoutingChain {
    private final ArrayList<Routing> routings;
    private String queueName;

    public RoutingChain() {
        this.queueName = null;
        this.routings = new ArrayList<>();
    }

    public RoutingChain add(Routing routing) {
        this.routings.add(routing);
        return this;
    }

    public ArrayList<Routing> getRoutings() {
        ArrayList<Routing> cloned = new ArrayList<>(this.routings);
        if (cloned.size() < 3) {
            cloned.add(Routing.Stop());
        }
        return this.routings;
    }

    public RoutingChain setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public String getQueueName() {
        return this.queueName;
    }
}
